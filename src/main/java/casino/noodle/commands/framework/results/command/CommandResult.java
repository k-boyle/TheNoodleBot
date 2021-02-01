package casino.noodle.commands.framework.results.command;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.results.Result;

public interface CommandResult extends Result {
    Command command();
}
