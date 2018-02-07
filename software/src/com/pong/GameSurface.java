package com.pong;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.util.Random;

public class GameSurface extends JPanel implements Runnable, KeyListener {

    public static class Velocity {
        public int value;
    }

    private Thread animator;
    public Velocity vel = new Velocity();

    Bar barA;
    Bar barB;
    Ball ball;
    private int scoreA;
    private int scoreB;

    public GameSurface() {
        vel.value = 0;
        barA = new Bar(0.5, 20, 80);
        barB = new Bar(0.5, 20, 80);
        ball = new Ball(20);
        scoreA = 0;
        scoreB = 0;

        try {
            //(new TwoWaySerialComm(vel)).connect("COM4");
        } catch (Exception ex) {
        }
    }


    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        drawBars(g2d);
        drawBall(g2d);
        drawScore(g2d);
        drawSeparator(g2d);
    }

    private void drawBars(Graphics2D g2d) {
        g2d.setPaint(Color.white);
        g2d.fillRect(20, (int) (barA.getPosition() * ((double) getHeight() - barA.getHeight())), barA.getWidth(), barA.getHeight());
        g2d.fillRect(getWidth() - barB.getWidth() - 20, (int) (barB.getPosition() * ((double) getHeight() - barB.getHeight())), barB.getWidth(), barB.getHeight());
    }

    private void drawBall(Graphics2D g2d) {
        g2d.setPaint(Color.white);
        //x i y jako srodek kulki
        int x = (int) (ball.getPositionX() * (double) getWidth());
        int y = (int) (ball.getPositionY() * (double) getHeight());

        g2d.fillOval(x - ball.getSize() / 2, y - ball.getSize() / 2, ball.getSize(), ball.getSize());

        int yBarA = (int) (barA.getPosition() * ((double) getHeight() - barA.getHeight()));
        int yBarB = (int) (barB.getPosition() * ((double) getHeight() - barB.getHeight()));

        //CHECK LEFT SIDE
        if (x <= 20 + barA.getWidth() + ball.getSize() / 2 && y + ball.getSize() / 2 >= yBarA && y - ball.getSize() / 2 <= yBarA + barA.getHeight()) {
            ball.setVelocityX(ball.getVelocityX() * -1);
        }
        if (x <= 0 - ball.getSize()) {
            scoreB++;
            ball = new Ball(ball.getSize());
        }

        //CHECK RIGHT SIDE
        if (x >= getWidth() - barA.getWidth() - 20 - ball.getSize() / 2 && y + ball.getSize() / 2 >= yBarB && y - ball.getSize() / 2 <= yBarB + barB.getHeight()) {
            ball.setVelocityX(ball.getVelocityX() * -1);
        }
        if (x >= getWidth() + ball.getSize()) {
            scoreA++;
            ball = new Ball(ball.getSize());
        }

        //CHECK TOP
        if (y <= ball.getSize()/2) {
            ball.setVelocityY(ball.getVelocityY() * -1);
        }
        //CHECK BOTTOM
        if (y >= getHeight() - ball.getSize()/2) {
            ball.setVelocityY(ball.getVelocityY() * -1);
        }

        ball.updatePosition();
    }

    private void drawScore(Graphics2D g2d) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Font font = new Font("arial", Font.BOLD, 50);
        g2d.setFont(font);
        g2d.setColor(Color.white);
        g2d.drawString(Integer.toString(scoreA), getWidth() / 2 - (int) font.getStringBounds(Integer.toString(scoreA), frc).getWidth() - 40, 70);
        g2d.drawString(Integer.toString(scoreB), getWidth() / 2 + 40, 70);
    }

    private void drawSeparator(Graphics2D g2d) {
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);
        g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
    }

    @Override
    public void addNotify() {
        super.addNotify();

        animator = new Thread(this);
        animator.start();
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
        System.out.println("Game clicked: " + e.getKeyChar());
        if (e.getKeyChar() == 'w') {
            barA.setPosition(Math.max(0.0, barA.getPosition() - 0.01));
        } else if (e.getKeyChar() == 's') {
            barA.setPosition(Math.min(1.0, barA.getPosition() + 0.01));
        }
        if (e.getKeyChar() == 'i') {
            barB.setPosition(Math.max(0.0, barB.getPosition() - 0.01));
        } else if (e.getKeyChar() == 'k') {
            barB.setPosition(Math.min(1.0, barB.getPosition() + 0.01));
        }
        if (e.getKeyChar() == 'y') {
            Pong.getInstance().showMenu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    private class Bar {
        private double position;
        private int height;
        private int width;

        public Bar(double initalY, int width, int height) {
            this.position = initalY;
            this.height = height;
            this.width = width;
        }


        public double getPosition() {
            return position;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }

    private class Ball {
        int size;
        double positionX;
        double positionY;
        double velocityX;
        double velocityY;

        public Ball(int size) {
            this.size = size;
            positionX = 0.5;
            positionY = 0.5;
            this.velocityX = (Math.random() - 0.5) / 100;
            this.velocityY = (Math.random() - 0.5) / 200;
        }

        public void updatePosition() {
            positionX += velocityX;
            positionY += velocityY;
        }

        public double getPositionX() {
            return positionX;
        }

        public double getPositionY() {
            return positionY;
        }

        public double getVelocityX() {
            return velocityX;
        }

        public double getVelocityY() {
            return velocityY;
        }

        public void setVelocityX(double velocityX) {
            this.velocityX = velocityX;
        }

        public void setVelocityY(double velocityY) {
            this.velocityY = velocityY;
        }

        public int getSize() {
            return size;
        }
    }

}
