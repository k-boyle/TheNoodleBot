package casino.noodle.jmh;

import casino.noodle.commands.framework.BeanProvider;
import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.CommandModuleFactory;
import casino.noodle.commands.framework.module.Module;
import casino.noodle.commands.framework.parsers.ArgumentParser;
import casino.noodle.commands.framework.parsers.PrimitiveTypeParser;
import casino.noodle.commands.framework.results.Result;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
public class ArgumentParserBenchmark {
    private final ArgumentParser argumentParser = new ArgumentParser(PrimitiveTypeParser.DEFAULT_PARSERS);

    private final Module module = CommandModuleFactory.create(
        BenchmarkCommandContext.class,
        BenchmarkModule.class,
        BeanProvider.get()
    );

    private final Map<String, List<Command>> commandMap = module.commands().stream()
        .collect(Collectors.groupingBy(command -> command.aliases().stream().findFirst().get()));

    @Benchmark
    public Result commandNotParameters() {
        return argumentParser.parse(new BenchmarkCommandContext(), commandMap.get("a").get(0), "", 0);
    }

    @Benchmark
    public Result commandOneParameter() {
        return argumentParser.parse(new BenchmarkCommandContext(), commandMap.get("b").get(0), "abc", 0);
    }

    @Benchmark
    public Result commandRemainderParameter() {
        return argumentParser.parse(new BenchmarkCommandContext(), commandMap.get("c").get(0), "abc def ghi", 0);
    }

    @Benchmark
    public Result commandFiveParameters() {
        return argumentParser.parse(new BenchmarkCommandContext(), commandMap.get("f").get(0), "abc def ghi jkl mno", 0);
    }
}
