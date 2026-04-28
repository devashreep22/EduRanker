package model;

public class OperationResult {
    public boolean success;
    public String message;

    public static OperationResult success(String message) {
        OperationResult result = new OperationResult();
        result.success = true;
        result.message = message;
        return result;
    }

    public static OperationResult failure(String message) {
        OperationResult result = new OperationResult();
        result.success = false;
        result.message = message;
        return result;
    }
}
