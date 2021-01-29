package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;

public interface CommandFailedResult extends FailedResult {
    Command command();
}
