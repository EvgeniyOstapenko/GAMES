package com.tutorial.editor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColoredPoint {

    private Color color;
    private Point point;
    private BufferedImage image;

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Color getColor() {
        return color;
    }

    public Point getPoint() {
        return point;
    }

    public ColoredPoint(Color color, Point point, BufferedImage image) {
        this.color = color;
        this.point = point;
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
