package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;

public interface TypeParserResult<T> extends Result {
    record Success<T>(T value) implements TypeParserResult<T> {
        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record Failure<T>(Command command, String reason) implements CommandFailedResult, TypeParserResult<T> {
    }
}
