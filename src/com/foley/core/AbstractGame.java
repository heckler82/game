package com.foley.core;

import com.foley.graphic.Screen;
import com.foley.graphic.Renderer;
import com.foley.input.Input;

/**
 * Sets up and runs a game loop
 *
 * @author Evan Foley
 * @version 10 Oct 2020
 */
public abstract class AbstractGame implements Game, Runnable{
    private boolean isRunning;
    private final long NS_PER_UPDATE;

    protected Screen screen;
    protected Renderer renderer;
    protected Input input;
    protected final Config config;

    private Thread t;

    /**
     * Creates a new abstract game
     */
    public AbstractGame() {
        config = new Config("config/config.cfg");
        isRunning = false;
        // Update 120 times per second
        NS_PER_UPDATE = 1000000000L / Long.parseLong(config.getProperty("updateInterval"));
    }

    @Override
    /**
     * Initializes the game components
     */
    public void initialize() {
        screen = new Screen(config, this);
        renderer = new Renderer(screen, config);
        input = new Input(screen);
    }

    /**
     * Cleans up the game components
     */
    public void terminate() {
        screen.closeScreen();
    }

    @Override
    /**
     * Starts the game loop
     */
    public void start() {
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    /**
     * Stops the game loop
     */
    public void stop() {
        isRunning = false;
    }

    @Override
    /**
     * Updates the game
     */
    public void updateGame() {
        checkInput();
        input.update();
    }

    @Override
    /**
     * Renders the game
     */
    public void renderGame() {
        // Clear the screen
        renderer.clearScreen();
    }

    @Override
    /**
     * Runs the game loop
     */
    public final void run() {
        // Initialize the game components
        initialize();

        // Timing variables
        long accumulator = 0L;
        long lastTime = System.nanoTime();
        long startTime;

        // Main game loop
        while(isRunning) {
            // Get times
            startTime = System.nanoTime();
            accumulator += startTime - lastTime;
            lastTime = startTime;

            // Update while the elapsed time is greater than the time interval
            while(accumulator >= NS_PER_UPDATE) {
                accumulator -= NS_PER_UPDATE;
                updateGame();
            }

            // Render to the screen
            renderer.beginRender();
            renderGame();
            renderer.endRender();
        }

        // Clean up the game components here
        terminate();
    }
}
