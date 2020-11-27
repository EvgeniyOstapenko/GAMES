package br.ol.kv.ai;

import br.ol.kv.ai.PathMap.Node;
import br.ol.kv.entity.Enemy;
import br.ol.kv.physics.Body;
import br.ol.kv.physics.Terrain;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * PathFinder class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PathFinder {

    /**
     *  5 6 7
     *   \|/ 
     * 4 -+- 0
     *   /|\
     *  3 2 1         
     */
    public static final Point[] DIRECTION_VECTORS = { 
        new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(-1, 1),
        new Point(-1, 0), new Point(-1, -1), new Point(0, -1), new Point(1, -1) };
    
    private final PathMap map;
    
    private int cols, rows;
    private int[][] distances; // [row][col]
    
    private final List<Enemy> blockingEnemies = new ArrayList<>();
    private boolean blockingEnemiesEnabled = true;
    
    public PathFinder(PathMap map) {
        this.map = map;
        createInitialCachedPoints();
        create();
    }

    private void create() {
        Terrain terrain = map.getTerrain();
        cols = terrain.getCols();
        rows = terrain.getRows();
        distances = new int[rows][cols];
    }
    
    private void clearDistances() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                distances[row][col] = 0;
            }
        }    
    }

    public PathMap getMap() {
        return map;
    }

    public void addBlockingEnemy(Enemy enemy) {
        blockingEnemies.add(enemy);
    }

    public boolean isBlockingEnemiesEnabled() {
        return blockingEnemiesEnabled;
    }

    public void setBlockingEnemiesEnabled(boolean blockingEnemiesEnabled) {
        this.blockingEnemiesEnabled = blockingEnemiesEnabled;
    }
    
    private boolean blockedByEnemies(int col, int row) {
        for (Enemy enemy : blockingEnemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            Body body = enemy.getBody();
            body.updateBounds();
            int bc = body.getMiddleXBound() / Terrain.TILE_SIZE;
            int br = body.getBottomBound() / Terrain.TILE_SIZE;
            if (col == bc && row == br) {
                return true;
            }
        }
        return false;
    }
    
    // points cache
    
    private static final int INITIAL_POINTS_CACHE_SIZE = 100;
    private final List<Point> pointsCache = new ArrayList<>();
    
    private void createInitialCachedPoints() {
        for (int i = 0; i < INITIAL_POINTS_CACHE_SIZE; i++) {
            pointsCache.add(new Point());
        }
    }

    private void savePointsToCache(List<Point> points) {
        pointsCache.addAll(points);
        points.clear();
    }

    private Point getCachedPoint(int col, int row) {
        Point point = null;
        if (pointsCache.isEmpty()) {
            point = new Point(col, row);
        }
        else {
            point = pointsCache.remove(pointsCache.size() - 1);
            point.setLocation(col, row);
        }
        return point;
    }
    
    // ---
    
    private final List<Point> neighborsTmp1 = new ArrayList<>();
    private final List<Point> neighborsTmp2 = new ArrayList<>();

    public void updateDistances(int targetX, int targetY) {
        int row = (int) targetY / Terrain.TILE_SIZE;
        int col = (int) targetX / Terrain.TILE_SIZE;
        Node node = map.get(col, row);
        if (node == null || node.id == 0) {
            return;
        }
        clearDistances();
        setDistance(col, row, 1);
        neighborsTmp1.clear();
        neighborsTmp2.clear();
        neighborsTmp1.add(getCachedPoint(col, row));
        int dist = 2;
        while (!neighborsTmp1.isEmpty()) {
            for (Point n : neighborsTmp1) {
                node = map.get(n.x, n.y);
                int direction = 1;
                if (node != null) {
                    for (Point directionVector : DIRECTION_VECTORS) {
                        if (node.isDirectionAllowed(direction)) {
                            int nx = n.x + directionVector.x;
                            int ny = n.y + directionVector.y;
                            if (setDistance(nx, ny, dist)) {
                                neighborsTmp2.add(new Point(nx, ny));
                            }
                        }
                        direction *= 2;
                    }
                }
            }
            savePointsToCache(neighborsTmp1);
            neighborsTmp1.addAll(neighborsTmp2);
            savePointsToCache(neighborsTmp2);
            dist++;
        }
    }

    public int getDistance(int col, int row) {
        if (map.get(col, row).id == 9) {
            return distances[row][col];
        }
        else {
            return Integer.MAX_VALUE;
        }
    }

    private boolean setDistance(int col, int row, int distance) {
        if (blockingEnemiesEnabled && blockedByEnemies(col, row)) {
            return false;
        }
        else if (map.get(col, row).id == 9 && distances[row][col] == 0) {
            distances[row][col] = distance;
            return true;
        }
        else {
            return false;
        }
    }

    private final Font font = new Font("arial", Font.PLAIN, 8);
    private final Color color = new Color(0, 255, 0, 64);
    
    public void drawDebug(Graphics2D g) {
        final int size = Terrain.TILE_SIZE;
        g.setColor(Color.BLACK);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Node node = map.get(col, row);
                if (node.id == 9) {
                    g.setColor(color);
                    g.fillRect(col * size, row * size, size, size);
                    g.setColor(Color.BLACK);
                    g.setFont(font);
                    g.drawString("" + getDistance(col, row), col * size, (row + 1) * size);     
                }
            }
        }
    }
    
}
