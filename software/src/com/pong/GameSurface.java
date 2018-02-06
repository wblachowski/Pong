package com.pong;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
public class GameSurface extends JPanel implements Runnable,KeyListener {

    public static class Velocity{
        public int value;
    }

    private Thread animator;
    public Velocity vel = new Velocity();

    Bar barA;
    Bar barB;

    public GameSurface() {
        vel.value=0;
        barA=new Bar(20,getWidth()/2,20,60);
        try {
            //(new TwoWaySerialComm(vel)).connect("COM4");
        }catch(Exception ex){}
    }


    public void setStartingPoint(int y){
        this.y=y-50;
    }

    private int y;
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0,0,getWidth(),getHeight());

        drawBars(g2d);

        y+=vel.value;
        y=Math.max(0,y);
        y = Math.min(y, getHeight() - 50);
    }

    private void drawBars(Graphics2D g2d){
        g2d.setPaint(Color.white);
        g2d.fillRect(barA.getX(),barA.getY(),barA.getWidth(),barA.getHeight());
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
        if(e.getKeyChar()=='w'){
            barA.setY(Math.max(0,barA.getY()-10));
        }
        else if(e.getKeyChar()=='s'){
            barA.setY(Math.min(getHeight()-barA.getHeight(),barA.getY()+10));
            System.out.println(getHeight());
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

    private class Bar{
        private int x;
        private int y;
        private int height;
        private int width;

        public Bar(int x,int y, int width, int height){
            this.x=x;
            this.y=y;
            this.height=height;
            this.width=width;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public void setY(int y){
            this.y=y;
        }

        public int getHeight(){
            return height;
        }

        public int getWidth(){
            return width;
        }

    }

}
