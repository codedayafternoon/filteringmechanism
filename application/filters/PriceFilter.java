package application.filters;

import domain.filters.INotifier;
import domain.filters.types.RangeFilter;

public class PriceFilter extends RangeFilter {

	public PriceFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}


}
