package casino.noodle.commands.framework.parsers;

import com.google.common.base.Preconditions;

public class CharTypeParser extends PrimitiveTypeParser<Character> {
    public CharTypeParser() {
        super(CharTypeParser::parse, Character.class);
    }

    private static Character parse(String input) {
        Preconditions.checkState(input.length() == 1);
        return input.charAt(0);
    }
}
