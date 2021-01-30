package casino.noodle.commands.framework.results.search;

import casino.noodle.commands.framework.results.FailedResult;
import com.google.common.collect.ImmutableList;

public record CommandMatchFailedResult(ImmutableList<FailedResult> failedResults) implements FailedResult {
    @Override
    public String reason() {
        return "Failed to find a matching command overload";
    }
}
