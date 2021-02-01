package casino.noodle.commands.framework.results.argumentparser;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.results.FailedResult;

public record FailedArgumentParserResult(Command command, Reason failureReason, int index) implements ArgumentParserResult, FailedResult {
    public enum Reason {
        TOO_FEW_ARGUMENTS,
        TOO_MANY_ARGUMENTS,
        MISSING_QUOTE,
        ;
    }

    @Override
    public String reason() {
        return failureReason().toString();
    }
}
