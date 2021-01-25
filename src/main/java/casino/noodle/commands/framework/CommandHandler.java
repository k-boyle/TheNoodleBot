package casino.noodle.commands.framework;

import casino.noodle.commands.framework.mapping.CommandMap;
import casino.noodle.commands.framework.mapping.CommandSearchResult;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.Result;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import discord4j.core.object.entity.Message;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass;
    private final CommandMap commandMap;
    private final PrefixProvider prefixProvider;

    private CommandHandler(
            Map<Class<?>, TypeParser<?>> typeParserByClass,
            CommandMap commandMapper,
            PrefixProvider prefixProvider) {
        this.typeParserByClass = ImmutableMap.copyOf(typeParserByClass);
        this.commandMap = commandMapper;
        this.prefixProvider = prefixProvider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Mono<Result> executeAsync(Message message, ApplicationContext applicationContext) {
        ImmutableList<CommandSearchResult> commands = commandMap.findCommands(message.getContent());
        return Mono.empty();
    }

    public static class Builder {
        private final Map<Class<?>, TypeParser<?>> typeParserByClass;
        private final CommandMap.Builder commandMap;

        private PrefixProvider prefixProvider;

        public Builder() {
            this.typeParserByClass = new HashMap<>();
            this.commandMap = CommandMap.builder();
        }

        public <T> Builder withTypeParser(Class<T> clazz, TypeParser<T> parser) {
            this.typeParserByClass.put(clazz, parser);
            return this;
        }

        public <T extends CommandModuleBase> Builder withModule(Class<T> moduleClazz) {
            Module module = CommandModuleFactory.create(moduleClazz);
            this.commandMap.map(module);
            return this;
        }

        public Builder withPrefixProvider(PrefixProvider prefixProvider) {
            this.prefixProvider = prefixProvider;
            return this;
        }

        public CommandHandler build() {
            Preconditions.checkNotNull(this.prefixProvider, "A PrefixProvider must be specified");
            return new CommandHandler(this.typeParserByClass, this.commandMap.build(), this.prefixProvider);
        }
    }
}
