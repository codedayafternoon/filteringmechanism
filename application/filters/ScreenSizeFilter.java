package application.filters;

import domain.filters.ICountable;
import domain.filters.IInvalidator;
import domain.filters.INotifier;
import domain.filters.types.SingleSelectFilter;

public class ScreenSizeFilter extends SingleSelectFilter implements ICountable {

	int count;
	
	public ScreenSizeFilter(IInvalidator invalidator, Object id, String name, INotifier notifier) {
		super(invalidator, id, name, notifier);
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
