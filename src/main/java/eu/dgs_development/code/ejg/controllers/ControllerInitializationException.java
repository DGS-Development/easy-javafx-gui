package eu.dgs_development.code.ejg.controllers;

/**
 * This exception is thrown when a {@link GuiController} instantiation related error occurs.
 */
public class ControllerInitializationException extends Exception {
    /**
     * Creates a new instance with an error-message.
     * @param message The error-message.
     */
    public ControllerInitializationException(String message) {
        super(message);
    }

    /**
     * Creates a new instance with an error-message and a cause.
     * @param message The error-message.
     * @param cause The cause of the exception.
     */
    public ControllerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
