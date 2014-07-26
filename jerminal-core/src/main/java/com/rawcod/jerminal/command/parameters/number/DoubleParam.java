package com.rawcod.jerminal.command.parameters.number;

/**
 * User: ykrasik
 * Date: 05/01/14
 */
public class DoubleParam extends AbstractNumberCommandParam<Double> {
    public DoubleParam(String name, String description) {
        super(name, description);
    }

    @Override
    protected Double parseNumber(String rawValue) {
        return Double.parseDouble(rawValue);
    }

    @Override
    public String toString() {
        return String.format("{%s: double}", getName());
    }
}
