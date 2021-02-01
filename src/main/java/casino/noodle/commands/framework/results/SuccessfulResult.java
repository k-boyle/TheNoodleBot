package casino.noodle.commands.framework.results;

public interface SuccessfulResult extends Result {
    @Override
    default boolean isSuccess() {
        return true;
    }
}
