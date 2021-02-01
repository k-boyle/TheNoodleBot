package casino.noodle.commands.framework.results;

public interface FailedResult extends Result {
    default boolean isSuccess() {
        return false;
    }

    String reason();
}
