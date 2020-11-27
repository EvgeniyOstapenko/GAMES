package com.tutorial.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class PreviewPanel extends JPanel implements MouseListener, MouseMotionListener{

    private int width = 800;
    private int height = 600;
    private int cellSize = 8;

    public ArrayList<ColoredPoint> points = new ArrayList<>();
    private Color paintColor = Color.yellow;
    public BufferedImage paintImage = null;

    public PreviewPanel() {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        for (int i = 0; i < width; i += cellSize) {
            g.setColor(Color.white);
            g.fillRect(i, 0, 1, height);
            g.fillRect(0, i, width, 1);
        }

        for (int i = 0; i < points.size(); i++) {
            ColoredPoint tmp = points.get(i);
//            g.setColor(tmp.getColor());
//            g.fillRect(tmp.getPoint().x * cellSize,
//                       tmp.getPoint().y * cellSize,
//                       cellSize,
//                       cellSize);
            g.drawImage(tmp.getImage(), tmp.getPoint().x * cellSize,
                                        tmp.getPoint().y * cellSize,
                                        cellSize, cellSize, null);
        }

        if (!points.isEmpty()) {
            for (int i = 0; i < points.size(); i++) {
                System.out.print(points.get(i).getImage());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {


    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        Point p = clickToGrid(x, y);
        ColoredPoint cp = new ColoredPoint(paintColor, p, paintImage);
        removeDuplicate(p);
        points.add(cp);
        repaint();
    }
    // List of ColoredPoints -> in ArrayList points

    private Point clickToGrid(int x, int y) {
        int px = x;
        int py = y;
        px = px / cellSize;
        py = py / cellSize;
        return new Point(px, py);
    }

    private void removeDuplicate(Point p) {
        for (int i = 0; i < points.size(); i++) {
            ColoredPoint tmp = points.get(i);
            if (tmp.getPoint().equals(p)) {
                points.remove(i);
                return;
            }
        }
    }

    public void setPaintColor(Color color) {
        this.paintColor = color;
    }

    @Override
    public void mouseReleased(MouseEvent e) {


    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePressed(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
