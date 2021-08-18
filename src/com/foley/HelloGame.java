package com.foley;

import com.foley.core.AbstractGame;
import com.foley.input.Input;

import java.awt.Color;

/**
 * Class description goes here
 *
 * @author Evan Foley
 * @version 31 Jul 2021
 */
public class HelloGame extends AbstractGame {
    // Your instance variables here
    private String message = "Hello, World!";
    private int x = 0;
    private int y = 0;

    @Override
    /**
     * Initializes the game components
     */
    public void initialize() {
        super.initialize();

        // Game specific initialization code
    }

    @Override
    /**
     * Cleans up the game components
     */
    public void terminate() {
        super.terminate();

        // Game specific termination code
    }

    @Override
    /**
     * Checks the input for the game
     */
    public void checkInput() {
        if(input.wasKeyPressed(Input.Keys.ESCAPE)) {
            stop();
        }
        if(input.isKeyPressed(Input.Keys.W)) {
            y -= 5;
        }
        if(input.isKeyPressed(Input.Keys.A)) {
            x -= 5;
        }
        if(input.isKeyPressed(Input.Keys.S)) {
            y += 5;
        }
        if(input.isKeyPressed(Input.Keys.D)) {
            x += 5;
        }
    }

    @Override
    /**
     * Updates the game
     */
    public void updateGame() {
        super.updateGame();

        // Game specific update code
    }

    @Override
    /**
     * Renders the game
     */
    public void renderGame() {
        super.renderGame();
        renderer.lookAt(0, 0);

        // Game specific render code
        renderer.drawText(Color.WHITE, message, x - renderer.stringWidth(message) / 2, y - renderer.stringHeight());
    }
}
