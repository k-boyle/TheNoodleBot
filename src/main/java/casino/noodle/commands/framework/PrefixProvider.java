package casino.noodle.commands.framework;

import com.google.common.collect.ImmutableSet;

public interface PrefixProvider {
    ImmutableSet<String> getPrefixes();
}
