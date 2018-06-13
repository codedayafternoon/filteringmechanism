package domain.filters.valueformatters;

import domain.filters.IValuePostFormatter;

import java.util.ArrayList;
import java.util.List;

public class DefaultValuePostFormatter implements IValuePostFormatter {

    @Override
    public List<String> Extract(String value) {
        return new ArrayList<>();
    }

    @Override
    public String Format(String number) {
        return number;
    }
}
