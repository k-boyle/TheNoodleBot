package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.results.TypeParserResult;

@FunctionalInterface
public interface TypeParser<T> {
    TypeParserResult<T> parse(CommandContext context, Command command, String input);

    default TypeParserResult<T> success(T value) {
        return new TypeParserResult.Success<>(value);
    }

    default TypeParserResult<T> failure(Command command, String reason, Object... args) {
        return new TypeParserResult.Failure<>(command, String.format(reason, args));
    }
}
