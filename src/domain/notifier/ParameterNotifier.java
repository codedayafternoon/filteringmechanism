package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;

public class ParameterNotifier implements INotifier{
	
	IParameterHub parameterHub;
	
	public ParameterNotifier(IParameterHub hub) {
		this.parameterHub = hub;
	}
	
	@Override
	public void NotifyFilterReset(Filter filter) {
		this.parameterHub.NotifyParameterReset(filter);
	}

	@Override
	public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
		this.parameterHub.NotifyParameterPropertyChanged(filter, old, _new, propType);
	}

	@Override
	public void NotifyFilterUpdated(Filter filter) {
		this.parameterHub.NotifyParameterUpdated(filter);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.parameterHub.NotifyParameterStateChanged(filter);
	}

}
