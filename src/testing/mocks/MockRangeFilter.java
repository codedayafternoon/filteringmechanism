package testing.mocks;

import domain.filters.INotifier;
import domain.filters.types.RangeFilter;

import java.util.List;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockRangeFilter extends RangeFilter {
    public MockRangeFilter(Object id, String name, INotifier notifier, List<String> from, List<String> to) {
        super(id, name, notifier, from, to);
    }

    public List<String> GetFromValues() {
        return super.getRangeFrom().getItems();
    }

    public List<String> GetToValues() {
        return super.getRangeTo().getItems();
    }

    public String GetDefaultFrom() {
        return this.getRangeFrom().getDefaultValue();
    }

    public String GetDefaultTo() {
        return this.getRangeTo().getDefaultValue();
    }

    public String GetSelectedFrom() {
        return  super.getRangeFrom().getSelectedValue();
    }

    public String GetSelectedTo() {
        return  super.getRangeTo().getSelectedValue();
    }
}
