package casino.noodle.commands.framework.results;

public class CommandMessageResult extends CommandResult {
    private final String message;

    public CommandMessageResult(String message) {
        super(true);
        this.message = message;
    }

    public static CommandMessageResult from(String message) {
        return new CommandMessageResult(message);
    }
}
