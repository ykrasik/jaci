package com.rawcod.jerminal.command.parameters.number;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class IntegerParam extends AbstractNumberCommandParam<Integer> {
    public IntegerParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected Integer parseNumber(String rawValue) {
        return Integer.parseInt(rawValue);
    }

    @Override
    public String toString() {
        return String.format("{%s: int}", getName());
    }
}
