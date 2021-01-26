package casino.noodle.commands.framework.module;

import casino.noodle.commands.framework.CommandContext;
import casino.noodle.commands.framework.results.PreconditionResult;
import casino.noodle.commands.framework.results.PreconditionsFailedResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private final ImmutableSet<String> groups;
    private final ImmutableList<Command> commands;
    private final ImmutableList<Precondition> preconditions;
    private final String description;

    private Module(
            ImmutableSet<String> groups,
            List<Command.Builder> commands,
            ImmutableList<Precondition> preconditions,
            String description) {
        this.groups = groups;
        this.commands = commands.stream()
            .map(command -> command.withModule(this).build())
            .collect(ImmutableList.toImmutableList());
        this.preconditions = preconditions;
        this.description = description;
    }

    static Builder builder() {
        return new Builder();
    }

    public PreconditionResult check(CommandContext context) {
        if (preconditions.isEmpty()) {
            return PreconditionResult.Success.get();
        }

        ImmutableList.Builder<PreconditionResult.Failure> failedResults = ImmutableList.builder();
        boolean failedResult = false;

        for (Precondition precondition : preconditions) {
            PreconditionResult result = precondition.check(context);
            if (result instanceof PreconditionResult.Failure failed) {
                failedResults.add(failed);
                failedResult = true;
            }
        }

        return failedResult
            ? new PreconditionsFailedResult(failedResults.build())
            : PreconditionResult.Success.get();
    }

    public ImmutableSet<String> groups() {
        return groups;
    }

    public ImmutableList<Command> commands() {
        return commands;
    }

    public String description() {
        return description;
    }

    static class Builder {
        private final ImmutableSet.Builder<String> groups;
        private final List<Command.Builder> commands;
        private final ImmutableList.Builder<Precondition> preconditions;

        private String description;

        private Builder() {
            this.groups = ImmutableSet.builder();
            this.commands = new ArrayList<>();
            this.preconditions = ImmutableList.builder();
        }

        public Builder withGroups(String... groups) {
            this.groups.add(groups);
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
            return new Module(
                this.groups.build(),
                this.commands,
                this.preconditions.build(),
                this.description
            );
        }
    }
}
