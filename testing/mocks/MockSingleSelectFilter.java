package testing.mocks;

import domain.filters.IInvalidator;
import domain.filters.INotifier;
import domain.filters.types.SingleSelectFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockSingleSelectFilter extends SingleSelectFilter {
    public MockSingleSelectFilter(IInvalidator invalidator, Object id, String name, INotifier notifier) {
        super(invalidator, id, name, notifier);
    }
}
