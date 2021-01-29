package casino.noodle.jmh;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.CommandHandler;
import casino.noodle.commands.framework.module.CommandModuleBase;
import casino.noodle.commands.framework.module.annotations.CommandDescription;
import casino.noodle.commands.framework.module.annotations.ParameterDescription;
import casino.noodle.commands.framework.results.CommandResult;
import casino.noodle.commands.framework.results.Result;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class CommandExecutionBenchmark {
    private final CommandHandler commandHandler = CommandHandler.builder()
        .withBeanProvider(BeanProvider.get())
        .withModule(BenchmarkModule.class)
        .build();

    private final CommandContext context = new CommandContext(BeanProvider.get(), null);

    @Benchmark
    public Mono<Result> commandNoParameters() {
        return commandHandler.executeAsync("a", context);
    }

    @Benchmark
    public Mono<Result> commandOneParameter() {
        return commandHandler.executeAsync("b abc", context);
    }

    @Benchmark
    public Mono<Result> commandRemainderParameter() {
        return commandHandler.executeAsync("c abc def ghi", context);
    }

    @Benchmark
    public Mono<Result> commandNotFound() {
        return commandHandler.executeAsync("notfound", context);
    }

    public static class BenchmarkModule extends CommandModuleBase {
        @CommandDescription(aliases = "a")
        public Mono<CommandResult> a(CommandContext context) {
            return Mono.empty();
        }

        @CommandDescription(aliases = "b")
        public Mono<CommandResult> b(CommandContext context, String arg1) {
            return Mono.empty();
        }

        @CommandDescription(aliases = "c")
        public Mono<CommandResult> c(CommandContext context, @ParameterDescription(remainder = true) String arg1) {
            return Mono.empty();
        }
    }
}
