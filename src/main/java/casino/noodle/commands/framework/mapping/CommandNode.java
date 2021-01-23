package casino.noodle.commands.framework.mapping;

import casino.noodle.commands.framework.module.ModuleCommand;

public class CommandNode {
    private ModuleCommand _module;

    public CommandNode(ModuleCommand module) {
        this._module = module;
    }

}
