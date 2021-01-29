package casino.noodle.commands.framework.results;

import casino.noodle.commands.framework.module.Command;

public interface ArgumentParserResult extends Result {
    record Success(Object[] parsedArguments) implements ArgumentParserResult {
        private static class SingletonHolder {
            public static final Success EMPTY = new Success(new Object[0]);
        }

        public static Success empty() {
            return SingletonHolder.EMPTY;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }
    }

    record ArgumentMismatch(Command command, Reason failureReason) implements ArgumentParserResult, CommandFailedResult {
        public enum Reason {
            TOO_FEW_ARGUMENTS,
            TOO_MANY_ARGUMENTS
        }

        @Override
        public String reason() {
            return failureReason().toString();
        }
    }

    record Exception(Command command, java.lang.Exception exception) implements ArgumentParserResult, CommandFailedResult {
        @Override
        public String reason() {
            return String.format("An exception was thrown whilst trying to execute %s", command.name());
        }
    }

    record ParseFailed(Command command, Class<?> clazz, String input, TypeParserResult.Failure failure) implements ArgumentParserResult, CommandFailedResult {
        @Override
        public String reason() {
            return String.format("Failed to parse %s as %s due to %s", input, clazz, failure.reason());
        }
    }
}
