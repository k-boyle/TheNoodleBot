package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;

public record ExecutionErrorResult(Command command, Exception exception) implements CommandFailedResult {
    @Override
    public String reason() {
        return String.format("An exception was thrown whilst trying to execute %s", command.name());
    }
}
