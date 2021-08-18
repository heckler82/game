package com.foley.core;

/**
 * Provides expected behavior for a game to follow
 *
 * @author Evan Foley
 * @version 31 Jul 2021
 */
public interface Game {
    /**
     * Starts the game loop
     */
    void start();

    /**
     * Stop the game loop
     */
    void stop();

    /**
     * Pauses the game. Default behavior is to do nothing
     */
    default void pause() {}

    /**
     * Resumes the game. Default behavior is to do nothing
     */
    default void resume() {}

    /**
     * Initializes the game components
     */
    void initialize();

    /**
     * Cleans up the game components
     */
    void terminate();

    /**
     * Checks the input for the game
     */
    void checkInput();

    /**
     * Updates the game
     */
    void updateGame();

    /**
     * Renders the game
     */
    void renderGame();
}
