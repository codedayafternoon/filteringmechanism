package domain.hub;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;

public interface IParameterHubListener {
	void ParameterChanged(Filter filter);
	void ParameterReset(Filter filter);
    void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType);
    void ParameterUpdated(Filter filter);
}
