package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;
import domain.hub.IRequestHubListener;

public interface IRequestHub {
	void AddRequestListener(IRequestHubListener listener);
	void RemoveRequestListener(IRequestHubListener listener);
	void ClearRequestListeners();
	void NotifyRequestReset(Filter filter);
	void NotifyRequestStateChanged(Filter filter);
    void NotifyRequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType);
    void NotifyRequestUpdated(Filter filter);
}
