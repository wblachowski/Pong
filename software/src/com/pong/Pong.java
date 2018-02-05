package com.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pong extends JFrame {

    public Pong() {
        initUI();
    }

    MenuSurface surface;
    private void initUI() {
        final int width = 800;
        final int height = 600;
        /*Surface surface = new Surface();
        surface.vel.value=-20;

        surface.setStartingPoint(height/2);*/
        surface = new MenuSurface();
        addKeyListener(surface);
        add(surface);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //Timer timer = surface.getTimer();
                //timer.stop();
            }
        });

        setTitle("Points");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }



    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Pong pong = new Pong();
                pong.setVisible(true);
            }
        });
    }
}
