package domain.hub;

import domain.filters.Filter;

public interface IFilterHubListener {
	void FilterAdded(Filter filter);
	void FilterRemoved(Filter filter);
}
