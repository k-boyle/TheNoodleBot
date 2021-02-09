package kboyle.noodle.commands.preconditions;

import kboyle.oktane.core.CommandContext;
import kboyle.oktane.core.module.Precondition;
import kboyle.oktane.core.results.precondition.PreconditionResult;

public class FailedPrecondition implements Precondition {
    @Override
    public PreconditionResult run(CommandContext context) {
        return failure("This was meant to fail ergo it worked");
    }
}
