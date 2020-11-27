package br.ol.kv.physics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Body class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Body<T> {
    
    private final T owner;
    private World world;
    private Terrain terrain;
    
    private boolean collisible = true;
    private boolean affectedByGravity = true;
    
    private int x;
    private int y;

    private final int pivotX;
    private final int pivotY;
    
    private final int width;
    private final int height;
    
    private double vx;
    private double vy;

    private double adx;
    private double ady;

    private int minTerrainCollisionY;
    private final int minTerrainCollisionRangeY;
    
    private int leftBound;
    private int topBound;
    private int rightBound;
    private int bottomBound;
    private int middleXBound;
    private int middleYBound;
    
    private double horizontalVx = 1.5;
    private double jumpVx = 1.75;
    private double jumpVy = -5.0;
    
    private String lastDirection = "left";

    private boolean movable = true;
    
    private boolean moveLeft;
    private boolean moveRight;
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveJump;
    
    private boolean onStair;
    private boolean stairDownLeft;
    private boolean stairDownRight;
    private boolean stairUpLeft;
    private boolean stairUpRight;
    private final Point stairPosition = new Point();
    
    public Body(T owner, int x, int y, int width, int height, int pivotX, int pivotY, int minTerrainCollisionRangeY) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.minTerrainCollisionRangeY = minTerrainCollisionRangeY;
    }

    public void reset() {
        minTerrainCollisionY = 0;
        onStair = false;
        vx = 0;
        vy = 0;
        adx = 0;
        ady = 0;
    }

    public T getOwner() {
        return owner;
    }

    public World getWorld() {
        return world;
    }
    
    public void setWorld(World world) {
        this.world = world;
        this.terrain = world.getTerrain();
    }

    public boolean isCollisible() {
        return collisible;
    }

    public void setCollisible(boolean collisible) {
        this.collisible = collisible;
    }

    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPivotX() {
        return pivotX;
    }

    public int getPivotY() {
        return pivotY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getHorizontalVx() {
        return horizontalVx;
    }

    public void setHorizontalVx(double horizontalVx) {
        this.horizontalVx = horizontalVx;
    }

    public double getJumpVx() {
        return jumpVx;
    }

    public void setJumpVx(double jumpVx) {
        this.jumpVx = jumpVx;
    }

    public double getJumpVy() {
        return jumpVy;
    }

    public void setJumpVy(double jumpVy) {
        this.jumpVy = jumpVy;
    }

    public boolean isOnStair() {
        return onStair;
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public int getTopBound() {
        return topBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    public int getBottomBound() {
        return bottomBound;
    }

    public int getMiddleXBound() {
        return middleXBound;
    }

    public int getMiddleYBound() {
        return middleYBound;
    }

    public int getMinTerrainCollisionY() {
        return minTerrainCollisionY;
    }

    public int getMinTerrainCollisionRangeY() {
        return minTerrainCollisionRangeY;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }
    
    public void update() {
        updateMovement();
        updateGravity();
        updateHorizontalPosition();
        if (onStair) {
            updateStairVerticalPosition();
        }
        else {
            updateVerticalPosition();
        }
    }

    public void resetMovement() {
        moveLeft = false;
        moveRight = false;
        moveUp = false;
        moveDown = false;
        moveJump = false;
    }

    public void moveLeft() {
        moveLeft = true;
        lastDirection = "left";
    }
    
    public void moveRight() {
        moveRight = true;
        lastDirection = "right";
    }
    
    public void moveUp() {
        moveUp = true;
    }
    
    public void moveDown() {
        moveDown = true;
    }
    
    public void moveJump() {
        moveJump = true;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public boolean isMoveUp() {
        return moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public boolean isMoveJump() {
        return moveJump;
    }
    
    private void updateMovement() {
        if (!movable) {
            return;
        }
        
        boolean collidingWithTerrainFloor = collidingWithTerrainFloor();
        
        if (moveLeft) {
            vx = collidingWithTerrainFloor || onStair ? -horizontalVx : -jumpVx;
        }
        else if (moveRight) {
            vx = collidingWithTerrainFloor || onStair ? horizontalVx : jumpVx;
        }
        else {
            vx = 0;
        }
        // moveJump
        if (!onStair && moveJump && collidingWithTerrainFloor) {
            vy = jumpVy;
            moveJump = false;
        }
    }

    private void updateGravity() {
        if (affectedByGravity) {
            vy += World.GRAVITY;
        }
    }
    
    private void updateHorizontalPosition() {
        adx += vx;
        int sign = adx < 0 ? -1 : 1;
        while (adx * sign >= 1) {
            if ((collidingWithTerrainRight() && adx > 0)
                    || (collidingWithTerrainLeft() && adx < 0)) {
                adx = 0;
                vx = 0;
                break;
            }
            else {
                x += sign;
                adx -= sign;
                if (!onStair && checkBeginStairState()) {
                    stairPosition.setLocation(x, y);
                    minTerrainCollisionY = Integer.MAX_VALUE;
                    onStair = true;
                    adx = 0;
                    vx = 0;
                    break;
                }
            }
        }
    }
    
    private void updateVerticalPosition() {
        ady += vy;
        int sign = ady < 0 ? -1 : 1;
        while (ady * sign >= 1) {
            boolean collidingWithFloor = collidingWithTerrainFloor();
            if (collidingWithFloor) {
                minTerrainCollisionY = y - pivotY - minTerrainCollisionRangeY;
            }
            if ((collidingWithFloor && ady > 0)
                    || (collidingWithTerrainCeil() && ady < 0)) {
                ady = 0;
                vy = 0;
                break;
            }
            else {
                y += sign;
                ady -= sign;
            }
        }
    }
    
    public void updateStairVerticalPosition() {
        vy = 0;
        int targetY = y;
        if (stairDownLeft) {
            targetY = stairPosition.y + (stairPosition.x - x) + 1;
        }
        else if (stairDownRight) {
            targetY = stairPosition.y + (x - stairPosition.x) + 1;
        }
        else if (stairUpLeft) {
            targetY = stairPosition.y + (x - stairPosition.x) - 1;
        }
        else if (stairUpRight) {
            targetY = stairPosition.y + (stairPosition.x - x) - 1;
        }
        int dif = targetY - y;
        int sign = dif > 0 ? 1 : -1;
        dif = Math.abs(dif);
        while (dif != 0) {
            y += sign;
            dif--;
            if (checkEndStairState()) {
                minTerrainCollisionY = 0;
                onStair = false;
                break;
            }
        }
    }
    
    public void updateBounds() {
        leftBound = x - pivotX;
        topBound = y - pivotY;
        rightBound = leftBound + width - 1;
        bottomBound = topBound + height - 1;
        middleXBound = leftBound + width / 2;
        middleYBound = topBound + height / 2;
    }
    
    public boolean collidingWithTerrainFloor() {
        updateBounds();
        return terrain.isRigidByPosition(leftBound, bottomBound + 1, minTerrainCollisionY) 
                || terrain.isRigidByPosition(rightBound, bottomBound + 1, minTerrainCollisionY);
    }
    
    public boolean collidingWithTerrainCeil() {
        updateBounds();
        return terrain.isRigidByPosition(leftBound, topBound - 1, minTerrainCollisionY) 
                || terrain.isRigidByPosition(rightBound, topBound - 1, minTerrainCollisionY);
    }
    
    public boolean collidingWithTerrainLeft() {
        updateBounds();
        return terrain.isRigidByPosition(leftBound - 1, topBound, minTerrainCollisionY) 
                || terrain.isRigidByPosition(leftBound - 1, middleYBound, minTerrainCollisionY) 
                || terrain.isRigidByPosition(leftBound - 1, bottomBound, minTerrainCollisionY);
    }

    public boolean collidingWithTerrainRight() {
        updateBounds();
        return terrain.isRigidByPosition(rightBound + 1, topBound, minTerrainCollisionY) 
                || terrain.isRigidByPosition(rightBound + 1, middleYBound, minTerrainCollisionY) 
                || terrain.isRigidByPosition(rightBound + 1, bottomBound, minTerrainCollisionY);
    }

    public boolean collidingWithTerrain() {
        updateBounds();
        return terrain.isRigidByPosition(rightBound, topBound, 0) 
                || terrain.isRigidByPosition(leftBound, topBound, 0) 
                || terrain.isRigidByPosition(rightBound, middleYBound, 0) 
                || terrain.isRigidByPosition(leftBound, middleYBound, 0) 
                || terrain.isRigidByPosition(rightBound, bottomBound, 0)
                || terrain.isRigidByPosition(leftBound, bottomBound, 0);
    }

    private boolean checkBeginStairState() {
        stairDownLeft = false;
        stairUpRight = false;
        stairDownRight = false;
        stairUpLeft = false;
        updateBounds();
        int tileId = terrain.getIdByPosition(middleXBound, bottomBound + 1);
        if (stairDownLeft = tileId == 7 && moveDown && moveLeft 
                && middleXBound % Terrain.TILE_SIZE == 2) {
            return true;
        }
        else if (stairUpRight = tileId == 5 && moveUp && moveRight 
                && middleXBound % Terrain.TILE_SIZE == 5) {
            return true;
        }
        else if (stairDownRight = tileId == 4 && moveDown && moveRight 
                && middleXBound % Terrain.TILE_SIZE == 5) {
            return true;
        }
        else if (stairUpLeft = tileId == 2 && moveUp && moveLeft 
                && middleXBound % Terrain.TILE_SIZE == 2) {
            return true;
        }
        return false;
    }
    
    private boolean checkEndStairState() {
        updateBounds();
        return ((bottomBound + 1) % 8) == 0 
                && terrain.isRigidByPosition(middleXBound, bottomBound + 1, 0);
    }

    public boolean collides(Body ob) {
        updateBounds();
        ob.updateBounds();
        if (Math.max(leftBound + width, ob.getLeftBound() + ob.getWidth()) - Math.min(leftBound, ob.getLeftBound()) 
                > width + ob.getWidth()) {
            return false;
        }
        else if (Math.max(topBound + height, ob.getTopBound()+ ob.getHeight()) - Math.min(topBound, ob.getTopBound()) 
                > height + ob.getHeight()) {
            return false;
        }
        return true;
    }
    
    public void drawDebug(Graphics2D g) {
        int rx = x - pivotX;
        int ry = y - pivotY;
        g.setColor(Color.BLUE);
        g.drawRect(rx, ry, width - 1, height - 1);
        
        g.setColor(Color.MAGENTA);
        g.drawLine(x, y, x, y);
        
        // draw collision min area line
        g.setColor(Color.GRAY);
        g.drawLine(0, (int) minTerrainCollisionY, 800, (int) minTerrainCollisionY);
        
        g.setColor(Color.WHITE);
    }
        
}
