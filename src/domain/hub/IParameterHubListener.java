package domain.hub;

import domain.filters.Filter;

public interface IParameterHubListener {
	void ParameterAdded(Filter filter);
	void ParameterRemoved(Filter filter);
}
