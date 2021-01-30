package casino.noodle.commands.framework;

import casino.noodle.commands.framework.exceptions.InvalidResultException;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandParameter;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.ExecutionErrorResult;
import casino.noodle.commands.framework.results.FailedResult;
import casino.noodle.commands.framework.results.Result;
import casino.noodle.commands.framework.results.argumentparser.ArgumentMismatchArgumentParserResult;
import casino.noodle.commands.framework.results.argumentparser.SuccessfulArgumentParserResult;
import casino.noodle.commands.framework.results.typeparser.SuccessfulTypeParserResult;
import casino.noodle.commands.framework.results.typeparser.TypeParserResult;
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
    public Result parse(CommandContext context, Command command, String[] rawArguments) {
        ImmutableList<CommandParameter> parameters = command.parameters();

        if (parameters.size() > rawArguments.length) {
            return new ArgumentMismatchArgumentParserResult(command, ArgumentMismatchArgumentParserResult.Reason.TOO_FEW_ARGUMENTS);
        }

        boolean remainder = !parameters.isEmpty() && parameters.get(parameters.size() - 1).remainder();

        if (parameters.size() < rawArguments.length && !remainder) {
            return new ArgumentMismatchArgumentParserResult(command, ArgumentMismatchArgumentParserResult.Reason.TOO_MANY_ARGUMENTS);
        }

        if (parameters.isEmpty()) {
            return SuccessfulArgumentParserResult.empty();
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
                TypeParserResult parseResult = typeParser.parse(context, rawArgument);
                if (parseResult instanceof SuccessfulTypeParserResult success) {
                    parsedArguments[i] = success.value();
                } else if (parseResult instanceof FailedResult failedResult) {
                    return failedResult;
                } else {
                    throw new InvalidResultException(SuccessfulTypeParserResult.class, parseResult.getClass());
                }
            } catch (Exception ex) {
                return new ExecutionErrorResult(command, ex);
            }
        }

        return new SuccessfulArgumentParserResult(parsedArguments);
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
