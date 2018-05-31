package domain.hub;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;

public interface IFilterHubListener {
	void FilterChanged(Filter filter);
	void FilterReset(Filter filter);
	void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType);
}
