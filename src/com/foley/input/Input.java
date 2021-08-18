package com.foley.input;

import com.foley.graphic.Screen;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.BitSet;

/**
 * Handles input for a window
 *
 * @author Evan Foley
 * @version 02 Jan 2021
 */
public class Input extends KeyAdapter {
    // TODO: Add Mouse capability
    // TODO: Add MouseMotion capability
    // TODO: Add input timing capability
    private BitSet currentKeys;
    private BitSet previousKeys;

    /**
     * Creates a new input
     */
    public Input(Screen screen) {
        currentKeys = new BitSet(600);
        previousKeys = new BitSet(600);
        listenToScreen(screen);
    }

    /**
     * Listens to a screen for input
     *
     * @param screen the screen to listen to
     */
    public void listenToScreen(Screen screen) {
        screen.getGameWindow().addKeyListener(this);
    }

    /**
     * Returns true if the specified key is currently being pressed
     *
     * @param key the key to test
     * @return true if the key is currently being pressed
     */
    public boolean isKeyPressed(Keys key) {
        return currentKeys.get(key.getKeyCode());
    }

    /**
     * Returns true if the specified key is not currently being pressed
     *
     * @param key the key to test
     * @return true if the key is not currently being pressed
     */
    public boolean isKeyReleased(Keys key) {
        return !currentKeys.get(key.getKeyCode());
    }

    /**
     * Returns true if a key is not currently pressed, but was being held down the previous frame
     *
     * @param key the key to test
     * @return true if the key is not pressed, but was being held down the previous frame
     */
    public boolean wasKeyPressed(Keys key) {
        return !currentKeys.get(key.getKeyCode()) && previousKeys.get(key.getKeyCode());
    }

    /**
     * Returns true if a key is currently pressed, but was not being held down the previous frame
     *
     * @param key the key to test
     * @return true if the key is pressed, but was not being held down the previous frame
     */
    public boolean wasKeyReleased(Keys key) {
        return currentKeys.get(key.getKeyCode()) && !previousKeys.get(key.getKeyCode());
    }

    /**
     * Updates the state of the input
     */
    public void update() {
        // Clear the previous state, and set it to the current state
        previousKeys.clear();
        for(int i = currentKeys.nextSetBit(0); i != -1; i = currentKeys.nextSetBit(i + 1)) {
            previousKeys.set(i);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!currentKeys.get(e.getKeyCode())) {
            currentKeys.set(e.getKeyCode());
        }
        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        currentKeys.clear(e.getKeyCode());
        e.consume();
    }

    /**
     * Utility container used for keyboard input
     */
    public enum Keys {
        A (KeyEvent.VK_A), B (KeyEvent.VK_B), C (KeyEvent.VK_C), D (KeyEvent.VK_D), E (KeyEvent.VK_E), F (KeyEvent.VK_F),
        G (KeyEvent.VK_G), H (KeyEvent.VK_H), I (KeyEvent.VK_I), J (KeyEvent.VK_J), K (KeyEvent.VK_K), L (KeyEvent.VK_L),
        M (KeyEvent.VK_M), N (KeyEvent.VK_N), O (KeyEvent.VK_O), P (KeyEvent.VK_P), Q (KeyEvent.VK_Q), R (KeyEvent.VK_R),
        S (KeyEvent.VK_S), T (KeyEvent.VK_T), U (KeyEvent.VK_U), V (KeyEvent.VK_V), W (KeyEvent.VK_W), X (KeyEvent.VK_X),
        Y (KeyEvent.VK_Y), Z (KeyEvent.VK_Z), ENTER (KeyEvent.VK_ENTER), SPACE (KeyEvent.VK_SPACE), ESCAPE (KeyEvent.VK_ESCAPE);

        private final int key;

        /**
         * Creates a new key
         *
         * @param key the key code to use
         */
        Keys(int key) {
            this.key = key;
        }

        /**
         * Gets the key code for the enum
         *
         * @return the key code for the enum
         */
        public int getKeyCode() {
            return key;
        }
    }

    /**
     * Utility container used for mouse input
     */
    public enum Button {
        MOUSE_LEFT (MouseEvent.BUTTON1), MOUSE_RIGHT (MouseEvent.BUTTON2), MOUSE_MIDDLE(MouseEvent.BUTTON3);

        private final int button;

        /**
         * Creates a new button
         *
         * @param button the button code to use
         */
        Button(int button) {
            this.button = button;
        }

        /**
         * Gets the button code for the enum
         *
         * @return the button code for the enum
         */
        public int getButton() {
            return button;
        }
    }
}
