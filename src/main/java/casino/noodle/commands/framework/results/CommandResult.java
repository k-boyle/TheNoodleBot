package casino.noodle.commands.framework.results;

public abstract class CommandResult implements Result {
    private final boolean success;

    protected CommandResult(boolean success) {
        this.success = success;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }
}
