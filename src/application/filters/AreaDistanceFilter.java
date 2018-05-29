package application.filters;

import domain.filters.Filter;
import domain.filters.INotifier;
import domain.filters.types.CompositeFilter;

public class AreaDistanceFilter extends CompositeFilter {

	public AreaDistanceFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}

}
