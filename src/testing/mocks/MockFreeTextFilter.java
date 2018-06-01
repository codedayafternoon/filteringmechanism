package testing.mocks;

import domain.filters.ICountable;
import domain.filters.INotifier;
import domain.filters.types.FreeTextFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockFreeTextFilter extends FreeTextFilter{

    int count;

    public MockFreeTextFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }

}
