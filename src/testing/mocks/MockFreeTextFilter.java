package testing.mocks;

import domain.filters.ICountable;
import domain.filters.INotifier;
import domain.filters.types.FreeTextFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockFreeTextFilter extends FreeTextFilter implements ICountable {

    int count;

    public MockFreeTextFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }

    @Override
    public void SetCount(int count) {
        this.count = count;
    }

    @Override
    public int GetCount() {
        return this.count;
    }
}
