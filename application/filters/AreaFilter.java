package application.filters;

import domain.filters.INotifier;
import domain.filters.types.FreeTextFilter;

public class AreaFilter extends FreeTextFilter {

	public AreaFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}

}
