package com.pong;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
public class Surface extends JPanel implements ActionListener {
    public static class Velocity{
        public int value;
    }

    private final int DELAY = 15;
    private Timer timer;

    public Velocity vel = new Velocity();

    public Surface() {
        vel.value=0;
        initTimer();
        try {
            (new TwoWaySerialComm(vel)).connect("COM4");
        }catch(Exception ex){}
    }


    public void setStartingPoint(int y){
        this.y=y-50;
    }

    private void initTimer() {
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public Timer getTimer() {

        return timer;
    }
    private int y;
    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0,0,getWidth(),getHeight());

        g2d.setPaint(Color.white);
        g2d.fillRect(10,y,20,50);

        y+=vel.value;
        y=Math.max(0,y);
        y = Math.min(y, getHeight() - 50);



    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
