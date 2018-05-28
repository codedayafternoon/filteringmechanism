package testing.mocks;

import domain.filters.INotifier;
import domain.filters.types.CompositeFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockCompositeFilter extends CompositeFilter {
    public MockCompositeFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }
}
