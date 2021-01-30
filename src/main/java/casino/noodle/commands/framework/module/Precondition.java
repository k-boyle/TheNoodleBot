package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.precondition.FailurePreconditionResult;
import casino.noodle.commands.framework.results.precondition.PreconditionResult;
import casino.noodle.commands.framework.results.precondition.SuccessfulPreconditionResult;

@FunctionalInterface
public interface Precondition {
    PreconditionResult run(CommandContext context);

    default SuccessfulPreconditionResult success() {
        return SuccessfulPreconditionResult.get();
    }

    default FailurePreconditionResult failure(String reason, Object... args) {
        return new FailurePreconditionResult(String.format(reason, args));
    }
}
