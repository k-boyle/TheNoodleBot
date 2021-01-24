package casino.noodle.commands.framework.module;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public record Module(
        ImmutableSet<String> groups,
        ImmutableList<Command> commands,
        String description) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final ImmutableSet.Builder<String> groups;
        private final ImmutableList.Builder<Command> commands;

        private String description;

        private Builder() {
            this.groups = ImmutableSet.builder();
            this.commands = ImmutableList.builder();
            this.description = "";
        }

        public Builder withGroups(String... groups) {
            this.groups.add(groups);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCommand(Command command) {
            this.commands.add(command);
            return this;
        }

        public Module build() {
            return new Module(
                this.groups.build(),
                this.commands.build(),
                this.description
            );
        }
    }
}
