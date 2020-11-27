import java.awt.*;

public class Enemy {
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad; // what is it?
    private double speed;

    private int health;
    private int type;
    private int rank;

    private Color color1;

    private boolean ready; // checking in window or not the enemy
    private boolean dead;

    private boolean hit;
    private long hitTimer;

    private boolean slow;

    public Enemy(int type, int rank) {
        this.type = type;
        this.rank = rank;

        // default enemies
        if (type == 1) {
//            color1 = Color.BLUE;
            color1 = new Color(0, 0, 255, 128);
            if (rank == 1) {
                speed = 2;
                r = 5;
                health = 1;
            }
            if (rank == 2) {
                speed = 2;
                r = 20;
                health = 2;
            }
            if (rank == 3) {
                speed = 1.5;
                r = 30;
                health = 3;
            }
            if (rank == 4) {
                speed = 1.5;
                r = 50;
                health = 4;
            }
        }
        // stronger, faster default
        if (type == 2) {
//            color1 = Color.RED;
            color1 = new Color(255, 0, 0, 128);
            if (rank == 1) {
                speed = 3;
                r = 5;
                health = 2;
            }
            if (rank == 2) {
                speed = 3;
                r = 10;
                health = 3;
            }
            if (rank == 3) {
                speed = 2.5;
                r = 20;
                health = 3;
            }
            if (rank == 4) {
                speed = 2.5;
                r = 30;
                health = 4;
            }
        }
        // slow, but hard to kill
        if (type == 3) {
//            color1 = Color.GREEN;
            color1 = new Color(0, 255, 0, 128);
            if (rank == 1) {
                speed = 1.5;
                r = 5;
                health = 3;
            }
            if (rank == 2) {
                speed = 1.5;
                r = 10;
                health = 4;
            }
            if (rank == 3) {
                speed = 1.5;
                r = 25;
                health = 5;
            }
            if (rank == 4) {
                speed = 1.5;
                r = 45;
                health = 5;
            }
        }

        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = -r;

        double angle = Math.random() * 140 + 20; // why??
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed; // may be positive or negative!

        ready = false;
        dead = false;

        hit = false;
        hitTimer = 0;

    }

    public double getx() { return x;}
    public double gety() { return y;}
    public int getr() { return r;}

    public int getType() { return  type; }
    public int getRank() { return  rank; }

    public void setSlow(boolean b) { slow = b; }

    public boolean isDead() {return dead;}

    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
        hit = true; // the moment of impact
        hitTimer = System.nanoTime();
    }

    public void explode() {
        if (rank > 1) {
            int amount = 0;
            if(type == 1){
                amount = 3;
            }if(type == 2){
                amount = 3;
            }
            if(type == 3){
                amount = 4;
            }
            for (int i = 0; i < amount; i++) {
                Enemy e = new Enemy(getType(), getRank() - 1);
                e.setSlow(slow);
                e.x = this.x;
                e.y = this.y;
                double angle;
                if (!ready) {
                    angle = Math.random() * 140 + 20; // this affects the speed
                } else {
                    angle = Math.random() * 360;
                }
                e.rad = Math.toRadians(angle);
                GamePanel.enemies.add(e); // in any cases we adds new enemy!
            }
        }

    }


    public void update() {      // if enemy in the our window?
        if (slow) {
            x += dx * 0.3;
            y += dy * 0.3;
        } else {
            x += dx; // add random velocity
            y += dy;
        }
        if (!ready) {                               // what is it???  checking or enemy on the field
            if (x > r && x < GamePanel.WIDTH - r &&
                    y > r && y < GamePanel.HEIGHT) {
                ready = true;
            }
        }

        if(x < r && dx < 0) dx = -dx;  // the walls collisions checking
        if(y < r && dy < 0) dy = -dy;  // upper limit
        if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
        if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
         // we are changing the speed!!! direction

        if (hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if (elapsed > 50) {       // time for enemy's lighting
                hit = false;
                hitTimer = 0;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (hit) {
            g.setColor(Color.WHITE);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

            g.setStroke(new BasicStroke(3)); // out-side our player, it has the certain thickness!!
            g.setColor(Color.WHITE.darker());
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));

        }
        else {
            g.setColor(color1);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);

            g.setStroke(new BasicStroke(3)); // out-side our player, it has the certain thickness!!
            g.setColor(color1.darker());
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        }
    }
}
