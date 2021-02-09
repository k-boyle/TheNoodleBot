package kboyle.noodle.markov;

public record Token(String word) {
    public static final Token NULL = new Token(null);
}
