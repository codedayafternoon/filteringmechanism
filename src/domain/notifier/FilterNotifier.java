package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;

public class FilterNotifier implements INotifier {

	IFilterHub filterHub;
	
	public FilterNotifier(IFilterHub hub) {
		this.filterHub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.filterHub.NotifyFilterReset(filter);
	}

	@Override
	public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
		this.filterHub.NotifyFilterPropertyChanged(filter, old, _new, propType);
	}

	@Override
	public void NotifyFilterUpdated(Filter filter) {
		this.filterHub.NotifyFilterUpdated(filter);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.filterHub.NotifyFilterStateChanged(filter);
	}

}
