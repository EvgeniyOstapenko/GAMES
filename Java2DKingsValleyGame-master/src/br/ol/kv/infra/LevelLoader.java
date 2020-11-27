package br.ol.kv.infra;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 * LevelLoader class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class LevelLoader {
    
    public static final char ENTITY_TREASURE = 'T';
    public static final char ENTITY_PICKAXE = 'P';
    public static final char ENTITY_KNIFE = 'K';
    
    public static final char ENTITY_DOOR_PLAYER_START = 'S';
    public static final char ENTITY_DOOR_NEXT_LEVEL = 'N';
    public static final char ENTITY_DOOR_PREVIOUS_LEVEL = 'B';
    public static final char ENTITY_DOOR = 'D'; // = S, N
    
    public static final char ENTITY_GATE = 'G';
    
    public static final char ENTITY_ENEMY_1 = 'Q';
    public static final char ENTITY_ENEMY_2 = 'W';
    
    private DocumentBuilderFactory dbf;
    private DocumentBuilder db;
    
    private int cols;
    private int rows;
    private String brickColor;
    private String terrain;
    private String entities;
    
    public LevelLoader() {
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public String getBrickColor() {
        return brickColor;
    }

    public String getTerrain() {
        return terrain;
    }
    
    public String getEntities() {
        return entities;
    }
    
    public void load(String resource) {
        try {
            InputStream is = LevelLoader.class.getResourceAsStream("/res/level/" + resource);
            Document d = db.parse(is);
            rows = Integer.parseInt(d.getElementsByTagName("rows").item(0).getTextContent());
            cols = Integer.parseInt(d.getElementsByTagName("cols").item(0).getTextContent());
            brickColor = d.getElementsByTagName("brickColor").item(0).getTextContent().trim();
            terrain = d.getElementsByTagName("terrain").item(0).getTextContent().trim();
            entities = d.getElementsByTagName("entities").item(0).getTextContent().trim();
        }
        catch (Exception ex) {
            Logger.getLogger(LevelLoader.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    /**
     * entityType: 'T' = treasure
     *           'P' = pickaxe
     *           'K' = knife
     * 
     *           'S' = door start player
     *           'N' = door next level
     *           'B' = door previous level
     *           'D' = door start player & next level
     * 
     *           'G' = gate
     * 
     *           'Q' = enemy type 1 (random move)
     *           'W' = enemy type 2 (chase player)
     */
    public List<Point> getAllEntities(char entityType) {
        List<Point> entitiesList = new ArrayList<>();
        String[] lines = entities.split("\n");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (lines[row].charAt(col) == entityType) {
                    entitiesList.add(new Point(col, row));
                }
            }
        }        
        return entitiesList;
    }

}
