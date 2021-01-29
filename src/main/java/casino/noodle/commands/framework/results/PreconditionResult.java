package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;

public interface PreconditionResult extends Result {
    record Success() implements PreconditionResult {
        private static final Success INSTANCE = new Success();

        @Override
        public boolean isSuccess() {
            return true;
        }

        public static Success get() {
            return INSTANCE;
        }
    }

    record Failure(Command command, String reason) implements CommandFailedResult, PreconditionResult {
    }
}
