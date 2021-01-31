package casino.noodle.commands.framework.parsers;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.TestCommandBuilder;
import casino.noodle.commands.framework.TestCommandContext;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.results.ExecutionErrorResult;
import casino.noodle.commands.framework.results.Result;
import casino.noodle.commands.framework.results.argumentparser.ArgumentMismatchArgumentParserResult;
import casino.noodle.commands.framework.results.argumentparser.SuccessfulArgumentParserResult;
import casino.noodle.commands.framework.results.typeparser.FailedTypeParserResult;
import casino.noodle.commands.framework.results.typeparser.TypeParserResult;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

public class ArgumentParserTests {
    private static final Command COMMAND_INT_ARG_NOT_REMAINDER = new TestCommandBuilder()
        .addParameter(int.class, false)
        .build();

    private static final Command COMMAND_LONG_ARG_NOT_REMAINDER = new TestCommandBuilder()
        .addParameter(Long.class, false)
        .build();

    private static final Command COMMAND_MISSING_PARAMETER_PARSER = new TestCommandBuilder()
        .addParameter(CommandContext.class, false)
        .build();

    private static final Command COMMAND_STRING_NOT_ARG_REMAINDER = new TestCommandBuilder()
        .addParameter(String.class, false)
        .build();

    private static final Command COMMAND_STRING_ARG_REMAINDER = new TestCommandBuilder()
        .addParameter(String.class, true)
        .build();

    private static final Command COMMAND_STRING_STRING_ARG_REMAINDER = new TestCommandBuilder()
        .addParameter(String.class, false)
        .addParameter(String.class, true)
        .build();

    @Test
    public void testArgumentParserThrowsOnMissingTypeParser() {
        ArgumentParser argumentParser = new ArgumentParser(ImmutableMap.copyOf(PrimitiveTypeParser.DEFAULT_PARSERS));
        Assertions.assertThrows(
            NullPointerException.class,
            () -> argumentParser.parse(new TestCommandContext(), COMMAND_MISSING_PARAMETER_PARSER, new String[]{ "string" })
        );
    }

    @ParameterizedTest
    @MethodSource("argumentParserTestSource")
    public void argumentParserTest(Command command, String[] arguments, Result expectedResult) {
        HashMap<Class<?>, TypeParser<?>> parsers = new HashMap<>(PrimitiveTypeParser.DEFAULT_PARSERS);
        parsers.put(Long.class, new BadParser());

        ArgumentParser argumentParser = new ArgumentParser(ImmutableMap.copyOf(parsers));
        Result actualResult = argumentParser.parse(new TestCommandContext(), command, arguments);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    public static class BadParser implements TypeParser<Long> {
        @Override
        public TypeParserResult parse(CommandContext context, String input) {
            throw new RuntimeException("Bad Parse");
        }
    }

    private static Stream<Arguments> argumentParserTestSource() {
        return Stream.of(
            Arguments.of(
                COMMAND_INT_ARG_NOT_REMAINDER,
                new String[] { "100" },
                new SuccessfulArgumentParserResult(new Object[]{ 100 })
            ),
            Arguments.of(
                COMMAND_INT_ARG_NOT_REMAINDER,
                new String[] { "100", "200" },
                new ArgumentMismatchArgumentParserResult(COMMAND_INT_ARG_NOT_REMAINDER, ArgumentMismatchArgumentParserResult.Reason.TOO_MANY_ARGUMENTS)
            ),
            Arguments.of(
                COMMAND_INT_ARG_NOT_REMAINDER,
                new String[0],
                new ArgumentMismatchArgumentParserResult(COMMAND_INT_ARG_NOT_REMAINDER, ArgumentMismatchArgumentParserResult.Reason.TOO_FEW_ARGUMENTS)
            ),
            Arguments.of(
                COMMAND_INT_ARG_NOT_REMAINDER,
                new String[] { "string" },
                new FailedTypeParserResult(String.format("Failed to parse %s as %s", "string", int.class))
            ),
            Arguments.of(
                COMMAND_LONG_ARG_NOT_REMAINDER,
                new String[] { "100" },
                new ExecutionErrorResult(COMMAND_LONG_ARG_NOT_REMAINDER,  new RuntimeException("Bad Parse"))
            ),
            Arguments.of(
                COMMAND_STRING_NOT_ARG_REMAINDER,
                new String[] { "string" },
                new SuccessfulArgumentParserResult(new Object[]{ "string" })
            ),
            Arguments.of(
                COMMAND_STRING_ARG_REMAINDER,
                new String[] { "string 123" },
                new SuccessfulArgumentParserResult(new Object[]{ "string 123" })
            ),
            Arguments.of(
                COMMAND_STRING_STRING_ARG_REMAINDER,
                new String[] { "string", "123 456" },
                new SuccessfulArgumentParserResult(new Object[]{ "string", "123 456" })
            )
        );
    }
}
