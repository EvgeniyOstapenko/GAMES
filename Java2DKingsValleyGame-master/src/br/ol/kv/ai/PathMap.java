package br.ol.kv.ai;

import br.ol.kv.physics.Terrain;
import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * PathMap class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PathMap {
    
    public static final int RIGHT_DIRECTION = 1;
    public static final int RIGHT_DOWN_DIRECTION = 2;
    public static final int DOWN_DIRECTION = 4;
    public static final int LEFT_DOWN_DIRECTION = 8;
    public static final int LEFT_DIRECTION = 16;
    public static final int LEFT_UP_DIRECTION = 32;
    public static final int UP_DIRECTION = 64;
    public static final int RIGHT_UP_DIRECTION = 128;
    
    public class Node {
        public int id;
        public boolean jumpLeft;
        public boolean jumpRight;

        /**
         * 76543210 bits
         * 
         *  5 6 7
         *   \|/ 
         * 4 -+- 0
         *   /|\
         *  3 2 1         
         */
        public int allowedDirections;
        
        public boolean isDirectionAllowed(int direction) {
            return (direction & allowedDirections) == direction;
        }
        
        public void allowDirection(int direction) {
            allowedDirections = allowedDirections | direction;
        }

    }
    
    private Terrain terrain;
    private int cols;
    private int rows;
    private Node[][] map; // [row][col]

    private final List<Point> platformLocations = new ArrayList<>();
    
    private final List<PathPattern> patterns = new ArrayList<>();

    public PathMap() {
        loadPatterns();
    }

    private void loadPatterns() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream is = getClass().getResourceAsStream("/res/path/path_patterns.xml");
            Document d = db.parse(is);
            NodeList nodes = d.getElementsByTagName("pattern");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element node = (Element) nodes.item(i);
                String terrainStr = node.getElementsByTagName("terrain").item(0).getTextContent().trim();
                String directionsStr = node.getElementsByTagName("directions").item(0).getTextContent().trim();
                String jumpsStr = node.getElementsByTagName("jumps").item(0).getTextContent().trim();
                patterns.add(new PathPattern(terrainStr, directionsStr, jumpsStr));
            }
        }
        catch (Exception ex) {
            Logger.getLogger(PathMap.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    public Terrain getTerrain() {
        return terrain;
    }

    public Node[][] getMap() {
        return map;
    }
    
    public Node get(int col, int row) {
        if (terrain.isOutOfBounds(col, row)) {
            return null;
        }
        return map[row][col];
    }

    public List<Point> getPlatformLocations() {
        return platformLocations;
    }

    public Point getRandomPlatformLocation() {
        int randomIndex = (int) (Math.random() * platformLocations.size());
        return platformLocations.get(randomIndex);
    }
    
    public void create(Terrain terrain) {
        this.terrain = terrain;
        cols = terrain.getCols();
        rows = terrain.getRows();
        map = new Node[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                map[row][col] = new Node();
            }
        }
        analyzeTerrain();
    }

    private void analyzeTerrain() {
        Node node = null;
        // clear previous analysis
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                map[row][col].id = 0;
                map[row][col].allowedDirections = 0;
                map[row][col].jumpLeft = false;
                map[row][col].jumpRight = false;
            }
        }
        
        // patterns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (PathPattern pattern : patterns) {
                    pattern.analyze(terrain, this, col, row);
                }
            }
        }

        // platform positions
        platformLocations.clear();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!terrain.isRigidByGrid(col, row, 0)
                        && !terrain.isRigidByGrid(col, row - 1, 0)
                        && !terrain.isRigidByGrid(col, row - 2, 0)
                        && terrain.isRigidByGrid(col, row + 1, 0)) {
                    platformLocations.add(new Point(col, row));
                }
            }
        }
        
        // stair \
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (terrain.getIdByGrid(col, row) == 2) {
                    node = map[row - 1][col];
                    node.allowDirection(LEFT_UP_DIRECTION);
                    int offsetX = -1;
                    int offsetY = -1;
                    while (terrain.getIdByGrid(col + offsetX, row + offsetY) != 4) {
                        node = map[row + offsetY - 1][col + offsetX];
                        node.id = 9;
                        node.allowDirection(LEFT_UP_DIRECTION);
                        node.allowDirection(RIGHT_DOWN_DIRECTION);
                        offsetX--;
                        offsetY--;
                    }
                    node = map[row + offsetY - 1][col + offsetX];
                    node.allowDirection(RIGHT_DOWN_DIRECTION);
                }
            }
        }
        
        // stair /
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (terrain.getIdByGrid(col, row) == 5) {
                    node = map[row - 1][col];
                    node.allowDirection(RIGHT_UP_DIRECTION);
                    int offsetX = 1;
                    int offsetY = -1;
                    while (terrain.getIdByGrid(col + offsetX, row + offsetY) != 7) {
                        node = map[row + offsetY - 1][col + offsetX];
                        node.id = 9;
                        node.allowDirection(RIGHT_UP_DIRECTION);
                        node.allowDirection(LEFT_DOWN_DIRECTION);
                        offsetX++;
                        offsetY--;
                    }
                    node = map[row + offsetY - 1][col + offsetX];
                    node.allowDirection(LEFT_DOWN_DIRECTION);
                }
            }
        }
        
        // fall left
        for (int row = 0; row < rows; row++) {
            fallLeftNextCol:
            for (int col = 0; col < cols; col++) {
                int terrainId = terrain.getIdByGrid(col, row);
                int terrainIdRight = terrain.getIdByGrid(col + 1, row);
                int terrainIdRightUp = terrain.getIdByGrid(col + 1, row - 1);
                int terrainIdUp = terrain.getIdByGrid(col, row - 1);
                int terrainIdDown1 = terrain.getIdByGrid(col, row + 1);
                int terrainIdDown2 = terrain.getIdByGrid(col, row + 2);
                if (valuesContain(terrainId, 0, 9) 
                        && valuesContain(terrainIdRight, 1, 8)
                        && valuesContain(terrainIdRightUp, 0, 9) 
                        && valuesContain(terrainIdUp, 0, 9) 
                        && valuesContain(terrainIdDown1, 0, 9)
                        && valuesContain(terrainIdDown2, 0, 9)) {
                    int offsetY = 0;
                    // if collides with stair, exit
                    while (!terrain.isRigidByGrid(col, row + offsetY, 0)) {
                        int stairCandidateId = terrain.getIdByGrid(col, row + offsetY);
                        if (valuesContain(stairCandidateId, 2, 3, 5, 6)) {
                            continue fallLeftNextCol;
                        }
                        offsetY++;
                    }
                    offsetY = 0;
                    while ((node = map[row + offsetY][col]).id != 9) {
                        node.id = 9;
                        if (offsetY == 0) {
                            map[row - 1][col + 1].allowDirection(LEFT_DOWN_DIRECTION);
                            node.allowDirection(RIGHT_UP_DIRECTION);
                        }
                        node.allowDirection(UP_DIRECTION);
                        offsetY++;
                    }
                    node.allowDirection(UP_DIRECTION);
                }
            }
        }
        
        // fall right
        for (int row = 0; row < rows; row++) {
            fallRightNextCol:
            for (int col = 0; col < cols; col++) {
                int terrainId = terrain.getIdByGrid(col, row);
                int terrainIdLeft = terrain.getIdByGrid(col - 1, row);
                int terrainIdLeftUp = terrain.getIdByGrid(col - 1, row - 1);
                int terrainIdUp = terrain.getIdByGrid(col, row - 1);
                int terrainIdDown1 = terrain.getIdByGrid(col, row + 1);
                int terrainIdDown2 = terrain.getIdByGrid(col, row + 2);
                if (valuesContain(terrainId, 0, 9) 
                        && valuesContain(terrainIdLeft, 1, 8)
                        && valuesContain(terrainIdUp, 0, 9) 
                        && valuesContain(terrainIdLeftUp, 0, 9) 
                        && valuesContain(terrainIdDown1, 0, 9)
                        && valuesContain(terrainIdDown2, 0, 9)) {
                    int offsetY = 0;
                    // if collides with stair, exit
                    while (!terrain.isRigidByGrid(col, row + offsetY, 0)) {
                        int stairCandidateId = terrain.getIdByGrid(col, row + offsetY);
                        if (valuesContain(stairCandidateId, 2, 3, 5, 6)) {
                            continue fallRightNextCol;
                        }
                        offsetY++;
                    }
                    offsetY = 0;
                    while ((node = map[row + offsetY][col]).id != 9) {
                        node.id = 9;
                        if (offsetY == 0) {
                            map[row - 1][col - 1].allowDirection(RIGHT_DOWN_DIRECTION);
                            node.allowDirection(LEFT_UP_DIRECTION);
                        }
                        node.allowDirection(UP_DIRECTION);
                        offsetY++;
                    }
                    node.allowDirection(UP_DIRECTION);
                }
            }
        }        
        
    }
    
    private boolean valuesContain(int v1, int ... values) {
        for (int v2 : values) {
            if (v1 == v2) {
                return true;
            }
        }
        return false;
    }
    
    public void dig(int col, int row) {
        terrain.set(col, row, 0);
        if (terrain.getIdByGrid(col, row + 1) == 1) {
            terrain.set(col, row + 1, 0);
        }
        analyzeTerrain();
    }
    
}
