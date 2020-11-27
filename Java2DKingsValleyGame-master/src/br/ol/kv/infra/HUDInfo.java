package br.ol.kv.infra;

/**
 * HUDInfo class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HUDInfo {
    
    private int level;
    private int previousLevel;
    private int lives;
    private int score;
    private int hiscore;

    public int getLevel() {
        return level;
    }

    public String getLevelStr() {
        String levelStr = "00" + level;
        levelStr = levelStr.substring(levelStr.length() - 2, levelStr.length());
        return levelStr;
    }

    public void setLevel(int level) {
        if (this.level != level) {
            previousLevel = this.level;
            this.level = level;
        }
    }

    public int getPreviousLevel() {
        return previousLevel;
    }

    public int getLives() {
        return lives;
    }
    public String getLivesStr() {
        String livesStr = "00" + lives;
        livesStr = livesStr.substring(livesStr.length() - 2, livesStr.length());
        return livesStr;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void decLives() {
        lives--;
    }
    
    public int getScore() {
        return score;
    }
    
    public String getScoreStr() {
        String scoreStr = "000000" + score;
        scoreStr = scoreStr.substring(scoreStr.length() - 6, scoreStr.length());
        return scoreStr;
    }

    public void clearScore() {
        score = 0;
    }
    
    public void addScore(int point) {
        score += point;
    }
    
    public int getHiscore() {
        return hiscore;
    }
    
    public String getHiscoreStr() {
        String hiscoreStr = "000000" + hiscore;
        hiscoreStr = hiscoreStr.substring(hiscoreStr.length() - 6, hiscoreStr.length());
        return hiscoreStr;
    }

    public void updateHiscore() {
        if (score > hiscore) {
            hiscore = score;
        }
    }
    
    public void reset() {
        level = 1;
        previousLevel = 0;
        lives = 3;
        score = 0;
    }
    
}
