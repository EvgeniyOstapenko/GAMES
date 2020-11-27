package br.ol.kv.ai;

import br.ol.kv.physics.Terrain;

/**
 * PathPattern class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class PathPattern {
    
    private int cols;
    private int rows;
    
    // 0=not rigid 1=rigid 2=it doesn't matter
    private int[][] terrainIds; // [row][col]
    
    private int[][] directions; // [row][col]
    
    // '0'=ignore 'l'=jump left 'r'=jump right
    private String[][] jumps; // [row][col]

    public PathPattern(String terrainStr, String directionsStr, String jumpsStr) {
        parseTerrain(terrainStr.trim());
        parseDirections(directionsStr.trim());
        parseJumps(jumpsStr.trim());
    }
    
    private void parseTerrain(String terrainStr) {
        String lines[] = terrainStr.split("\n");
        cols = lines[0].trim().split("\\,").length;
        rows = lines.length;
        terrainIds = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String idsStr[] = lines[row].split("\\,");
                terrainIds[row][col] = Integer.parseInt(idsStr[col].trim());
            }
        }
    }

    private void parseDirections(String directionsStr) {
        String lines[] = directionsStr.split("\n");
        directions = new int[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String idsStr[] = lines[row].split("\\,");
                directions[row][col] = Integer.parseInt(idsStr[col].trim());
            }
        }
    }
    
    private void parseJumps(String jumpsStr) {
        String lines[] = jumpsStr.split("\n");
        jumps = new String[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String idsStr[] = lines[row].split("\\,");
                jumps[row][col] = idsStr[col].trim();
            }
        }
    }
    
    public void analyze(Terrain terrain, PathMap pathMap, int startCol, int startRow) {
        boolean match = true;
        outer:
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int c = col + startCol;
                int r = row + startRow;
                switch (terrainIds[row][col]) {
                    case 0:
                        if (terrain.isRigidByGrid(c, r, 0)) {
                            match = false;
                            break outer;
                        }
                        break;
                    case 1:
                        if (!terrain.isRigidByGrid(c, r, 0)) {
                            match = false;
                            break outer;
                        }
                        break;
                    case 2:
                        // just ignore
                        break;
                }
            }
        }
        if (!match) {
            return;
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int c = col + startCol;
                int r = row + startRow;
                pathMap.get(c, r).allowedDirections |= directions[row][col];
                pathMap.get(c, r).id = pathMap.get(c, r).allowedDirections != 0 ? 9 : 0;
                // jumps
                if (jumps[row][col].contains("l")) {
                    pathMap.get(c, r).jumpLeft = true;
                }
                if (jumps[row][col].contains("r")) {
                    pathMap.get(c, r).jumpRight = true;
                }
            }
        }
    }
    
}
