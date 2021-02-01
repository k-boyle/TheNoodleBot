package casino.noodle.commands.framework.results.command;

import casino.noodle.commands.framework.module.Command;

public record CommandMessageResult(Command command, String message) implements CommandSuccessfulResult {
    public static CommandMessageResult from(Command command, String message) {
        return new CommandMessageResult(command, message);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
