package it.epicode.library.model.exceptions;

public class DataPersistenceException extends LibraryException {
    private final String operation;
    private final String fileName;

    public DataPersistenceException(String operation, String fileName, Throwable cause) {
        super(
                String.format("Data persistence failed during %s operation on file %s", operation, fileName),
                cause,
                "A technical error occurred while saving data. Please try again.",
                "LIB_DATA_001",
                ErrorSeverity.HIGH
        );
        this.operation = operation;
        this.fileName = fileName;
    }

    public String getOperation() { return operation; }
    public String getFileName() { return fileName; }
}