package domain.notifier;

import domain.filters.Filter;
import domain.filters.INotifier;

public class RequestNotifier implements INotifier {

	IRequestHub hub;
	
	public RequestNotifier(IRequestHub hub) {
		this.hub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.hub.NotifyRequestReset(filter);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.hub.NotifyRequestStateChanged(filter);
	}

}
