package domain.filters.filterformatters;

import domain.filters.Filter;
import domain.filters.FilterFormatter;

public class DefaultFilterFormatter extends FilterFormatter {

    public DefaultFilterFormatter(Object id, String pattern) {
        super(id, pattern);
    }

    @Override
    public String Format(Filter filter){
        return filter.GetState();
    }

}
