package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;

public interface IFilterHub {
	void AddFilterListener(IFilterHubListener listener);
	void RemoveFilterListener(IFilterHubListener listener);
	void ClearFilterListeners();
	void NotifyFilterReset(Filter filter);
	void NotifyFilterStateChanged(Filter filter);
    void NotifyFilterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType);
    void NotifyFilterUpdated(Filter filter);
}
