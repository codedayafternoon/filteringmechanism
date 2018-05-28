package domain.notifier;

import domain.filters.Filter;
import domain.filters.INotifier;

public class FilterNotifier implements INotifier {

	IFilterHub filterHub;
	
	public FilterNotifier(IFilterHub hub) {
		this.filterHub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.filterHub.NotifyFilterRemoved(filter);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.filterHub.NotifyFilterAdded(filter);
	}

}
