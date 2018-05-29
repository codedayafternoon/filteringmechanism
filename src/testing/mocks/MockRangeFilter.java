package testing.mocks;

import domain.filters.INotifier;
import domain.filters.types.RangeFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockRangeFilter extends RangeFilter {
    public MockRangeFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }
}
