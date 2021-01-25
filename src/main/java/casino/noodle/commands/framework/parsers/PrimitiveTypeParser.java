package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.TypeParserResult;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class PrimitiveTypeParser<T> extends TypeParser<T> {
    private final Function<String, T> parseFunction;
    private final Class<T> clazz;

    public PrimitiveTypeParser(Function<String, T> parseFunction, Class<T> clazz) {
        this.parseFunction = parseFunction;
        this.clazz = clazz;
    }

    @Override
    public Mono<TypeParserResult<T>> parseAsync(CommandContext context, String input) {
        try {
            T value = parseFunction.apply(input);
            return success(value);
        } catch (Exception e) {
            return failure("Failed to parse %s as %s", input, clazz);
        }
    }
}
