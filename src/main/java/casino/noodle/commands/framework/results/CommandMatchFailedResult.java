package casino.noodle.commands.framework.results;

import com.google.common.collect.ImmutableList;

public record CommandMatchFailedResult(ImmutableList<FailedResult> failedResults) implements FailedResult {
    @Override
    public String reason() {
        return "Failed to find a matching command overload";
    }
}
