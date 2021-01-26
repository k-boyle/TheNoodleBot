package casino.noodle.commands.framework;

import casino.noodle.commands.framework.mapping.CommandMap;
import casino.noodle.commands.framework.mapping.CommandSearchResult;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.parsers.CharTypeParser;
import casino.noodle.commands.framework.parsers.PrimitiveTypeParser;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.CommandNotFoundResult;
import casino.noodle.commands.framework.results.FailedResult;
import casino.noodle.commands.framework.results.PreconditionResult;
import casino.noodle.commands.framework.results.Result;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandler {
    public static final ImmutableMap<Class<?>, PrimitiveTypeParser<?>> PRIMITIVE_TYPE_PARSERS = ImmutableMap.<Class<?>, PrimitiveTypeParser<?>>builder()
        .put(boolean.class, new PrimitiveTypeParser<>(Boolean::parseBoolean, boolean.class))
        .put(Boolean.class, new PrimitiveTypeParser<>(Boolean::parseBoolean, Boolean.class))

        .put(byte.class, new PrimitiveTypeParser<>(Byte::parseByte, byte.class))
        .put(Byte.class, new PrimitiveTypeParser<>(Byte::parseByte, Byte.class))

        .put(char.class, new CharTypeParser())
        .put(Character.class, new CharTypeParser())

        .put(int.class, new PrimitiveTypeParser<>(Integer::parseInt, int.class))
        .put(Integer.class, new PrimitiveTypeParser<>(Integer::parseInt, Integer.class))

        .put(short.class, new PrimitiveTypeParser<>(Short::parseShort, short.class))
        .put(Short.class, new PrimitiveTypeParser<>(Short::parseShort, Short.class))

        .put(float.class, new PrimitiveTypeParser<>(Float::parseFloat, float.class))
        .put(Float.class, new PrimitiveTypeParser<>(Float::parseFloat, Float.class))

        .put(long.class, new PrimitiveTypeParser<>(Long::parseLong, long.class))
        .put(Long.class, new PrimitiveTypeParser<>(Long::parseLong, Long.class))

        .put(double.class, new PrimitiveTypeParser<>(Double::parseDouble, double.class))
        .put(Double.class, new PrimitiveTypeParser<>(Double::parseDouble, Double.class))
        .build();

    private final ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass;
    private final CommandMap commandMap;
    private final PrefixProvider prefixProvider;
    private final BeanProvider beanProvider;

    private CommandHandler(
            Map<Class<?>, TypeParser<?>> typeParserByClass,
            CommandMap commandMapper,
            PrefixProvider prefixProvider,
            BeanProvider beanProvider) {
        this.typeParserByClass = ImmutableMap.copyOf(typeParserByClass);
        this.commandMap = commandMapper;
        this.prefixProvider = prefixProvider;
        this.beanProvider = beanProvider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Mono<Result> executeAsync(String input, CommandContext context) {
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(context);

        ImmutableList<CommandSearchResult> searchResults = commandMap.findCommands(input);

        if (searchResults.isEmpty()) {
            return Mono.just(CommandNotFoundResult.get());
        }

        int pathLength = searchResults.get(0).path().size();

        // command can't be a key
        Map<Command, FailedResult> failedOverloads = new HashMap<>();

        // todo command errors "Too many/few args"
        for (CommandSearchResult searchResult : searchResults) {
            if (searchResult.path().size() < pathLength) {
                continue;
            }

            Command command = searchResult.command();
            PreconditionResult preconditionResult = command.check(context);

            if (preconditionResult instanceof FailedResult failedResult) {
                failedOverloads.put(command, failedResult);
                continue;
            }


        }


        // Number of args
        // remainder
        // parsed Args
        // preconditions

        return Mono.empty();
    }

    public static class Builder {
        private final Map<Class<?>, TypeParser<?>> typeParserByClass;
        private final CommandMap.Builder commandMap;
        private final List<Class<? extends CommandModuleBase>> commandModules;

        private PrefixProvider prefixProvider;
        private BeanProvider beanProvider;

        public Builder() {
            this.typeParserByClass = new HashMap<>(PRIMITIVE_TYPE_PARSERS);
            this.commandMap = CommandMap.builder();
            this.commandModules = new ArrayList<>();
            this.beanProvider = BeanProvider.get();
        }

        public <T> Builder withTypeParser(Class<T> clazz, TypeParser<T> parser) {
            this.typeParserByClass.put(clazz, parser);
            return this;
        }

        public <T extends CommandModuleBase> Builder withModule(Class<T> moduleClazz) {
            this.commandModules.add(moduleClazz);
            return this;
        }

        public Builder withPrefixProvider(PrefixProvider prefixProvider) {
            this.prefixProvider = prefixProvider;
            return this;
        }

        public Builder withBeanProvider(BeanProvider beanProvider) {
            this.beanProvider = beanProvider;
            return this;
        }

        public CommandHandler build() {
            Preconditions.checkNotNull(this.prefixProvider, "A PrefixProvider must be specified");
            for (Class<? extends CommandModuleBase> moduleClazz : commandModules) {
                Module module = CommandModuleFactory.create(moduleClazz, this.beanProvider);
                this.commandMap.map(module);
            }

            return new CommandHandler(this.typeParserByClass, this.commandMap.build(), this.prefixProvider, this.beanProvider);
        }
    }
}
