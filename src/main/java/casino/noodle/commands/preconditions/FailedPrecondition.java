package casino.noodle.commands.preconditions;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.Precondition;
import casino.noodle.commands.framework.results.PreconditionResult;

public class FailedPrecondition implements Precondition {
    @Override
    public PreconditionResult run(CommandContext context, Command command) {
        return failure(command, "This was meant to fail ergo it worked");
    }
}
