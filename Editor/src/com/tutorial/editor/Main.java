package com.tutorial.editor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Level Editor");

        PreviewPanel panel = new PreviewPanel();
        ToolsPanel tools = new ToolsPanel(panel);

        Container contentPane = frame.getContentPane();

        panel.setPreferredSize(new Dimension(800, 600));
        tools.setPreferredSize(new Dimension(300, 600));

        frame.setLayout(new FlowLayout());
        frame.add(panel, BorderLayout.EAST);
        frame.add(tools, BorderLayout.WEST);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
