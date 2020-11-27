package br.ol.kv.audio;

import br.ol.kv.infra.Time;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * SoundPlayer class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SoundPlayer implements Runnable {
    
    private final int id;
    private final SoundManager soundManager;
    private Thread thread;
    private SourceDataLine line;
    private Sound sound;
    private boolean loop;
    
    private boolean paused;
    private double pauseTime;
    
    public SoundPlayer(int id, SoundManager soundManager) {
        this.id = id;
        this.soundManager = soundManager;
    }
    
    private void createLine() throws Exception {
        Mixer mixer = AudioSystem.getMixer(null);
        SourceDataLine.Info sourceDataLineInfo 
                = new DataLine.Info(SourceDataLine.class, SoundManager.AUDIO_FORMAT);
        line = (SourceDataLine) mixer.getLine(sourceDataLineInfo);
    }

    public Sound getSound() {
        return sound;
    }
    
    public boolean start() {
        try {
            createLine();
            line.open();
            line.start();
            thread = new Thread(this);
            thread.start();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public boolean isAvailable() {
        return sound == null;
    }
    
    public void play(Sound sound, boolean loop) {
        this.loop = loop;
        this.sound = sound;
    }

    @Override
    public void run() {
        while (soundManager.isRunning()) {
            if (paused && pauseTime > 0 && Time.getCurrent() >= pauseTime) {
                line.start();
                paused = false;
            }
            else if (sound != null) {
                line.write(sound.getData(), 0, sound.getSize());
                if (!loop) {
                    sound = null;
                }
            }

            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void stop() {
        sound = null;
        loop = false;
        line.flush();
    }

    public void pause() {
        pause(-1);
    }

    // time in seconds
    public void pause(double time) {
        line.stop();
        paused = true;
        if (time < 0) {
            this.pauseTime = -1;
        }
        else {
            this.pauseTime = Time.getCurrent() + time;
        }
    }

    public void resume() {
        if (paused) {
            line.start();
            paused = false;
        }
    }
    
}
