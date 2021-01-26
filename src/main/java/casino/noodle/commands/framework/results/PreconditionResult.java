package casino.noodle.commands.framework.results;

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

    record Failure(String reason) implements FailedResult, PreconditionResult {
    }
}
