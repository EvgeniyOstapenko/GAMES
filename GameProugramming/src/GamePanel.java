import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener { // runnable have only run method!

    // creating GamePanel with start program
    public static int WIDTH = 800;
    public static int HEIGHT = 800;

    private Thread thread; // why are we needed a thread???
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets; // push our bullet to the list
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerups;
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;

    private long waveStartTimer;
    private long waveStartTimerDiff; // ?
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000; // How long will be appear and disappear the wave bar

    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength = 1000;


    public GamePanel() {  // the constructor
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {        // obviously it called automatically, it's JComponent's method!
        super.addNotify();         // Makes this Container displayable, haw is it working???
        // It is very low level stuff that is part of the "glue"
        // that connects the AWT world to the native windowing world
        if (thread == null) {
            thread = new Thread(this);
            thread.start();    // why this working this thread?
        }
        addKeyListener(this);
    }

    public void run() { // run starts all this fan stuff!!! this method called automatically?! and once in a beginning?
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);// Buffer!!!
        g = (Graphics2D) image.getGraphics(); // creating graphical object from our image
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new Player();
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        texts = new ArrayList<>();

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;


        long startTime;      // the start time
        long URDTimeMillis;  // the time in ms
        long waitTime;       // time for waiting
        long totalTime = 0;  // all this is creating again and again each time of staring loop?

        int frameCount = 0;
        int maxFrameCount = 30; // max number for loop repeating for one second?

        long targetTime = 1000 / FPS; // this is time im ms that we is wanted the loop to going
        // about 33 ms we will be waiting in each loop iterations!!

        while (running) {
            startTime = System.nanoTime(); // get time in a nanoseconds

            gameUpdate();
            gameRender(); // render - it is draw everything but not buffer
            gameDraw();   // in the beginning of loop we made this important three methods !!!!!

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000; // get time in ms after methods done
            //System.out.println(URDTimeMillis);
            waitTime = targetTime - URDTimeMillis; // this is difference between time that we is needed and past time
            // we are wanted that the loop is working about targetTime
            // the URDTimeMillis is already gone and only the waitTime is leaved to go

            try {
                Thread.sleep(waitTime); // we are sleeping in each iterations !!
            } catch (Exception e) {
            }


            totalTime += System.nanoTime() - startTime; // this is difference between current time and startTime
            frameCount++;                               // how many times we have the loop had ran
            // each loop is getting about 1/30 of second. And then we ii waiting for 30 iterations of loop!!

            //System.out.println(((System.nanoTime()-startTime)/1000000)*30); // this is full iteration's time!!

            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }

        g.setColor(new Color(0, 100, 155));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 16));
        String s = "G A M E    O V E R";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);
        s = "Final Score: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2 + 30);
        gameDraw(); //??
    }


    private void gameUpdate() {

        // new wave
        if (waveStartTimer == 0 && enemies.size() == 0) { // the trigger
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime(); // starts the cloak
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000; // time our wave, the time that going
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }

        // create enemies
        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }


        // player update
        player.update();

        // bullets update
        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update(); // WOW!!! two operations at once!  i - object bullet
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }

        // enemy update
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }

        // powerup update
        for (int i = 0; i < powerups.size(); i++) {
            boolean remove = powerups.get(i).update();
            if (remove) {    // if gift is out of window
                powerups.remove(i);
                i--;
            }
        }

        // explosion update
        for (int i = 0; i < explosions.size(); i++) {
            boolean remove = explosions.get(i).update();
            if (remove) {
                explosions.remove(i);
                i--;
            }
        }
        
        // text update
        for (int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if (remove) {
                texts.remove(i);
                i--;
            }
        }

        // bullet-enemy collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();

            for (int j = 0; j < enemies.size(); j++) {
                Enemy e = enemies.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy); // return square root

                if (dist < br + er + 10) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }

            }

        }

        // check dead enemies
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                Enemy e = enemies.get(i);

                // chance for powerup
                double rand = Math.random();
                if (rand < 0.001) powerups.add(new PowerUp(1, e.getx(), e.gety()));
                else if (rand < 0.020) powerups.add(new PowerUp(3, e.getx(), e.gety()));
                else if (rand < 0.120) powerups.add(new PowerUp(2, e.getx(), e.gety()));
                else if (rand < 0.130) powerups.add(new PowerUp(4, e.getx(), e.gety()));

                //else powerups.add(new PowerUp(4, e.getx(), e.gety()));

                // Math.random() - generates numbers from zero to one !!!


                player.addScore(e.getType() + e.getRank()); // earn the score!
                enemies.remove(i);
                i--;

                e.explode();
                explosions.add(new Explosion((e.getx()), (e.gety()), e.getr(), e.getr() + 30));
            }

        }

        // check dead player
        if (player.isDead()) {
            running = false;
        }

        // player-enemy collision
        if (!player.isRecovering()) {       // if I am not red, then I can get the player-enemy collision
            int px = (int) player.getx();
            int py = (int) player.gety();
            int pr = (int) player.getr();
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = px - ex; // third side of a triangle
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy); // the distance between player and enemy

                if (dist < pr + er) {
                    player.loseLife();
                }
            }
        }

        // player-powerup collision
        int px = (int) player.getx();
        int py = (int) player.gety();
        int pr = (int) player.getr();
        for (int i = 0; i < powerups.size(); i++) {
            PowerUp p = powerups.get(i);
            double x = p.getx();
            double y = p.gety();
            double r = p.getr();
            double dx = px - x;
            double dy = py - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            // collected powerup
            if (dist < pr + r + 10) {
                int type = p.getType();
                if (type == 1) {
                    player.gainLife();
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Extra Live"));
                }
                if (type == 2) {
                    player.increasePower(1);
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Power"));
                }
                if (type == 3) {
                    player.increasePower(2);
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Double power"));
                }
                if (type == 4) {
                    slowDownTimer = System.nanoTime(); // time is begun when I got
                    for (int j = 0; j < enemies.size(); j++) {
                        enemies.get(j).setSlow(true);
                    }
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Slow Down"));
                }

                powerups.remove(i);
                i--;
            }
        }

        // slowdown update
        if (slowDownTimer != 0) {
            slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
            if (slowDownTimerDiff > slowDownLength) {
                slowDownTimer = 0;
                for (int j = 0; j < enemies.size(); j++) {
                    enemies.get(j).setSlow(false);
                }
            }
        }

    }

    private void gameRender() {                    // draw our friends on buffer
        // draw background
        g.setColor(new Color(0, 100, 155)); // adding all we wanted to our graphical object!
        g.fillRect(0, 0, WIDTH, HEIGHT);      // this is background!!! it redraw

        // draw slowdown screen
        if (slowDownTimer != 0) {
            g.setColor(new Color(255, 255, 255, 64));
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }

        // draw player
        player.draw(g);

        // draw bullet
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g); // g - it is object of our bullet
        }
        // draw enemy // and a can write the wounded enemy!!!
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        // draw powerup
        for (int i = 0; i < powerups.size(); i++) {
            powerups.get(i).draw(g);
        }

        // draw explosions
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).draw(g);
        }

        // draw text
        for (int i = 0; i < texts.size(); i++) {
            texts.get(i).draw(g);
        }

        // draw wave number
        if (waveStartTimer != 0) {         // start not immediately?
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "- W A V E " + waveNumber + " -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth(); // length our string?
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay)); // sin - up and dawn in time !!!
            if (alpha > 255) alpha = 255;   // cool!!!
            g.setColor(new Color(255, 255, 255, alpha)); //  changing the transparency !!!
            g.drawString(s, WIDTH / 2 - length / 2, (HEIGHT / 2) - 20);
        }

        // draw player lives
        for (int i = 0; i < player.getLives(); i++) {
            g.setColor(Color.WHITE);
            g.fillOval(20 + (20 * i), 20, (int) player.getr() * 2, (int) player.getr() * 2);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());
            g.drawOval(20 + (20 * i), 20, (int) player.getr() * 2, (int) player.getr() * 2);
            g.setStroke(new BasicStroke(1));
        }

        // draw player power
        g.setColor(Color.YELLOW);
        g.fillRect(20 , 40, player.getPower() * 8, 8); // the squares are painted
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(2)); // thickening of squares
        for (int i = 0; i < player.getRequiredPower(); i++) {
            g.drawRect(20 + 8 * i, 40, 8, 8); // each time - new small square plus old!!!
        }
        // draw player score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);

        // draw slowdown meter
        if (slowDownTimer != 0) {
            g.setColor(Color.WHITE);
            g.drawRect(20, 60, 100, 8);
            g.fillRect(20, 60, (int) (100 - 100.0 * slowDownTimerDiff / slowDownLength), 8);
        }
    }

    private void gameDraw() {  // repainting all Panel
        Graphics g2 = this.getGraphics(); // get graphics from GamePanel object
        g2.drawImage(image, 0, 0, null); // draw all our buffer!!!
        g2.dispose();
    }

    private void createNewEnemies() {
        enemies.clear();
        if (waveNumber == 1) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if (waveNumber == 2) {
            for (int i = 0; i < 8; i++) {
                enemies.add(new Enemy(1, 1));
            }

        }
        if (waveNumber == 3) {
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
        }
        if (waveNumber == 4) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 4));
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
            }
        }
        if (waveNumber == 5) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));

        }
        if (waveNumber == 6) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
            for (int i = 0; i < 4; i++) {
                enemies.add(new Enemy(2, 1));
                enemies.add(new Enemy(3, 1));
            }
        }
        if (waveNumber == 7) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(2, 3));
            enemies.add(new Enemy(3, 3));

        }
        if (waveNumber == 7) {
            enemies.add(new Enemy(1, 4));
            enemies.add(new Enemy(2, 4));
            enemies.add(new Enemy(3, 4));

        }
        if (waveNumber == 7) {
            running = false;
        }
    }


    @Override
    public void keyTyped(KeyEvent key) { // what of type of key??

    }

    @Override
    public void keyPressed(KeyEvent key) {  // when the buttons is pressed
        int keyCode = key.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(true);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(true);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(true);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(true);
        }

    }

    @Override
    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(false);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(false);
        }

    }
}
