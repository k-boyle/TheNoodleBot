package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.typeparser.FailedTypeParserResult;
import casino.noodle.commands.framework.results.typeparser.SuccessfulTypeParserResult;
import casino.noodle.commands.framework.results.typeparser.TypeParserResult;

@FunctionalInterface
public interface TypeParser<T> {
    TypeParserResult parse(CommandContext context, String input);

    default SuccessfulTypeParserResult<T> success(T value) {
        return new SuccessfulTypeParserResult<>(value);
    }

    default FailedTypeParserResult failure(String reason, Object... args) {
        return new FailedTypeParserResult(String.format(reason, args));
    }
}
