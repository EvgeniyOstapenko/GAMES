package br.ol.kv.infra;

/**
 * Time class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Time {

    public static final double FRAME_RATE = 30;
    public static final double TIME_STEP_PER_FRAME = 1 / FRAME_RATE;
    
    private static double current;
    private static double delta;
    private static double unprocessed;
    private static double previous = -1;

    private static int updatesCount;

    public static double getCurrent() {
        if (current == 0) {
            current = System.nanoTime() * 0.000000001;
        }
        return current;
    }

    public static double getDelta() {
        return delta;
    }

    static void start() {
        current = System.nanoTime() * 0.000000001;
        previous = current;
    }

    static void update() {
        current = System.nanoTime() * 0.000000001;
        delta = current - previous;
        unprocessed += delta;
        while (unprocessed >= TIME_STEP_PER_FRAME) {
            unprocessed -= TIME_STEP_PER_FRAME;
            updatesCount++;
        }
        previous = current;
    }

    static boolean needsUpdate() {
        if (updatesCount > 0) {
            updatesCount--;
            return true;
        }
        return false;
    }
        
}
