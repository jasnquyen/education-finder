package cs1302.api;

import javafx.application.Application;

/**
 * Driver for the {@code ApiApp} class.
 */
public class ApiDriver {

    /**
     * Main entry-point into the application.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        try {
            Application.launch(MainApp.class, args);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            System.err.println("If this is a DISPLAY problem, then your X server connection");
            System.err.println("has likely timed out. This can generally be fixed by logging");
            System.err.println("out and logging back in.");
            System.exit(1);
        } // try
    } // main

} // ApiDriver
