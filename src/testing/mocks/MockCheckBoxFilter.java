package testing.mocks;

import domain.filters.ICountable;
import domain.filters.INotifier;
import domain.filters.types.CheckBoxFilter;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class MockCheckBoxFilter extends CheckBoxFilter implements ICountable {
    int Count = 0;
    public MockCheckBoxFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }

    @Override
    public void SetCount(int count) {
        this.Count = count;
    }

    @Override
    public int GetCount() {
        return this.Count;
    }
}
