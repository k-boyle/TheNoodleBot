package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.results.argumentparser.ArgumentParserResult;

public record ExecutionErrorResult(Command command, Exception exception) implements ArgumentParserResult, FailedResult {
    @Override
    public String reason() {
        return String.format("An exception was thrown whilst trying to execute %s", command.name());
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
