package casino.noodle.commands.framework.module;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CommandModule {
    private final ImmutableSet<String> groups;
    private final ImmutableList<ModuleCommand> commands;
    private final Optional<String> description;

    private CommandModule(
            ImmutableSet<String> groups,
            ImmutableList<ModuleCommand> commands,
            Optional<String> description) {
        this.groups = groups;
        this.commands = commands;
        this.description = description;
    }

    static Builder builder() {
        return new Builder();
    }

    public ImmutableSet<String> getGroups() {
        return groups;
    }

    public ImmutableList<ModuleCommand> getCommands() {
        return commands;
    }

    public Optional<String> getDescription() {
        return description;
    }

    static class Builder {
        private final ImmutableSet.Builder<String> groups;
        private final ImmutableList.Builder<ModuleCommand> commands;

        private String description;

        private Builder() {
            this.groups = ImmutableSet.builder();
            this.commands = ImmutableList.builder();
        }

        public Builder withGroups(String... groups) {
            this.groups.add(groups);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCommand(ModuleCommand command) {
            this.commands.add(command);
            return this;
        }

        public CommandModule build() {
            return new CommandModule(
                this.groups.build(),
                this.commands.build(),
                Optional.ofNullable(this.description)
            );
        }
    }
}
