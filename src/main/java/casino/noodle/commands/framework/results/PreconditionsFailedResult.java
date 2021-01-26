package casino.noodle.commands.framework.results;

import com.google.common.collect.ImmutableList;

public record PreconditionsFailedResult(ImmutableList<PreconditionResult.Failure> results) implements FailedResult, PreconditionResult {
    @Override
    public String reason() {
        return "Precondition checks failed";
    }
}
