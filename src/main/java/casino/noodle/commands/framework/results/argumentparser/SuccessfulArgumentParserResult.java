package casino.noodle.commands.framework.results.argumentparser;

import casino.noodle.commands.framework.results.SuccessfulResult;

public record SuccessfulArgumentParserResult(Object[] parsedArguments) implements SuccessfulResult, ArgumentParserResult {
    private static class SingletonHolder {
        public static final SuccessfulArgumentParserResult EMPTY = new SuccessfulArgumentParserResult(new Object[0]);
    }

    public static SuccessfulArgumentParserResult empty() {
        return SuccessfulArgumentParserResult.SingletonHolder.EMPTY;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
