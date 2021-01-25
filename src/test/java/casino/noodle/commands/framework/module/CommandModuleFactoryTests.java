package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ModuleDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// todo preconditions
public class CommandModuleFactoryTests {
    @Test
    public void testCorrectModuleAndCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleOne.class, BeanProvider.EmptyBeanProvider.INSTANCE);

        assertEquals(ImmutableSet.of("group1", "group2"), module.groups());
        assertEquals("This is a test module", module.description());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of("one", "two"), command.aliases());
        assertEquals("This is a test command", command.description());
    }

    @Test
    public void testCorrectModuleAndGroupCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleTwo.class, BeanProvider.EmptyBeanProvider.INSTANCE);

        assertEquals(ImmutableSet.of("group1", "group2"), module.groups());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of(), command.aliases());
    }

    @Test
    public void testCorrectDescriptionlessModuleAndCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleThree.class, BeanProvider.EmptyBeanProvider.INSTANCE);

        assertEquals(ImmutableSet.of(), module.groups());
        assertNull(module.description());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of("one", "two"), command.aliases());
        assertEquals("This is a test command", command.description());
    }

    @Test
    public void testCorrectParameterDetailsAppliedCorrectly() {
        Module module = CommandModuleFactory.create(TestModuleEight.class, BeanProvider.EmptyBeanProvider.INSTANCE);

        assertEquals(1, module.commands().size());

        ImmutableList<CommandParameter> parameters = module.commands().get(0).parameters();

        assertEquals(3, parameters.size());

        CommandParameter param1 = parameters.get(0);
        assertEquals(CommandContext.class, param1.type());
        assertEquals("context", param1.name());
        assertNull(param1.description());
        assertFalse(param1.remainder());

        CommandParameter param2 = parameters.get(1);
        assertEquals(int.class, param2.type());
        assertEquals("temp", param2.name());
        assertNull(param2.description());
        assertFalse(param2.remainder());

        CommandParameter param3 = parameters.get(2);
        assertEquals(String.class, param3.type());
        assertEquals("input", param3.name());
        assertEquals("remaining inputs", param3.description());
        assertTrue(param3.remainder());
    }

    @Test
    public void testTwoCommandsAreCreated() {
        Module module = CommandModuleFactory.create(TestModuleThirteen.class, BeanProvider.EmptyBeanProvider.INSTANCE);
        assertEquals(2, module.commands().size());
    }

    @ParameterizedTest
    @MethodSource("testInvalidCommandSignatureSource")
    public void testInvalidCommandSignature(Class<? extends CommandModuleBase> moduleClazz) {
        assertThrows(IllegalStateException.class, () -> CommandModuleFactory.create(moduleClazz, BeanProvider.EmptyBeanProvider.INSTANCE));
    }

    // Groups with commands
    @ModuleDescription(groups = { "group1", "group2" }, description = "This is a test module")
    private static class TestModuleOne extends CommandModuleBase {
        @CommandDescription(aliases = { "one", "two" }, description = "This is a test command")
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }
    }

    // Group command (default command)
    @ModuleDescription(groups = { "group1", "group2" })
    private static class TestModuleTwo extends CommandModuleBase {
        @CommandDescription(aliases = {})
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }
    }

    // Root commands
    private static class TestModuleThree extends CommandModuleBase {
        @CommandDescription(aliases = { "one", "two" }, description = "This is a test command")
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }
    }

    // Missing group and commands
    private static class TestModuleFour extends CommandModuleBase {
        @CommandDescription(aliases = {})
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }
    }

    // Missing command context in command signature
    private static class TestModuleFive extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public Mono<CommandResult> command() {
            return empty();
        }
    }

    // Doesn't return a Mono
    private static class TestModuleSix extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public void command(CommandContext context) {
        }
    }

    // Missing command context in command signature
    // && Doesn't return a Mono
    private static class TestModuleSeven extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public void command() {
        }
    }

    // Parameter Remainder: Valid parameters
    private static class TestModuleEight extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public Mono<CommandResult> command(
                CommandContext context,
                int temp,
                @ParameterDescription(name = "input",description = "remaining inputs", remainder = true)
                String last) {
            return empty();
        }
    }

    // Parameters Remainder: Not on end (last parameter)
    private static class TestModuleNine extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public Mono<CommandResult> command(
                CommandContext context,
                @ParameterDescription(remainder = true)
                int temp,
                String last) {
            return empty();
        }
    }

    // Parameters Remainder: Multiple remainders
    private static class TestModuleTen extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public Mono<CommandResult> command(
            CommandContext context,
            @ParameterDescription(remainder = true)
                String temp,
            @ParameterDescription(remainder = true)
                String last) {
            return empty();
        }
    }
    
    // Group with space
    @ModuleDescription(groups = "a space")
    private static class TestModuleEleven extends CommandModuleBase {        
    }

    // Alias with space
    private static class TestModuleTwelve extends CommandModuleBase {
        @CommandDescription(aliases = "a space")
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }
    }

    // Two commands
    private static class TestModuleThirteen extends CommandModuleBase {
        @CommandDescription(aliases = { "one" })
        public Mono<CommandResult> command(CommandContext context) {
            return empty();
        }

        @CommandDescription(aliases = { "two" })
        public Mono<CommandResult> command2(CommandContext context) {
            return empty();
        }
    }

    private static Stream<Class<? extends CommandModuleBase>> testInvalidCommandSignatureSource() {
        return Stream.of(
            TestModuleFour.class,
            TestModuleFive.class,
            TestModuleSix.class,
            TestModuleSeven.class,
            TestModuleNine.class,
            TestModuleTen.class,
            TestModuleEleven.class,
            TestModuleTwelve.class
        );
    }
}
