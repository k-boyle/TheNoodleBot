package casino.noodle.commands.framework.exceptions;

public class InvalidResultException extends RuntimeException {
    public InvalidResultException(Class<?> expected, Class<?> actual) {
        super(String.format("Expected a result of type %s but got %s", expected, actual));
    }
}
