package com.foley;

import com.foley.core.Game;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Runs the main program
 *
 * @author Evan Foley
 * @version 10 Oct 2020
 */
public class Driver {
    /**
     * Main entry-point for the program
     *
     * @param args any command line arguments for the program
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | ClassCastException e) {
            System.out.println("INFORMATIONAL: Could not set the system default look and feel. Continuing with java default");
        }
        Game g = new HelloGame();
        g.start();
    }
}
