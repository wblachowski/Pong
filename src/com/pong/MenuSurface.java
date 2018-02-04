package com.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class MenuSurface extends JPanel implements Runnable {

    String title = "PONG";
    String[] options = new String[]{"New game", "Settings", "Quit"};

    private Thread animator;

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

        font = new Font("arial", Font.BOLD, 35);
        g.setFont(font);
        for (int i = 0; i < options.length; i++) {
            Rectangle2D stringBounds = font.getStringBounds(options[i], frc);
            Point mousePosition = getMousePosition();
            int x = (int) (getWidth() / 2 - stringBounds.getWidth()/2);
            int y = 200+i*60;
            if (mousePosition!= null && mousePosition.getX()>=x && mousePosition.getX()<=stringBounds.getWidth()+x
                    && mousePosition.getY()<=y && mousePosition.getY()>=y-stringBounds.getHeight()
                    ){
                g.setColor(Color.yellow);
            } else {
                g.setColor(Color.white);
            }
            g.drawString(options[i], x, y);
        }
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
}
