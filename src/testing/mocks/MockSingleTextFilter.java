package testing.mocks;

import domain.filters.INotifier;
import domain.filters.types.SingleTextFilter;

import java.util.List;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockSingleTextFilter extends SingleTextFilter {
    public MockSingleTextFilter(Object id, String name, INotifier notifier, List<String> values) {
        super(id, name, notifier, values);
    }
}
