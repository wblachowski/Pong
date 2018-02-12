package com.pong;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class InitialSurface extends JPanel {

    int firstState = 0; //0 - wait, 1-ok, 2-error
    int secondState=-1; //0 - wait, 1 -ok, 2-error
    long configTime=0;

    public void setFirstState(int state){
        firstState=state;
    }

    public void setSecondState(int state){
        secondState=state;
        if(state>0){
            configTime=System.currentTimeMillis();
        }
    }

    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        String firstUrl = "",secondUrl="";
        String firstText = "",secondText="";
        switch (firstState) {
            case 0:
                firstUrl = "src/main/resources/spinner.gif";
                firstText = "Connect first controller";
                break;
            case 1:
                firstUrl = "src/main/resources/ok.png";
                firstText = "First controller connected";
                break;
            case 2:
                firstUrl = "src/main/resources/no.png";
                firstText = "First controller not connected";
                break;
        }
        switch(secondState){
            case 0:
                secondUrl = "src/main/resources/spinner.gif";
                secondText = "Connect second controller";
                break;
            case 1:
                secondUrl = "src/main/resources/ok.png";
                secondText = "Second controller connected";
                break;
            case 2:
                secondUrl = "src/main/resources/no.png";
                secondText = "Second controller not connected";
                break;
        }

        Image icon = new ImageIcon(firstUrl).getImage();
        g.drawImage(icon, getWidth() / 4 - icon.getWidth(null) / 2, getHeight() / 2 - 100, this);

        FontRenderContext frc = new FontRenderContext(null, true, true);
        Font font = new Font("helvetica", Font.BOLD, 23);
        g.setFont(font);
        g.setColor(Color.white);
        g.drawString(firstText, getWidth() / 4 - (int) font.getStringBounds(firstText, frc).getWidth() / 2, getHeight() / 2 + 50);

        if(secondState>=0){
            icon = new ImageIcon(secondUrl).getImage();
            g.drawImage(icon, 3*getWidth() / 4 - icon.getWidth(null) / 2, getHeight() / 2 - 100, this);
            g.drawString(secondText, 3*getWidth() / 4 - (int) font.getStringBounds(secondText, frc).getWidth() / 2, getHeight() / 2 + 50);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
        if(configTime>0 && System.currentTimeMillis()-configTime>1000){
            Pong.getInstance().showMenu();
        }
    }
}
