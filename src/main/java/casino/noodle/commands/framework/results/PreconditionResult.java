package casino.noodle.commands.framework.results;

public abstract class PreconditionResult implements Result {
    private final boolean success;

    public PreconditionResult(boolean success) {
        this.success = success;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }
}
