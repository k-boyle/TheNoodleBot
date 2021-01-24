package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.Command;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommandNode {
    private final ImmutableMap<String, List<Command>> commandsByAlias;
    private final ImmutableMap<String, Builder> nodeByAlias;

    private CommandNode(Map<String, List<Command>> commandsByAlias, Map<String, Builder> nodeByAlias) {
        this.commandsByAlias = ImmutableMap.copyOf(commandsByAlias);
        this.nodeByAlias = ImmutableMap.copyOf(nodeByAlias);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private final Map<String, List<Command>> commandsByAlias;
        private final Map<String, Builder> nodeByAlias;

        private Builder() {
            this.commandsByAlias = new HashMap<>();
            this.nodeByAlias = new HashMap<>();
        }

        public Builder addCommand(Command command, List<String> paths, int index) {
            Preconditions.checkState(!paths.isEmpty(), "Cannot map pathless commands to root");

            String path = paths.get(index);
            if (index == paths.size() - 1) {
                commandsByAlias.compute(path, (p, commands) -> {
                    if (commands != null) {
                        assertUniqueCommand(command, p, commands);
                    } else {
                        commands = new ArrayList<>();
                    }

                    commands.add(command);
                    return commands;
                });
            } else {
                nodeByAlias.compute(path, (p, node) -> {
                    if (node == null) {
                        node = CommandNode.builder();
                    }

                    node.addCommand(command, paths, index + 1);
                   return node;
                });
            }

            return this;
        }

        private void assertUniqueCommand(Command command, String path, List<Command> commands) {
            for (Command otherCommand : commands) {
                // add commands signature
            }
        }

        public CommandNode build() {
            return new CommandNode(this.commandsByAlias, this.nodeByAlias);
        }
    }
}
