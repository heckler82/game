package com.foley.graphic;

import com.foley.core.Config;
import com.foley.core.Game;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.InputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;

/**
 * Abstracts a screen
 *
 * @author Evan Foley
 * @version 02 Jan 2021
 */
public class Screen {
    private JFrame frame;
    private WindowMode windowMode;
    private Game game;
    private Rectangle bounds;

    /**
     * Allows for different window modes to be used
     */
    public enum WindowMode {
        WINDOWED,
        BORDERLESS,
        FULLSCREEN;
    }

    /**
     * Creates a new screen
     *
     * @param width the width of the window
     * @param height the height of the window
     * @param g the game object
     */
    public Screen(int width, int height, Game g) {
        this("My Game", width, height, g);
    }

    /**
     * Creates a new screen
     *
     * @param title the title of the window
     * @param width the width of the window
     * @param height the height of the window
     * @param g the game object
     */
    public Screen(String title, int width, int height, Game g) {
        this(title, width, height, WindowMode.WINDOWED, g);
    }

    /**
     * Creates a new screen
     *
     * @param title the title of the window
     * @param width the width of the window
     * @param height the height of the window
     * @param mode the mode of the window
     * @param g the game object
     */
    public Screen(String title, int width, int height, WindowMode mode, Game g) {
        windowMode = mode;
        game = g;
        createWindow(title, width, height, mode);
    }

    /**
     * Creates a new screen
     *
     * @param config the config file
     * @param g the game object
     */
    public Screen(Config config, Game g) {
        // Ensure proper window mode is obtained
        windowMode = WindowMode.WINDOWED;
        try {
            windowMode = WindowMode.valueOf(config.getProperty("mode"));
        } catch(IllegalArgumentException e) {
            System.out.println("INFORMATIONAL: Invalid window mode. Will use default \"WINDOWED\" mode");
        }

        game = g;

        // Attempt to find a custom window icon
        Image img = null;
        try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("images/icons/" + config.getProperty("windowIcon"))) {
            img = ImageIO.read(is);
        } catch(IOException | IllegalArgumentException e) {
            System.out.printf("INFORMATIONAL: Could not find image \"%s\". Window will have default Java icon\n", config.getProperty("windowIcon"));
        }

        // Parse display width
        int width = 0;
        String sVal = config.getProperty("width");
        if("system".equalsIgnoreCase(sVal)) {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            width = device.getDisplayMode().getWidth();
        } else {
            try {
                width = Integer.parseInt(sVal);
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Could not parse display width. The value \"width\" in the configuration file must be numeric. The program will now exit.",
                        "Invalid Width Value", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        // Parse display height
        int height = 0;
        sVal = config.getProperty("height");
        if("system".equalsIgnoreCase(sVal)) {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            height = device.getDisplayMode().getHeight();
        } else {
            try {
                height = Integer.parseInt(sVal);
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Could not parse display height. The value \"height\" in the configuration file must be numeric . The program will now exit.",
                        "Invalid Height Value", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        // Get title
        String title = config.getProperty("title");
        if(title == null || title.isBlank() || title.isEmpty()) {
            title = "My Game";
        }

        // Create and show the game window
        createWindow(title, width, height, windowMode, img);
    }

    /**
     * Creates a new window to be used with the screen
     *
     * @param title the title of the window
     * @param width the width of the window
     * @param height the height of the window
     * @param mode the mode of the window
     */
    private void createWindow(String title, int width, int height, WindowMode mode) {
        createWindow(title, width, height, mode, null);
    }

    /**
     * Creates a new window to be used with the screen
     *
     * @param title the title of the window
     * @param width the width of the window
     * @param height the height of the window
     * @param mode the mode of the window
     * @param icon the icon of the window
     */
    private void createWindow(String title, int width, int height, WindowMode mode, Image icon) {
        if(width < 0 || height < 0) {
            JOptionPane.showMessageDialog(null, "Display height and width cannot be negative. Check the configuration file. The program will now exit.", "Negative Display Parameters", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Setup basic window parameters
        windowMode = mode;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // WindowListener that is added will properly stop game loop and close the window
        frame.setIgnoreRepaint(true);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Attempt to set the window icon
        if(icon != null) {
            frame.setIconImage(icon);
        }

        // Handle different window modes
        if(windowMode != WindowMode.WINDOWED) {
            frame.setUndecorated(true);

            // Go fullscreen if required
            if(windowMode == WindowMode.FULLSCREEN) {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                // If fullscreen is supported, then go into fullscreen mode, otherwise, window will default back to windowed mode
                if(device.isFullScreenSupported()) {
                    device.setFullScreenWindow(frame);
                } else {
                    System.out.println("INFORMATIONAL: Fullscreen is not supported on this device. Defaulting to windowed mode");
                    windowMode = WindowMode.WINDOWED;
                    frame.setUndecorated(false);
                }
            }
        }

        // Ensure game thread is stopped if window is closed
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                game.stop();
            }
        });

        // Handle window resize events
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // Fix up aspect ratio here
            }
        });

