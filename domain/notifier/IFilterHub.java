package domain.notifier;

import domain.filters.Filter;
import domain.hub.IFilterHubListener;

public interface IFilterHub {
	void AddFilterListener(IFilterHubListener listener);
	void RemoveFilterListener(IFilterHubListener listener);
	void ClearFilterListeners();
	void NotifyFilterRemoved(Filter filter);
	void NotifyFilterAdded(Filter filter);
}
