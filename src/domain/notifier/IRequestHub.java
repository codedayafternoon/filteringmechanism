package domain.notifier;

import domain.filters.Filter;
import domain.hub.IFilterHubListener;
import domain.hub.IRequestHubListener;

public interface IRequestHub {
	void AddRequestListener(IRequestHubListener listener);
	void RemoveRequestListener(IRequestHubListener listener);
	void ClearRequestListeners();
	void NotifyRequestReset(Filter filter);
	void NotifyRequestStateChanged(Filter filter);
}
