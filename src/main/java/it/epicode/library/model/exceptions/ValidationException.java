package it.epicode.library.model.exceptions;

public class ValidationException extends LibraryException {
    private final String fieldName;
    private final Object invalidValue;
    private final String validationRule;

    public ValidationException(String fieldName, Object invalidValue, String validationRule) {
        super(
                String.format("Validation failed for field '%s' with value '%s': %s", fieldName, invalidValue, validationRule),
                String.format("Invalid %s. %s", fieldName, validationRule),
                "LIB_VAL_001",
                ErrorSeverity.LOW
        );
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.validationRule = validationRule;
    }

    public String getFieldName() { return fieldName; }
    public Object getInvalidValue() { return invalidValue; }
    public String getValidationRule() { return validationRule; }
}