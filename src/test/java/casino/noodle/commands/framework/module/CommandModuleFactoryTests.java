package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.CommandResult;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandModuleFactoryTests {
    @Test
    public void testCorrectModuleAndCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleOne.class);

        assertEquals(ImmutableSet.of("group1", "group2"), module.groups());
        assertEquals("This is a test module", module.description());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of("one", "two"), command.aliases());
        assertEquals("This is a test command", command.description());
    }

    @Test
    public void testCorrectModuleAndGroupCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleTwo.class);

        assertEquals(ImmutableSet.of("group1", "group2"), module.groups());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of(), command.aliases());
    }

    @Test
    public void testCorrectDescriptionlessModuleAndCommandIsCreated() {
        Module module = CommandModuleFactory.create(TestModuleThree.class);

        assertEquals(ImmutableSet.of(), module.groups());
        assertEquals("", module.description());
        assertEquals(1, module.commands().size());

        Command command = module.commands().get(0);

        assertEquals(ImmutableSet.of("one", "two"), command.aliases());
        assertEquals("This is a test command", command.description());
    }

    @ParameterizedTest
    @MethodSource("testInvalidCommandSignatureSource")
    public void testInvalidCommandSignature(Class<? extends CommandModuleBase> moduleClazz) {
        assertThrows(IllegalStateException.class, () -> CommandModuleFactory.create(moduleClazz));
    }

    @ModuleDescriptor(groups = { "group1", "group2" }, description = "This is a test module")
    private static class TestModuleOne extends CommandModuleBase {
        @CommandDescriptor(aliases = { "one", "two" }, description = "This is a test command")
        public Mono<CommandResult> command(CommandContext context) {
            return Mono.empty();
        }
    }

    @ModuleDescriptor(groups = { "group1", "group2" })
    private static class TestModuleTwo extends CommandModuleBase {
        @CommandDescriptor(aliases = {})
        public Mono<CommandResult> command(CommandContext context) {
            return Mono.empty();
        }
    }

    private static class TestModuleThree extends CommandModuleBase {
        @CommandDescriptor(aliases = { "one", "two" }, description = "This is a test command")
        public Mono<CommandResult> command(CommandContext context) {
            return Mono.empty();
        }
    }

    private static class TestModuleFour extends CommandModuleBase {
        @CommandDescriptor(aliases = {})
        public Mono<CommandResult> command(CommandContext context) {
            return Mono.empty();
        }
    }

    private static class TestModuleFive extends CommandModuleBase {
        @CommandDescriptor(aliases = { "one" })
        public Mono<CommandResult> command() {
            return Mono.empty();
        }
    }

    private static class TestModuleSix extends CommandModuleBase {
        @CommandDescriptor(aliases = { "one" })
        public void command(CommandContext context) {
        }
    }

    private static class TestModuleSeven extends CommandModuleBase {
        @CommandDescriptor(aliases = { "one" })
        public void command() {
        }
    }

    private static Stream<Class<? extends CommandModuleBase>> testInvalidCommandSignatureSource() {
        return Stream.of(TestModuleFour.class, TestModuleFive.class, TestModuleSix.class, TestModuleSeven.class);
    }
}
