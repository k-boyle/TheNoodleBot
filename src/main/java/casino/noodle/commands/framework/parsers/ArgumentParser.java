package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.exceptions.InvalidResultException;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandParameter;
import casino.noodle.commands.framework.results.ExecutionErrorResult;
import casino.noodle.commands.framework.results.FailedResult;
import casino.noodle.commands.framework.results.Result;
import casino.noodle.commands.framework.results.argumentparser.FailedArgumentParserResult;
import casino.noodle.commands.framework.results.argumentparser.SuccessfulArgumentParserResult;
import casino.noodle.commands.framework.results.typeparser.SuccessfulTypeParserResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ArgumentParser {
    private static final char SPACE = ' ';
    private static final char QUOTE = '"';
    private static final char ESCAPE = '\\';

    private final ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass;

    public ArgumentParser(ImmutableMap<Class<?>, TypeParser<?>> typeParserByClass) {
        this.typeParserByClass = typeParserByClass;
    }

    public Result parse(CommandContext context, String input, int index) {
        return parse(context, context.command(), input, index);
    }

    // todo garbage code but works:tm:
    public Result parse(CommandContext context, Command command, String input, int index) {
        ImmutableList<CommandParameter> parameters = command.parameters();

        if (input.length() <= index && !parameters.isEmpty()) {
            return new FailedArgumentParserResult(command, FailedArgumentParserResult.Reason.TOO_FEW_ARGUMENTS, index);
        }

        if (parameters.isEmpty()) {
            return SuccessfulArgumentParserResult.empty();
        }

        Object[] parsedArguments = new Object[parameters.size()];
        for (int p = 0; p < parameters.size(); p++) {
            CommandParameter parameter = parameters.get(p);
            Class<?> type = parameter.type();

            for (; index < input.length(); index++) {
                char currentCharacter = input.charAt(index);
                if (Character.isSpaceChar(currentCharacter)) {
                    continue;
                }

                if (currentCharacter == ESCAPE && input.charAt(index - 1) == ESCAPE) {
                    break;
                }

                if (currentCharacter != ESCAPE) {
                    break;
                }
            }

            if (index == input.length() - 1) {
                return new FailedArgumentParserResult(command, FailedArgumentParserResult.Reason.TOO_FEW_ARGUMENTS, index);
            }

            if (parameter.remainder()) {
                String remainder = input.substring(index);
                index = input.length();
                if (type == String.class) {
                    parsedArguments[p] = remainder;
                    break;
                }

                Result result = parse(type, context, remainder);
                if (result instanceof SuccessfulTypeParserResult success) {
                    parsedArguments[p] = success.value();
                } else if (result instanceof FailedResult failedResult) {
                    return failedResult;
                } else {
                    throw new InvalidResultException(SuccessfulTypeParserResult.class, result.getClass());
                }
            } else {
                int paramStart = index;
                for (; index < input.length(); index++) {
                    char currentCharacter = input.charAt(index);

                    if (currentCharacter == QUOTE) {
                        if (input.charAt(index - 1) == ESCAPE) {
                            continue;
                        }

                        index++;
                        for (; index < input.length(); index++) {
                            if (input.charAt(index) != QUOTE) {
                                continue;
                            }

                            if (input.charAt(index - 1) != ESCAPE) {
                                String param = paramStart + 1 == index
                                    ? ""
                                    : input.substring(paramStart + 1, index + 1);

                                if (type == String.class) {
                                    parsedArguments[p] = param;
                                    break;
                                }

                                Result result = parse(type, context, param);
                                if (result instanceof SuccessfulTypeParserResult success) {
                                    parsedArguments[p] = success.value();
                                } else if (result instanceof FailedResult failedResult) {
                                    return failedResult;
                                } else {
                                    throw new InvalidResultException(SuccessfulTypeParserResult.class, result.getClass());
                                }

                                break;
                            }
                        }

                        if (index >= input.length() - 1) {
                            return new FailedArgumentParserResult(command, FailedArgumentParserResult.Reason.MISSING_QUOTE, index);
                        }

                        break;
                    } else if (currentCharacter == SPACE) {
                        String param = input.substring(paramStart, index);

                        if (type == String.class) {
                            parsedArguments[p] = param;
                            break;
                        }

                        Result result = parse(type, context, param);
                        if (result instanceof SuccessfulTypeParserResult success) {
                            parsedArguments[p] = success.value();
                        } else if (result instanceof FailedResult failedResult) {
                            return failedResult;
                        } else {
                            throw new InvalidResultException(SuccessfulTypeParserResult.class, result.getClass());
                        }
                        break;
                    } else if(index == input.length() - 1) {
                        String param = paramStart == 0 ? input : input.substring(paramStart);

                        if (type == String.class) {
                            parsedArguments[p] = param;
                            index++;
                            break;
                        }

                        Result result = parse(type, context, param);
                        if (result instanceof SuccessfulTypeParserResult success) {
                            parsedArguments[p] = success.value();
                        } else if (result instanceof FailedResult failedResult) {
                            return failedResult;
                        } else {
                            throw new InvalidResultException(SuccessfulTypeParserResult.class, result.getClass());
                        }
                    }
                }
            }
        }

        if (index != input.length()) {
            return new FailedArgumentParserResult(command, FailedArgumentParserResult.Reason.TOO_MANY_ARGUMENTS, index);
        }

        return new SuccessfulArgumentParserResult(parsedArguments);
    }

    private Result parse(Class<?> type, CommandContext context, String input) {
        TypeParser<?> typeParser = Preconditions.checkNotNull(
            typeParserByClass.get(type),
            "Missing type parser for type %s",
            type
        );

        try {
            return typeParser.parse(context, input);
        }catch (Exception ex) {
            return new ExecutionErrorResult(context.command(), ex);
        }
    }
}
