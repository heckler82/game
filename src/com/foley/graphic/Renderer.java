package com.foley.graphic;

import com.foley.core.Config;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders 2D-graphics to the screen
 *
 * @author Evan Foley
 * @version 02 Jan 2021
 */
public class Renderer {
    private Screen screen;
    private Graphics2D g;
    private BufferStrategy strategy;
    private Color clearColor;
    private Stack<Color> colorStack;
    private Stack<AffineTransform> matrices;
    private Map<Integer, Font> fonts;
    private int HALF_WIDTH;
    private int HALF_HEIGHT;
    private Font mainFont;

    /**
     * Creates a new renderer
     *
     * @param screen the screen context
     */
    public Renderer(Screen screen) {
        this.screen = screen;
        HALF_WIDTH = screen.getWidth() / 2;
        HALF_HEIGHT = screen.getHeight() / 2;
        colorStack = new Stack<>();
        matrices = new Stack<>();
        strategy = screen.getGameWindow().getBufferStrategy();
        fonts = new HashMap<>();
        mainFont = getNewFont("Arial", Font.PLAIN, 30);
    }

    /**
     * Creates a new renderer
     *
     * @param screen the screen context
     */
    public Renderer(Screen screen, Config config) {
        this(screen);
        // Attempt to get clear color
        clearColor = stringToColor(config.getProperty("clearColor"));
        if(validateFontInfo(config.getProperty("font-family"), config.getProperty("font-style"), config.getProperty("font-size"))) {
            mainFont = getNewFont(config.getProperty("font-family"),
                    getCombinedInteger(config.getProperty("font-style")),
                    Integer.parseInt(config.getProperty("font-size")));
        }
    }

    /**
     * Preps the renderer for rendering operations
     */
    public void beginRender() {
        if(strategy != null) {
            g = (Graphics2D) strategy.getDrawGraphics();
            g.setFont(mainFont);
            setToIdentity();
        }
    }

    /**
     * Displays everything that was rendered to the screen
     */
    public void endRender() {
        strategy.show();
    }

    /**
     * Sets the transformation matrix in the renderer to an identity matrix
     */
    public void setToIdentity() {
        g.setTransform(new AffineTransform());
    }

    /**
     * Pushes the current transformation matrix onto the stack
     */
    public void pushMatrix() {
        matrices.push(g.getTransform());
    }

    /**
     * Loads the last saved matrix into the renderer
     */
    public void popMatrix() {
        if(matrices.isEmpty()) {
            System.out.println("INFORMATIONAL: There is no available matrix on the stack to pop");
            return;
        }
        g.setTransform(matrices.pop());
    }

    /**
     * Centers the view at the specified coordinates
     *
     * @param x the coordinate
     * @param y the y coordinate
     */
    public void lookAt(int x, int y) {
        g.translate(HALF_WIDTH - x, HALF_HEIGHT - y);
    }

    /**
     * Sets the clear color
     *
     * @param color the color to use for clearing the screen
     */
    public void setClearColor(Color color) {
        clearColor = color;
    }

    /**
     * Sets the font for use in rendering text
     *
     * @param font the font to use
     */
    public void setFont(Font font) {
        g.setFont(font);
    }

    /**
     * Gets a specific font of a size and type
     *
     * @param name the name of the font
     * @param style the style of the font
     * @param size the size of the font
     * @return the created font
     */
    public Font getNewFont(String name, int style, int size) {
        // Ensure style and size are positive non-zero integers. Style is also bounded
        if(style < 0 || style > 3) {
            System.out.printf("INFORMATIONAL: [%d] is not a valid style. Valid styles are 0 for PLAIN, 1 for BOLD, and 2 for ITALIC. The default font will be used\n", style);
            return getNewFont("Arial", Font.PLAIN, 30);
        }
        if(size <= 0) {
            System.out.printf("INFORMATIONAL: [%d] is not a valid size. Size must be an integer greater than 0. The default font will be used\n", size);
            return getNewFont("Arial", Font.PLAIN, 30);
        }

        // Determine if font has already been used. If yes, pull from cache. Otherwise, create a new Font and add to cache
        int key = Objects.hash(name, style, size);
        if(fonts.containsKey(key)) {
            return fonts.get(key);
        }
        Font font = new Font(name, style, size);
        fonts.put(key, font);
        return font;
    }

    /**
     * Gets the height of text for the current font
     */
    public int stringHeight() {
        return g.getFontMetrics().getHeight() / 2;
    }

    /**
     * Gets the height of text for the specified font
     *
     * @return the height of the font
     */
    public int stringHeight(Font font) {
        return g.getFontMetrics(font).getHeight() / 2;
    }

