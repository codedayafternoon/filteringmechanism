package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IParameterHubListener;

public interface IParameterHub {
	void AddParameterListener(IParameterHubListener listener);
	void RemoveParameterListener(IParameterHubListener listener);
	void ClearParameterListeners();
	void NotifyParameterReset(Filter filter);
	void NotifyParameterStateChanged(Filter filter);
	void NotifyParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType);
}
