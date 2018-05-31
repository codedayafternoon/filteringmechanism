package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;

public class RequestNotifier implements INotifier {

	IRequestHub requestHub;
	
	public RequestNotifier(IRequestHub hub) {
		this.requestHub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.requestHub.NotifyRequestReset(filter);
	}

	@Override
	public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
		this.requestHub.NotifyRequestPropertyChanged(filter, old, _new, propType);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.requestHub.NotifyRequestStateChanged(filter);
	}

}
