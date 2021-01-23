package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.TypeParserResult;
import reactor.core.publisher.Mono;

public abstract class TypeParser<T> {
    public abstract Mono<TypeParserResult<T>> parseAsync(CommandContext context, String input);
}
