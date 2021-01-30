package casino.noodle.commands.framework;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.parsers.PrimitiveTypeParser;
import casino.noodle.commands.framework.parsers.TypeParser;
import casino.noodle.commands.framework.results.Result;
import casino.noodle.commands.framework.results.argumentparser.ArgumentParserResult;
import casino.noodle.commands.framework.results.typeparser.TypeParserResult;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

public class ArgumentParserTests {
    private static final Command COMMAND_INT_ARG_NOT_REMAINDER = new TestCommandBuilder()
        .addParameter(int.class, false)
        .build();

    private static final Command COMMAND_INT_ARG_REMAINDER = new TestCommandBuilder()
        .addParameter(int.class, true)
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

    private static final Command COMMAND_STRING_STRING_NOT_ARG_REMAINDER = new TestCommandBuilder()
        .addParameter(String.class, false)
        .addParameter(String.class, false)
        .build();

    @ParameterizedTest
    @MethodSource("argumentParserTestSource")
    public void argumentParserTest(Command command, String[] arguments, ArgumentParserResult expectedResult) {
        HashMap<Class<?>, TypeParser<?>> parsers = new HashMap<>(PrimitiveTypeParser.DEFAULT_PARSERS);
        parsers.put(BadParser.class, new BadParser());

        ArgumentParser argumentParser = new ArgumentParser(ImmutableMap.copyOf(parsers));
        // todo fix context
        Result actualResult = argumentParser.parse(null, command, arguments);
        Assertions.assertEquals(expectedResult, actualResult);
    }

    public static class BadParser implements TypeParser<Long> {
        @Override
        public TypeParserResult parse(CommandContext context, String input) {
            throw new RuntimeException("Bad Parse");
        }
    }

    // todo test missing type parser
    private static Stream<Arguments> argumentParserTestSource() {
        return Stream.of(
//            Arguments.of(
//                COMMAND_INT_ARG_NOT_REMAINDER,
//                new String[] { "100" },
//                new ArgumentParserResult.Success(100)
//            ),
//            Arguments.of(
//                COMMAND_INT_ARG_NOT_REMAINDER,
//                new String[] { "100", "200" },
//                new ArgumentParserResult.ArgumentMismatch(COMMAND_INT_ARG_NOT_REMAINDER, ArgumentParserResult.ArgumentMismatch.Reason.TOO_MANY_ARGUMENTS)
//            ),
//            Arguments.of(
//                COMMAND_INT_ARG_NOT_REMAINDER,
//                new String[0],
//                new ArgumentParserResult.ArgumentMismatch(COMMAND_INT_ARG_NOT_REMAINDER, ArgumentParserResult.ArgumentMismatch.Reason.TOO_FEW_ARGUMENTS)
//            ),
//            Arguments.of(
//                COMMAND_INT_ARG_NOT_REMAINDER,
//                new String[] { "string" },
//                new ArgumentParserResult.ParseFailed(COMMAND_INT_ARG_NOT_REMAINDER, int.class, "string", new TypeParserResult.Failure(COMMAND_INT_ARG_NOT_REMAINDER, ""))
//            ),
//            Arguments.of(
//                COMMAND_INT_ARG_REMAINDER,
//                new String[] { "10", "10", "10" },
//                new ArgumentParserResult.Success(10, 10, 10)
//            )
        );
    }
}
