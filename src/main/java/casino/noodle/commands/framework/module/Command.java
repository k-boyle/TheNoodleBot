package casino.noodle.commands.framework.module;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* todo
    - name
 */
public record Command(
        ImmutableSet<String> aliases,
        String description, //todo optional
        CommandCallback commandCallback,
        ImmutableList<Parameter> parameters,
        Signature signature) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final ImmutableSet.Builder<String> aliases;
        private final ImmutableList.Builder<Parameter> parameters;

        private String description;
        private CommandCallback commandCallback;

        private Builder() {
            this.aliases = ImmutableSet.builder();
            this.parameters = ImmutableList.builder();
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

        public Builder withParameter(Parameter parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Command build() {
            Preconditions.checkNotNull(commandCallback, "A command callback must be specified");
            return new Command(
                this.aliases.build(),
                this.description,
                this.commandCallback,
                this.parameters.build(),
                null);
        }
    }

    public static record Signature(boolean remainder, String parameters) {
    }
}
