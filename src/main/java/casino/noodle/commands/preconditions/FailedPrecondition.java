package casino.noodle.commands.preconditions;

import kboyle.octane.core.CommandContext;
import kboyle.octane.core.module.Precondition;
import kboyle.octane.core.results.precondition.PreconditionResult;

public class FailedPrecondition implements Precondition {
    @Override
    public PreconditionResult run(CommandContext context) {
        return failure("This was meant to fail ergo it worked");
    }
}
