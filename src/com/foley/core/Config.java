package com.foley.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * Utility class for working with Java Properties API
 *
 * @author Evan Foley
 * @version 06 Jan 2021
 */
public class Config {
    private static final String INVALID_KEY = "INVALID_KEY";
    private Properties properties;

    /**
     * Creates a new config
     *
     * @param path the path to the configuration file
     */
    public Config(String path) {
        properties = new Properties();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(path)) {
            properties.load(is);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "There was an error reading from the configuration file.Please ensure the file is not open. The program will now exit.", "File Read Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "The configuration file cannot be found. The program will now exit.", "No Configuration File", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Gets a property from the config file
     *
     * @param key the key
     * @return the value associated with the key
     */
    public String getProperty(String key) {
        String ret = properties.getProperty(key);
        if(ret == null) {
            System.out.println("INFORMATIONAL: Requested property does not have a value associated with it");
            return INVALID_KEY;
        }
        return ret;
    }
}
