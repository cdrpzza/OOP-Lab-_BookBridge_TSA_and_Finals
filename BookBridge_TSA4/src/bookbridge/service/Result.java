package bookbridge.service;

public class Result<T> {

    private final T data;
    private final String message;
    private final boolean found;

    public Result(T data, String message, boolean found) {
        this.data = data;
        this.message = message;
        this.found = found;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFound() {
        return found;
    }
}