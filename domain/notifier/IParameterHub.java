package domain.notifier;

import domain.filters.Filter;
import domain.hub.IParameterHubListener;

public interface IParameterHub {
	void AddParameterListener(IParameterHubListener listener);
	void RemoveParameterListener(IParameterHubListener listener);
	void ClearParameterListeners();
	void NotifyParameterRemoved(Filter filter);
	void NotifyParameterAdded(Filter filter);
}
