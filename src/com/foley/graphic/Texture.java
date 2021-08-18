package com.foley.graphic;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * A base texture used for displaying graphics
 *
 * @author Evan Foley
 * @version 05 Jan 2021
 */
public class Texture {
    private BufferedImage img;

    /**
     * Creates a new texture
     *
     * @param img the image to use
     */
    private Texture(BufferedImage img) {
        this.img = img;
    }

    /**
     * Gets the image used in the texture
     *
     * @return the image used in the texture
     */
    public Image getImage() {
        return img;
    }

    /**
     * Gets the width of the texture
     *
     * @return the width of the texture
     */
    public int getWidth() {
        return img.getWidth();
    }

    /**
     * Gets the height of the texture
     *
     * @return the height of the texture
     */
    public int getHeight() {
        return img.getHeight();
    }

    /**
     * Gets a smaller image from the parent image
     *
     * @param x the starting x coordinate
     * @param y the starting y coordinate
     * @param w the width of the sub image
     * @param h the height of the sub image
     * @return the image defined by the passed parameters
     */
    public BufferedImage getSubImage(int x, int y, int w, int h) {
        return img.getSubimage(x, y, w, h);
    }

    /**
     * Gets an image from a path
     *
     * @param path the path to the image
     * @return the image stored at the specified path
     */
    public static BufferedImage getImage(String path) {
        try (InputStream is = Texture.class.getClassLoader().getResourceAsStream(path)){
            return ImageIO.read(is);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets a texture from a path
     *
     * @param path the path
     * @return a texture that uses the image stored at the specified path
     */
    public static Texture getTexture(String path) {
        BufferedImage img = getImage(path);
        if(img != null) {
            return new Texture(img);
        }
        return null;
    }
}
