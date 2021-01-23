package casino.noodle.commands.framework.module;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public record Command(
        ImmutableSet<String> aliases,
        String description,
        CommandCallback commandCallback) {

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final ImmutableSet.Builder<String> aliases;

        private String description;
        private CommandCallback commandCallback;

        private Builder() {
            this.aliases = ImmutableSet.builder();
            this.description = "";
        }

        public Builder withAliases(String... aliases) {
            this.aliases.add(aliases);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCallback(CommandCallback commandCallback) {
            this.commandCallback = commandCallback;
            return this;
        }

        public Command build() {
            Preconditions.checkNotNull(commandCallback, "A command callback must be specified");
            return new Command(this.aliases.build(), this.description, this.commandCallback);
        }
    }
}
