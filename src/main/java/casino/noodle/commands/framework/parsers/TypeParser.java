package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.TypeParserResult;
import reactor.core.publisher.Mono;

public abstract class TypeParser<T> {
    public abstract Mono<TypeParserResult<T>> parseAsync(CommandContext context, String input);

    protected Mono<TypeParserResult<T>> success(T value) {
        return Mono.just(new TypeParserResult.Success<>(value));
    }

    protected Mono<TypeParserResult<T>> failure(String reason, Object... args) {
        return Mono.just(new TypeParserResult.Failure<>(String.format(reason, args)));
    }
}