    /**
     * Gets the width of a string when using a specified font
     *
     * @param str the string to measure
     * @return the width of the string
     */
    public int stringWidth(String str) {
        return g.getFontMetrics().stringWidth(str);
    }

    /**
     * Gets the width of a string when using a specified font
     *
     * @param str the string to measure
     * @param font the font to use
     * @return the width of the string
     */
    public int stringWidth(String str, Font font) {
        return g.getFontMetrics(font).stringWidth(str);
    }

    /**
     * Clears the screen
     */
    public void clearScreen() {
        // Save current color, and set to clear color
        colorStack.push(g.getColor());
        g.setColor(clearColor);

        // Clear the screen and set color back to previously saved color
        g.fillRect(0, 0, screen.getGameWindow().getWidth(), screen.getGameWindow().getHeight());
        g.setColor(colorStack.pop());
    }

    /**
     * Draws a string at a position on the screen
     *
     * @param color the color to make the text
     * @param text the text to draw
     * @param x the x position on the screen
     * @param y the y position on the screen
     */
    public void drawText(Color color, String text, int x, int y) {
        g.setColor(color);
        g.drawString(text, x, y);
    }

    /**
     * Draws an image to the screen
     *
     * @param img The image
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void drawImage(Image img, double x, double y) {
        // Push the current transformations onto the stack, and set to identity
        pushMatrix();
        setToIdentity();

        // Perform translations, draw, then pop the old transformation matrix off the stack
        g.translate(x, y);
        g.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null) / 2, null);
        popMatrix();
    }

    /**
     * Draws a texture to the screen
     *
     * @param texture the texture
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void drawTexture(Texture texture, double x, double y) {
        // Push the current transformations onto the stack, and set to identity
        pushMatrix();
        g.translate(x, y);

        // Perform translations, draw, then pop the old transformation matrix off the stack
        g.drawImage(texture.getImage(), -texture.getWidth() / 2, -texture.getHeight() / 2, null);
        popMatrix();
    }

    /**
     * Converts a string to a color
     *
     * @param str the string
     * @return the color value matching the named reference
     */
    public static Color stringToColor(String str) {
        // Ensure a name was given to decode
        if(str == null) {
            System.out.println("INFORMATIONAL: Null color value passed. Defaulting to black clear color");
            return Color.BLACK;
        }

        // Attempt to get from hex or octal
        try {
            return Color.decode(str);
        } catch(NumberFormatException e) {
            // Attempt to get by name
            try {
                final Field field = Color.class.getField(str);
                return (Color)field.get(null);
            } catch(NoSuchFieldException | IllegalAccessException ex) {
                System.out.printf("INFORMATIONAL: Could not find matching color \"%s\". Defaulting to black clear color\n", str);
                return Color.BLACK;
            }
        }
    }

    /**
     * Checks if all information needed to create a font is valid
     *
     * @param name the name of the font family
     * @param style the style of the font
     * @param size the size of the font
     * @return {@code true} if all data can create a valid font
     */
    public boolean validateFontInfo(String name, String style, String size) {
        // Name must not be null, and must exist on the local machine
        if(name == null || !validateFontFamilyName(name)) {
            System.out.printf("INFORMATIONAL: '%s' is not a valid font family name and cannot be created. The default font will be used\n", name);
            return false;
        }

        // Style and size must be numeric
        if(style == null || !Pattern.matches("([0-9])(\\s?\\|\\s?[0-9])?", style)) {
            System.out.printf("INFORMATIONAL: '%s' is not a valid font style. Ensure it is in integer format. The default font will be used\n", style);
            return false;
        }
        if(size == null || !Pattern.matches("[0-9]+", size)) {
            System.out.printf("INFORMATIONAL: '%s' is not a valid font size. Ensure it is in integer format. The default font will be used\n", style);
            return false;
        }
        return true;
    }

    /**
     * Validates that a string can be used as a font on the local machine
     *
     * @param name the name of the font family
     * @return true if {@code name} is a valid font family name on the local machine
     */
    public boolean validateFontFamilyName(String name) {
        // Determine if name exists as a font on the local machine
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for(String s : ge.getAvailableFontFamilyNames()) {
            if(s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a single integer from a formatted string
     *
     * @param style the formatted string
     * @return an integer that is either a single value, or the OR of two values
     */
    private int getCombinedInteger(String style) {
        Pattern p = Pattern.compile("([0-9])(\\s?\\|\\s?([0-9]))");
        Matcher m = p.matcher(style);
        if(m.find()) {
            return (Integer.parseInt(m.group(1)) | Integer.parseInt(m.group(3)));
        }
        return Integer.parseInt(style);
    }
}
