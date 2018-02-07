package com.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class MenuSurface extends JPanel implements Runnable, KeyListener {

    String title = "PONG";
    String[] options = new String[]{"New game", "Settings", "Quit"};

    private Thread animator;
    private int state = 0;

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        FontRenderContext frc = new FontRenderContext(null, true, true);
        Font font = new Font("arial", Font.BOLD, 50);
        g.setFont(font);
        g.setColor(Color.white);

        g.drawString(title, getWidth() / 2 - (int) font.getStringBounds(title, frc).getWidth() / 2, 100);

        for (int i = 0; i < options.length; i++) {
            if (state == i) {
                font = new Font("arial", Font.BOLD, 38);
                g.setColor(Color.yellow);
            } else {
                font = new Font("arial", Font.BOLD, 35);
                g.setColor(Color.white);
            }
            g.setFont(font);
            Rectangle2D stringBounds = font.getStringBounds(options[i], frc);
            g.drawString(options[i], (int) (getWidth() / 2 - stringBounds.getWidth() / 2), 200 + i * 60);
        }
    }

    public void processKey(char keyChar) {
        switch (keyChar) {
            case 's':
                state++;
                state = state % 3;
                break;
            case 'w':
                state--;
                if (state < 0) state = 2;
                break;
            case 'x':
                if (state == 0) selectMenuItem();
                break;
        }

    }

    public void selectMenuItem() {
        Pong.getInstance().showNewGame();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    @Override
    public void run() {

        long beforeTime, timeDiff, sleep;

        beforeTime = System.currentTimeMillis();

        while (true) {

            repaint();

            timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = 10 - timeDiff;

            if (sleep < 0) {
                sleep = 2;
            }

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("Interrupted: " + e.getMessage());
            }

            beforeTime = System.currentTimeMillis();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        processKey(e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
