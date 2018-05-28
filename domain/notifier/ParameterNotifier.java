package domain.notifier;

import domain.filters.Filter;
import domain.filters.INotifier;

public class ParameterNotifier implements INotifier{
	
	IParameterHub parameterHub;
	
	public ParameterNotifier(IParameterHub hub) {
		this.parameterHub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.parameterHub.NotifyParameterRemoved(filter);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.parameterHub.NotifyParameterAdded(filter);
	}

}
