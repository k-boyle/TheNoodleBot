package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.Command;
import casino.noodle.commands.framework.module.Module;

import java.util.ArrayList;
import java.util.List;

// based on https://github.com/Quahu/Qmmands/blob/master/src/Qmmands/Mapping/CommandMap.cs
public class CommandMap {
    private final CommandNode rootNode;

    private CommandMap(CommandNode rootNode) {
        this.rootNode = rootNode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CommandNode.Builder rootNode;

        private Builder() {
            this.rootNode = CommandNode.builder();
        }

        public Builder map(Module module) {
            // todo if throw invalid state
            map0(module, new ArrayList<>());
            return this;
        }

        private void map0(Module module, List<String> paths) {
            if (module.groups().isEmpty()) {
                map1(module, paths);
                return;
            }

            for (String group : module.groups()) {
                if (group.isEmpty()) {
                    map1(module, paths);
                } else {
                    paths.add(group);
                    map1(module, paths);

                    paths.remove(paths.size() - 1);
                }
            }
        }

        private void map1(Module module, List<String> paths) {
            for (Command command : module.commands()) {
                if (command.aliases().isEmpty()) {
                    rootNode.addCommand(command, paths, 0);
                    continue;
                }

                for (String alias : command.aliases()) {
                    if (alias.isEmpty()) {
                        if (paths.isEmpty()) {
                            continue;
                        }

                        rootNode.addCommand(command, paths, 0);
                    } else {
                        paths.add(alias);
                        rootNode.addCommand(command, paths, 0);
                        paths.remove(paths.size() - 1);
                    }
                }
            }
        }

        public CommandMap build() {
            return new CommandMap(this.rootNode.build());
        }
    }
}
