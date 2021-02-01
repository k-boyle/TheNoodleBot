package casino.noodle.commands.framework.jmh;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandCallback;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.results.command.CommandResult;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class CommandExecutionBenchmark {
    private final Module module = CommandModuleFactory.create(BenchmarkCommandContext.class, BenchmarkModule.class, BeanProvider.get());
    private final Map<String, Command> commands = module.commands().stream()
        .collect(Collectors.toMap(Command::name, Function.identity()));

    private final CommandCallback c = commands.get("f").commandCallback();
    private final CommandCallback b = commands.get("b").commandCallback();
    private final CommandCallback a = commands.get("a").commandCallback();

    private final Object[] EMPTY = new Object[0];
    private final Object[] ONE = new Object[] { "abc" };
    private final Object[] FIVE = new Object[] { "a", "b", "c", "d", "e" };
    private final BenchmarkCommandContext context = new BenchmarkCommandContext();

    @Benchmark
    public Mono<CommandResult> commandNoParameters() {
        return a.execute(context, EMPTY, EMPTY);
    }

    @Benchmark
    public Mono<CommandResult> commandOneParameter() {
        return b.execute(context, EMPTY, ONE);
    }

    @Benchmark
    public Mono<CommandResult> commandFiveParameters() {
        return c.execute(context, EMPTY, FIVE);
    }


    @Benchmark
    public Mono<CommandResult> directCommandNoParameters() {
        BenchmarkModule benchmarkModule = new BenchmarkModule();
        benchmarkModule.setContext(context);
        return benchmarkModule.a();
    }

    @Benchmark
    public Mono<CommandResult> directCommandOneParameter() {
        BenchmarkModule benchmarkModule = new BenchmarkModule();
        benchmarkModule.setContext(context);
        return benchmarkModule.b("abc");
    }

    @Benchmark
    public Mono<CommandResult> directCommandFiveParameters() {
        BenchmarkModule benchmarkModule = new BenchmarkModule();
        benchmarkModule.setContext(context);
        return benchmarkModule.f("a", "b", "c", "d", "e");
    }
}
