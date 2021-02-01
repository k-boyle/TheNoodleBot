package casino.noodle.commands.framework.results.precondition;

import casino.noodle.commands.framework.results.SuccessfulResult;

public class SuccessfulPreconditionResult implements SuccessfulResult, PreconditionResult {
    private static class SingletonHolder {
        private static SuccessfulPreconditionResult INSTANCE = new SuccessfulPreconditionResult();
    }

    public static SuccessfulPreconditionResult get() {
        return SingletonHolder.INSTANCE;
    }

    private SuccessfulPreconditionResult() {
    }
}