        // Get the drawing area bounds. Insets are the border and title bar for a decorated window. Getting the insets will ensure you can draw on a visible surface
        Insets insets = frame.getInsets();
        bounds = new Rectangle(insets.left, insets.top, frame.getWidth() - (insets.left + insets.right), frame.getHeight() - (insets.top + insets.bottom));

        // Display the window
        frame.setVisible(true);

        // Ensure buffer strategy is created before window is drawn to
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            frame.createBufferStrategy(2);
            latch.countDown();
        });

        // Wait for buffer strategy to be created
        try {
            latch.await();
        } catch(InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Thread was interrupted while awaiting the CountDownLatch to unlock. The program will now exit.", "Buffer Strategy Interrupted", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Change the window mode
     *
     * @param mode the mode to switch to
     */
    public boolean changeMode(WindowMode mode) {
        if(windowMode == mode) {
            System.out.printf("INFORMATIONAL: Selected window mode [%s] is the current mode of the screen. No further action will be taken\n", mode.toString());
            return false;
        }
        System.out.printf("INFORMATIONAL: Changing mode to %s\n", mode.toString());

        // Come out of fullscreen mode if necessary
        if(windowMode == WindowMode.FULLSCREEN) {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(null);
        }

        // Save the current window, then hide it
        JFrame oldFrame = frame;
        oldFrame.setVisible(false);

        // Create and display the new window
        Image icon = oldFrame.getIconImage();
        createWindow(oldFrame.getTitle(), oldFrame.getWidth(), oldFrame.getHeight(), mode, icon);

        // Dispose of the saved window
        oldFrame.dispose();

        return true;
    }

    /**
     * Determines if the screen is in fullscreen
     *
     * @return true if the screen is currently in full screen mode
     */
    public boolean isFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return device.getFullScreenWindow() != null;
    }

    /**
     * Closes down the screen
     */
    public void closeScreen() {
        // Shut down full screen is necessary
        if(isFullScreen()) {
            GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            device.setFullScreenWindow(null);
        }
        // Dispose the game window
        frame.dispose();
    }

    /**
     * Gets the width of the screen
     *
     * @return the width of the screen
     */
    public int getWidth() {
        return frame.getWidth();
    }

    /**
     * Gets the height of the screen
     *
     * @return the height of the screen
     */
    public int getHeight() {
        return frame.getHeight();
    }

    /**
     * Gets the game window the screen is using
     *
     * @return the game window
     */
    public Window getGameWindow() {
        return frame;
    }

    /**
     * Gets the portion of the window that can be drawn on and not be hidden by decorations
     *
     * @return the visible drawing area of the game window
     */
    public Rectangle getVisibleDrawingArea() {
        return bounds;
    }
}
