package application.filters;

import java.util.List;

import domain.filters.INotifier;
import domain.filters.types.SingleTextFilter;

public class PageFilter extends SingleTextFilter {

	public PageFilter(Object id, String name, INotifier notifier, List<String> values) {
		super(id, name, notifier, values);
	}

}
