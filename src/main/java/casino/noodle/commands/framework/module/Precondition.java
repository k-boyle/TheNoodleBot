package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;

@FunctionalInterface
public interface Precondition {
    PreconditionResult run(CommandContext context, Command command);

    default PreconditionResult.Success success() {
        return PreconditionResult.Success.get();
    }

    default PreconditionResult.Failure failure(Command command, String reason, Object... args) {
        return new PreconditionResult.Failure(command, String.format(reason, args));
    }
}
