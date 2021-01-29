package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;
import casino.noodle.commands.framework.results.PreconditionsFailedResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class Module {
    private final String name;
    private final ImmutableSet<String> groups;
    private final ImmutableList<Command> commands;
    private final ImmutableList<Precondition> preconditions;
    private final Optional<String> description;

    private Module(
            String name, ImmutableSet<String> groups,
            List<Command.Builder> commands,
            ImmutableList<Precondition> preconditions,
            Optional<String> description) {
        this.name = name;
        this.groups = groups;
        this.commands = commands.stream()
            .map(command -> command.build(this))
            .collect(ImmutableList.toImmutableList());
        this.preconditions = preconditions;
        this.description = description;
    }

    static Builder builder() {
        return new Builder();
    }

    PreconditionResult runPreconditions(CommandContext context, Command command) {
        if (preconditions.isEmpty()) {
            return PreconditionResult.Success.get();
        }

        ImmutableList.Builder<PreconditionResult.Failure> failedResults = ImmutableList.builder();
        boolean failedResult = false;

        for (Precondition precondition : preconditions) {
            PreconditionResult result = precondition.run(context, command);
            if (result instanceof PreconditionResult.Failure failed) {
                failedResults.add(failed);
                failedResult = true;
            }
        }

        return failedResult
            ? new PreconditionsFailedResult(failedResults.build())
            : PreconditionResult.Success.get();
    }

    public String name() {
        return name;
    }

    public ImmutableSet<String> groups() {
        return groups;
    }

    public ImmutableList<Command> commands() {
        return commands;
    }

    public Optional<String> description() {
        return description;
    }

    public static class Builder {
        private static final String SPACE = " ";

        private final ImmutableSet.Builder<String> groups;
        private final List<Command.Builder> commands;
        private final ImmutableList.Builder<Precondition> preconditions;

        private String name;
        private String description;

        private Builder() {
            this.groups = ImmutableSet.builder();
            this.commands = new ArrayList<>();
            this.preconditions = ImmutableList.builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withGroup(String group) {
            Preconditions.checkState(!group.contains(SPACE), "Group %s contains a space", group);
            this.groups.add(group);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCommand(Command.Builder command) {
            this.commands.add(command);
            return this;
        }

        public Builder withPrecondition(Precondition precondition) {
            this.preconditions.add(precondition);
            return this;
        }

        public Module build() {
            Preconditions.checkNotNull(this.name, "A module name must be specified");

            return new Module(
                this.name,
                this.groups.build(),
                this.commands,
                this.preconditions.build(),
                Optional.ofNullable(this.description)
            );
        }
    }
}
