package com.pong;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Pong extends JFrame {

    private static Pong instance;

    public static Pong getInstance(){
        if(instance==null){
            instance=new Pong();
        }
        return instance;
    }

    private Pong() {
    }

    MenuSurface menuSurface;
    GameSurface gameSurface;

    private void initUI() {
        final int width = 800;
        final int height = 550;

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new CardLayout());


        menuSurface = new MenuSurface();
        addKeyListener(menuSurface);

        gameSurface = new GameSurface();

        contentPane.add(menuSurface,"Menu");
        contentPane.add(gameSurface,"Game");
        CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        cardLayout.first(contentPane);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        setContentPane(contentPane);
        setTitle("Pong");
        setSize(width,height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showNewGame(){
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        gameSurface=new GameSurface();
        getContentPane().add(gameSurface,"Game");
        cardLayout.show(getContentPane(),"Game");
        removeKeyListener(menuSurface);
        addKeyListener(gameSurface);
    }

    public void showMenu(){
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(),"Menu");
        removeKeyListener(gameSurface);
        addKeyListener(menuSurface);
    }


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Pong pong = Pong.getInstance();
                pong.initUI();
                pong.setVisible(true);
            }
        });
    }
}
