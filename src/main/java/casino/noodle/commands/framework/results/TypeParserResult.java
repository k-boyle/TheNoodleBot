package casino.noodle.commands.framework.results;

public interface TypeParserResult<T> extends Result {
    record Success<T>(T value) implements TypeParserResult<T> {
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record Failure<T>(String reason) implements FailedResult, TypeParserResult<T> {
    }
}
