package casino.noodle.commands.framework.module;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/* todo
    - name (in build() Precondition too)
 */
public record Command(
        ImmutableSet<String> aliases,
        String description, //todo optional
        CommandCallback commandCallback,
        ImmutableList<CommandParameter> parameters,
        Signature signature) {
    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final ImmutableSet.Builder<String> aliases;
        private final ImmutableList.Builder<CommandParameter> parameters;

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

        public Builder withParameter(CommandParameter commandParameter) {
            this.parameters.add(commandParameter);
            return this;
        }

        public Command build() {
            Preconditions.checkNotNull(commandCallback, "A command callback must be specified");

            ImmutableList<CommandParameter> parameters = this.parameters.build();
            for (int i = 0; i < parameters.size(); i++) {
                CommandParameter commandParameter = parameters.get(i);
                Preconditions.checkState(
                    !commandParameter.remainder() || i == parameters.size() - 1,
                    "Parameter %s of Command %s cannot be remainder only the final parameter can be remainder",
                    commandParameter.name(),
                    ""
                );
            }

            return new Command(
                this.aliases.build(),
                this.description,
                this.commandCallback,
                parameters,
                null);
        }
    }

    public static record Signature(boolean remainder, String parameters) {
    }
}
