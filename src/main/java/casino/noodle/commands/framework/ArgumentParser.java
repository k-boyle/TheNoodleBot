package casino.noodle.commands.framework;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandParameter;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.ArgumentParserResult;
import casino.noodle.commands.framework.results.TypeParserResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.StringJoiner;

// todo add support for (escaped-)quotes
class ArgumentParser {
    private static final String SPACE = " ";

    private final ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass;

    ArgumentParser(ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass) {
        this.typeParserByClass = typeParserByClass;
    }

    @SuppressWarnings("rawtypes")
    ArgumentParserResult parse(CommandContext context, Command command, String[] rawArguments) {
        ImmutableList<CommandParameter> parameters = command.parameters();

        if (parameters.size() > rawArguments.length) {
            return new ArgumentParserResult.ArgumentMismatch(command, ArgumentParserResult.ArgumentMismatch.Reason.TOO_FEW_ARGUMENTS);
        }

        boolean remainder = !parameters.isEmpty() && parameters.get(parameters.size() - 1).remainder();

        if (parameters.size() < rawArguments.length && !remainder) {
            return new ArgumentParserResult.ArgumentMismatch(command, ArgumentParserResult.ArgumentMismatch.Reason.TOO_MANY_ARGUMENTS);
        }

        if (parameters.isEmpty()) {
            return ArgumentParserResult.Success.empty();
        }

        Object[] parsedArguments = new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            CommandParameter parameter = parameters.get(i);
            Class<?> type = parameter.type();
            String rawArgument = getRawArgument(parameter.remainder(), rawArguments, i);

            if (type.equals(String.class)) {
                parsedArguments[i] = rawArgument;
                continue;
            }

            TypeParser<?> typeParser = Preconditions.checkNotNull(
                typeParserByClass.get(type),
                "Missing type parser for type %s",
                type
            );

            try {
                TypeParserResult<?> parseResult = typeParser.parse(context, command, rawArgument);
                if (parseResult instanceof TypeParserResult.Success success) {
                    parsedArguments[i] = success.value();
                } else {
                    TypeParserResult.Failure failure = (TypeParserResult.Failure) parseResult;
                    return new ArgumentParserResult.ParseFailed(command, type, rawArgument, failure);
                }
            } catch (Exception ex) {
                return new ArgumentParserResult.Exception(command, ex);
            }
        }

        return new ArgumentParserResult.Success(parsedArguments);
    }

    private static String getRawArgument(boolean remainder, String[] rawArguments, int index) {
        if (!remainder) {
            return rawArguments[index];
        }

        StringJoiner joiner = new StringJoiner(SPACE);
        for (int i = index; i < rawArguments.length; i++) {
            joiner.add(rawArguments[i]);
        }

        return joiner.toString();
    }
}
