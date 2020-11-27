package com.tutorial.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ToolsPanel extends JPanel implements ActionListener{

    private PreviewPanel pp;
    private JButton b1 = new JButton();
    private JButton b2 = new JButton();
    private BufferedImageLoader loader = new BufferedImageLoader();
    private BufferedImage b1Image;
    private BufferedImage b2Image;

    public ToolsPanel(PreviewPanel pp) {
        super();
        this.pp = pp;

        b1Image = loader.loadImage("C:\\Users\\Evgeniy\\Desktop\\projects\\GAMES\\Editor\\res\\brick.png");
        b2Image = loader.loadImage("C:\\Users\\Evgeniy\\Desktop\\projects\\GAMES\\Editor\\res\\jungle.png");
        pp.paintImage = b1Image;

        b1.addActionListener(this);
        b1.setIcon(new ImageIcon(b1Image.getScaledInstance(32, 32, 0)));
        add(b1);

        b2.addActionListener(this);
        b2.setIcon(new ImageIcon(b2Image.getScaledInstance(32, 32, 0)));
        add(b2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b1) {
            System.out.println("RED");
            pp.paintImage = b1Image;

        } else if (e.getSource() == b2) {
            System.out.println("YELLOW");
            pp.paintImage = b2Image;
        }
    }
}
