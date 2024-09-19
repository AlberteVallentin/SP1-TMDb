package dat.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaException extends RuntimeException {

    // Correct logger instantiation
    private static final Logger logger = LoggerFactory.getLogger(JpaException.class);

    // Constructor that accepts only message
    public JpaException(String message) {
        super(message);
        writeToLog(message, null);
    }

    // Constructor that accepts message and cause (Throwable)
    public JpaException(String message, Throwable cause) {
        super(message, cause);
        writeToLog(message, cause);
    }

    // Log the exception message and cause (if available)
    private void writeToLog(String message, Throwable cause) {
        if (cause != null) {
            logger.error(message, cause);  // Log both message and cause
        } else {
            logger.error(message);  // Log only message
        }
    }
}
