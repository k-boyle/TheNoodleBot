package casino.noodle.commands.framework.results.precondition;

import casino.noodle.commands.framework.results.FailedResult;
import com.google.common.collect.ImmutableList;

public record PreconditionsFailedResult(ImmutableList<FailedResult> results) implements FailedResult, PreconditionResult {
    @Override
    public String reason() {
        return "Precondition checks failed";
    }
}
