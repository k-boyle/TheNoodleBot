package casino.noodle.commands.framework;

import casino.noodle.commands.framework.mapping.CommandMap;
import casino.noodle.commands.framework.mapping.CommandSearchResult;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.parsers.PrimitiveTypeParser;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.ArgumentParserResult;
import casino.noodle.commands.framework.results.CommandMatchFailedResult;
import casino.noodle.commands.framework.results.CommandNotFoundResult;
import casino.noodle.commands.framework.results.ExecutionErrorResult;
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
    private final CommandMap commandMap;
    private final ArgumentParser argumentParser;

    private CommandHandler(Map<Class<?>, TypeParser<?>> typeParserByClass, CommandMap commandMapper) {
        this.commandMap = commandMapper;
        // todo potentially abstract out
        this.argumentParser = new ArgumentParser(ImmutableMap.copyOf(typeParserByClass));
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

        ImmutableList.Builder<FailedResult> failedResults = null;

        for (CommandSearchResult searchResult : searchResults) {
            if (searchResult.path().size() < pathLength) {
                continue;
            }

            Command command = searchResult.command();

            try {
                PreconditionResult preconditionResult = command.runPreconditions(context);
                if (preconditionResult instanceof FailedResult failedResult) {
                    if (searchResults.size() == 1) {
                        return Mono.just(failedResult);
                    }

                    if (failedResults == null) {
                        failedResults = ImmutableList.builder();
                    }
                    failedResults.add(failedResult);
                    continue;
                }
            } catch (Exception ex) {
                return Mono.just(new ExecutionErrorResult(command, ex));
            }

            ArgumentParserResult argumentParserResult = argumentParser.parse(
                context,
                searchResult.command(),
                searchResult.remainingArguments()
            );

            if (argumentParserResult instanceof FailedResult failedResult) {
                if (searchResults.size() == 1) {
                    return Mono.just(failedResult);
                }

                if (failedResults == null) {
                    failedResults = ImmutableList.builder();
                }
                failedResults.add(failedResult);
                continue;
            }

            ArgumentParserResult.Success success = (ArgumentParserResult.Success) argumentParserResult;

            return command.commandCallback().execute(context, success.parsedArguments()).cast(Result.class);
        }

        assert failedResults != null;
        return Mono.just(new CommandMatchFailedResult(failedResults.build()));
    }

    public static class Builder {
        private final Map<Class<?>, TypeParser<?>> typeParserByClass;
        private final CommandMap.Builder commandMap;
        private final List<Class<? extends CommandModuleBase>> commandModules;

        private BeanProvider beanProvider;

        private Builder() {
            this.typeParserByClass = new HashMap<>(PrimitiveTypeParser.DEFAULT_PARSERS);
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

        public Builder withBeanProvider(BeanProvider beanProvider) {
            this.beanProvider = beanProvider;
            return this;
        }

        public CommandHandler build() {
            for (Class<? extends CommandModuleBase> moduleClazz : commandModules) {
                Module module = CommandModuleFactory.create(moduleClazz, this.beanProvider);
                this.commandMap.map(module);
            }

            return new CommandHandler(this.typeParserByClass, this.commandMap.build());
        }
    }
}
