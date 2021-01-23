package casino.noodle.commands.framework;

import casino.noodle.commands.framework.mapping.CommandMap;
import casino.noodle.commands.framework.module.CommandModule;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.Result;
import com.google.common.base.Preconditions;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler {
    private final ConcurrentHashMap<Class<?>, TypeParser<?>> typeParserByClass;
    private final CommandMap commandMap;
    private final PrefixProvider prefixProvider;

    private CommandHandler(
            Map<Class<?>, TypeParser<?>> typeParserByClass,
            CommandMap commandMapper,
            PrefixProvider prefixProvider) {
        this.typeParserByClass = new ConcurrentHashMap<>(typeParserByClass);
        this.commandMap = commandMapper;
        this.prefixProvider = prefixProvider;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Mono<Result> executeAsync(Message message) {
        String[] params = message.getContent().split(" ");



        return Mono.empty();
    }

    public static class Builder {
        private final Map<Class<?>, TypeParser<?>> typeParserByClass;
        private final CommandMap commandMap;

        private PrefixProvider prefixProvider;

        public Builder() {
            this.typeParserByClass = new HashMap<>();
            this.commandMap = new CommandMap();
        }

        public <T> Builder withTypeParser(Class<T> clazz, TypeParser<T> parser) {
            this.typeParserByClass.put(clazz, parser);
            return this;
        }

        public <T extends CommandModuleBase> Builder withModule(Class<T> moduleClazz) {
            CommandModule commandModule = CommandModuleFactory.create(moduleClazz);
//            this.commandMapper.map(commandModule);
            return this;
        }

        public Builder withPrefixProvider(PrefixProvider prefixProvider) {
            this.prefixProvider = prefixProvider;
            return this;
        }

        public CommandHandler build() {
            Preconditions.checkNotNull(this.prefixProvider, "A PrefixProvider must be specified");
            return new CommandHandler(this.typeParserByClass, this.commandMap, this.prefixProvider);
        }
    }
}
