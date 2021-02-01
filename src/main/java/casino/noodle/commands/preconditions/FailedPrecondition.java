package casino.noodle.commands.preconditions;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.Precondition;
import casino.noodle.commands.framework.results.precondition.PreconditionResult;

public class FailedPrecondition implements Precondition {
    @Override
    public PreconditionResult run(CommandContext context) {
        return failure("This was meant to fail ergo it worked");
    }
}
