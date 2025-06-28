package it.epicode.library;

/**
 * Main entry point for the Digital Library System.
 * Launches the interactive demo application.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("🏛️ Digital Library Management System");
        System.out.println("Starting application...");

        try {
            LibrarySystemDemo demo = new LibrarySystemDemo();
            demo.runDemo();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            throw e;
        }
    }
}