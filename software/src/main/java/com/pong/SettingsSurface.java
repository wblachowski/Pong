package com.pong;

import com.sun.javafx.scene.layout.region.Margins;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

public class SettingsSurface extends JPanel implements KeyListener {

    private int state=0;

    String title="Settings";
    String options[]={"Points to win","Sensivity A","Sensivity B"};
    String values[];

    public SettingsSurface(){
        values=new String[]{Integer.toString(Pong.getInstance().getPointsToWin()),
                Integer.toString(Pong.getInstance().getSensitivityA()),
                Integer.toString(Pong.getInstance().getSensitivityB())};
    }

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
            Rectangle2D stringBounds = font.getStringBounds(options[i]+"   "+values[i], frc);
            g.drawString(options[i]+"   "+values[i], (int) (getWidth() / 2 - stringBounds.getWidth() / 2), 200 + i * 60);
        }
        Image icon = new ImageIcon("src/main/resources/spinner.gif").getImage();
        g.drawImage(icon, 0, 0, this);

    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar()=='y')Pong.getInstance().showMenu();
        if(e.getKeyChar()=='w')state--;
        if(e.getKeyChar()=='s')state++;
        if(e.getKeyChar()=='a'){
           values[state]=Integer.toString(Integer.parseInt(values[state])-1);
        }
        if(e.getKeyChar()=='d'){
            values[state]=Integer.toString(Integer.parseInt(values[state])+1);
        }

        if(state==options.length)state=0;
        if(state==-1)state=options.length-1;
        if(values[state].equals("0"))values[state]="1";
        if(values[state].equals("11"))values[state]="10";

        Pong.getInstance().setSettings(Integer.parseInt(values[0]),Integer.parseInt(values[1]),Integer.parseInt(values[2]));
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
