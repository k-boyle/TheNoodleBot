package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;

public interface Precondition {
    PreconditionResult check(CommandContext context);
}
