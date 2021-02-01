package casino.noodle.commands.framework.results.search;

import casino.noodle.commands.framework.results.FailedResult;

public class CommandNotFoundResult implements FailedResult {
    private static class SingletonHolder {
        private static final CommandNotFoundResult INSTANCE = new CommandNotFoundResult();
    }

    public static CommandNotFoundResult get() {
        return SingletonHolder.INSTANCE;
    }

    private CommandNotFoundResult() {
    }

    @Override
    public String reason() {
        return "Command not found";
    }
}