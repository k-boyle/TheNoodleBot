package kboyle.noodle.markov;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.Objects;

public class MarkovMessage {
    @SerializedName("UserId")
    private final long userId;
    @SerializedName("Prefix")
    private final String[] prefix;
    @SerializedName("Content")
    private final String content;

    public MarkovMessage(long userId, String[] prefix, String content) {
        this.userId = userId;
        this.prefix = prefix;
        this.content = content;
    }

    @SerializedName("UserId")
    public long userId() {
        return userId;
    }

    @SerializedName("Prefix")
    public String[] prefix() {
        return prefix;
    }

    @SerializedName("Content")
    public String content() {
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MarkovMessage) obj;
        return this.userId == that.userId &&
            Arrays.equals(this.prefix, that.prefix) &&
            Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, prefix, content);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("userId", userId)
            .add("prefix", prefix)
            .add("content", content)
            .toString();
    }
}
