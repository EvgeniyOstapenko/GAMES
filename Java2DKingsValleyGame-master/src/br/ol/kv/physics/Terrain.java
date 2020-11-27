package br.ol.kv.physics;

import br.ol.kv.infra.LevelLoader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Terrain class.
 * 
 * brick: 1, 8 = unbreakable brick, 9 = enemy ai random jump
 * 
 * stairs: 4\      /7
 *          3\    /6
 *           2\  /5
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Terrain {
    
    public static final int TILE_SIZE = 8;
    private String brickColor;
    private int rows, cols;
    private int[][] map;

    public Terrain() {
    }
    
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String getBrickColor() {
        return brickColor;
    }

    public int[][] getMap() {
        return map;
    }
    
    public void set(int col, int row, int id) {
        if (isOutOfBounds(col, row)) {
            return;
        }
        map[row][col] = id;
    }

    public void loadMapFromResource(String resource) {
        try {
            InputStream is = getClass().getResourceAsStream("/res/" + resource);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            cols = Integer.parseInt(br.readLine());
            rows = Integer.parseInt(br.readLine());
            map = new int[rows][cols];
            int row = 0;
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                for (int col = 0; col < line.length(); col++) {
                    map[row][col] = Integer.parseInt("" +  line.charAt(col));
                }
                row++;
            }
            br.close();
        }
        catch (Exception ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    public void loadMap(LevelLoader levelLoader) {
        String terrain = levelLoader.getTerrain();
        cols = levelLoader.getCols();
        rows = levelLoader.getRows();
        brickColor = levelLoader.getBrickColor();
        map = new int[rows][cols];
        String[] lines = terrain.split("\n");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                map[row][col] = Integer.parseInt("" +  lines[row].charAt(col));
            }
        }
    }
    
    public int getIdByPosition(int sx, int sy) {
        int row = sy / TILE_SIZE;
        int col = sx / TILE_SIZE;
        return getIdByGrid(col, row);
    }

    public int getIdByGrid(int col, int row) {
        if (isOutOfBounds(col, row)) {
            return -1;
        }
        return map[row][col];
    }

    public boolean isRigidByGrid(int col, int row, int minTerrainCollisionRow) {
        if (row < minTerrainCollisionRow) {
            return false;
        }
        if (isOutOfBounds(col, row)) {
            return false;
        }
        return map[row][col] == 1 || map[row][col] == 8
                || map[row][col] == 2 || map[row][col] == 5
                || map[row][col] == 4 || map[row][col] == 7;
    }
    
    public boolean isRigidByPosition(int sx, int sy, int minTerrainCollisionY) {
        int minTerrainCollisionRow = minTerrainCollisionY / TILE_SIZE;
        int row = sy / TILE_SIZE;
        int col = sx / TILE_SIZE;
        return isRigidByGrid(col, row, minTerrainCollisionRow);
    }

    public boolean canDig(int playerCol, int brickCol, int brickRow) {
        if (brickRow >= rows - 1) {
            return false;
        }
        else if (isOutOfBounds(playerCol, brickRow) 
                || isOutOfBounds(brickCol, brickRow) 
                | isOutOfBounds(brickCol, brickRow - 1)) {
            return false;
        }
        return isRigidByGrid(playerCol, brickRow, 0)
                && map[brickRow][brickCol] == 1 
                && map[brickRow - 1][brickCol] == 0;
    }

    public boolean canDrop(int playerCol, int brickCol, int brickRow) {
        if (isOutOfBounds(playerCol, brickRow) 
                || isOutOfBounds(brickCol, brickRow) 
                | isOutOfBounds(brickCol, brickRow - 1)) {
            return false;
        }
        return isRigidByGrid(playerCol, brickRow, 0)
                && isRigidByGrid(brickCol, brickRow, 0)
                && map[brickRow - 1][brickCol] == 0;
    }
    
    public boolean isOutOfBounds(int col, int row) {
        return col < 0 || row < 0 || col > cols - 1 || row > rows - 1;
    }

    private final Color[] colors = { 
            Color.WHITE, Color.BLACK, Color.RED, Color.LIGHT_GRAY, 
            Color.GREEN, Color.MAGENTA, Color.GRAY, Color.CYAN, 
            Color.YELLOW, Color.ORANGE };
    
    public void drawDebug(Graphics2D g) {
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int tileIndex = map[row][col];
                g.setColor(colors[tileIndex]);
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

}
