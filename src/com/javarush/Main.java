package com.javarush;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Model m = new Model();
        Controller c = new Controller(m);
//        System.out.println("2048");
//        m.testMethod();

        JFrame game = new JFrame();
        game.setTitle("2048");
        game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        game.setSize(450,500);
        game.setResizable(false);

        game.add(c.getView());

        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}
