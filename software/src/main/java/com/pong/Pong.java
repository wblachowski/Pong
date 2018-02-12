package com.pong;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.pong.TwoWaySerialComm;

public class Pong extends JFrame implements Runnable {

    private static Pong instance;
    Thread animator;

    InitialSurface initialSurface;
    MenuSurface menuSurface;
    GameSurface gameSurface;
    SettingsSurface settingsSurface;

    TwoWaySerialComm connectionA;
    TwoWaySerialComm connectionB;

    //settings
    int pointsToWin=10;
    int sensitivityA=5;
    int sensitivityB=5;
    //end of settings

    public static Pong getInstance() {
        if (instance == null) {
            instance = new Pong();
        }
        return instance;
    }

    private Pong() {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Pong pong = Pong.getInstance();
                pong.initUI();
                pong.setVisible(true);
                pong.initConnection();
            }
        });
    }

    private void initUI() {
        final int width = 800;
        final int height = 550;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new CardLayout());

        initialSurface = new InitialSurface();
        contentPane.add(initialSurface,"Initial");

        CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.first(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        setContentPane(contentPane);
        setTitle("Pong");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animator = new Thread(this);
        animator.start();
    }

    private void initConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionA = new TwoWaySerialComm();
                    connectionA.connect("COM5");
                    initialSurface.setFirstState(1);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    initialSurface.setFirstState(2);
                }
                initialSurface.setSecondState(0);
                try {
                    connectionB = new TwoWaySerialComm();
                    connectionB.connect("COM8");
                    initialSurface.setSecondState(1);
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    initialSurface.setSecondState(2);
                }
            }
        }).start();
    }

    public void showNewGame() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        gameSurface = new GameSurface();
        getContentPane().add(gameSurface, "Game");
        cardLayout.show(getContentPane(), "Game");
        updateKeyListener(gameSurface);
    }

    public void showMenu() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        if(menuSurface == null){
            menuSurface=new MenuSurface();
            getContentPane().add(menuSurface,"Menu");
        }
        cardLayout.show(getContentPane(), "Menu");
        updateKeyListener(menuSurface);
    }

    public void showSettings() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        if (settingsSurface == null) {
            settingsSurface = new SettingsSurface();
            getContentPane().add(settingsSurface, "Settings");
        }
        cardLayout.show(getContentPane(), "Settings");
        updateKeyListener(settingsSurface);
    }

    private void updateKeyListener(KeyListener panel){
        for(KeyListener keyListener : getKeyListeners()){
            removeKeyListener(keyListener);
        }
        addKeyListener(panel);
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

    public void controllerButtonClicked(char keyChar){
        KeyEvent key = new KeyEvent(this, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED, keyChar);
        for(KeyListener keyListener: getKeyListeners()){
            keyListener.keyPressed(key);
        }
    }

    public int getPointsToWin(){
        return pointsToWin;
    }

    public int getSensitivityA(){
        return sensitivityA;
    }

    public int getSensitivityB(){
        return sensitivityB;
    }

    public void setSettings(int pointsToWin,int sensitivityA,int sensitivityB){
        this.pointsToWin=pointsToWin;
        this.sensitivityA=sensitivityA;
        this.sensitivityB=sensitivityB;
    }

    public TwoWaySerialComm getConnectionA() {
        return connectionA;
    }

    public TwoWaySerialComm getConnectionB() {
        return connectionB;
    }
}
