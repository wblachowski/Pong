package com.pong;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.pong.TwoWaySerialComm;

public class Pong extends JFrame implements Runnable {

    private static Pong instance;
    Thread animator;

    MenuSurface menuSurface;
    GameSurface gameSurface;
    SettingsSurface settingsSurface;

    TwoWaySerialComm connectionA;
    TwoWaySerialComm connectionB;

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
                pong.initConnection();
                pong.initUI();
                pong.setVisible(true);
            }
        });
    }

    private void initUI() {
        final int width = 800;
        final int height = 550;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new CardLayout());


        menuSurface = new MenuSurface();
        addKeyListener(menuSurface);

        contentPane.add(menuSurface, "Menu");
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
        try {
            connectionA = new TwoWaySerialComm();
            connectionA.connect("COM5", "COM6");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        try {
            connectionB = new TwoWaySerialComm();
            connectionB.connect("COM3", "COM4");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void showNewGame() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        gameSurface = new GameSurface();
        getContentPane().add(gameSurface, "Game");
        cardLayout.show(getContentPane(), "Game");
        removeKeyListener(menuSurface);
        addKeyListener(gameSurface);
    }

    public void showMenu() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), "Menu");
        removeKeyListener(gameSurface);
        addKeyListener(menuSurface);
    }

    public void showSettings() {
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        if (settingsSurface == null) {
            settingsSurface = new SettingsSurface();
        }
        getContentPane().add(settingsSurface, "Settings");
        cardLayout.show(getContentPane(), "Settings");
        removeKeyListener(menuSurface);
        addKeyListener(settingsSurface);
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

    public TwoWaySerialComm getConnectionA() {
        return connectionA;
    }

    public TwoWaySerialComm getConnectionB() {
        return connectionB;
    }
}
