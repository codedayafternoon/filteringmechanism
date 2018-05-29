package application.filters;

import java.util.List;

import domain.filters.INotifier;
import domain.filters.types.SingleTextFilter;

public class SortingFilter extends SingleTextFilter {

	public SortingFilter(Object id, String name, INotifier notifier, List<String> values) {
		super(id, name, notifier, values);
	}

}
