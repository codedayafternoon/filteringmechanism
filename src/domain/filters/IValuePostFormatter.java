package domain.filters;

import java.util.List;

public interface IValuePostFormatter {
    List<String> Extract(String value);
    String Format(String number);
}
