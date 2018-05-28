package domain.hub;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.filtercontroller.FilterController;
import domain.filters.Filter;
import domain.notifier.IFilterHub;
import domain.notifier.IParameterHub;
import domain.notifier.IRequestHub;

public class Hub implements IParameterHub, IFilterHub, IRequestHub, IHub {
	
	private Map<Filter, Date> filters;
	private Map<Filter, Date> parameters;
	private Map<Filter, Date> requests;
	private FilterController filterController;
	private List<IParameterHubListener> parameterListeners;
	private List<IFilterHubListener> filterListeners;
	private List<IRequestHubListener> requestListeners;

	public Hub() {
		this.filters = new HashMap<>();
		this.parameters = new HashMap<>();
		this.requests = new HashMap<>();
		this.parameterListeners = new ArrayList<IParameterHubListener>();
		this.filterListeners = new ArrayList<IFilterHubListener>();
		this.requestListeners = new ArrayList<IRequestHubListener>();
	}

	// ===================================== REGION Filters ==============================================
	@Override
	public void AddFilterListener(IFilterHubListener listener) {
		if(this.filterListeners.contains(listener))
			return;
		this.filterListeners.add(listener);
	}

	@Override
	public void RemoveFilterListener(IFilterHubListener listener) {
		if(this.filterListeners.contains(listener))
			this.filterListeners.remove(listener);
	}

	@Override
	public void ClearFilterListeners() {
		this.filterListeners.clear();
	}

	@Override
	public void NotifyFilterRemoved(Filter filter) {
		this.removeFilter(filter);
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterRemoved(filter);
	}

	@Override
	public void NotifyFilterAdded(Filter filter) {
		this.addFilter(filter);
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterAdded(filter);
	}
	
	private void addFilter(Filter filter) {
		if (this.filters.containsKey(filter))
			return;
		this.filters.put(filter, new Date());
	}

	private void removeFilter(Filter filter) {
		if (this.filters.containsKey(filter)) {
			this.filters.remove(filter);
		}
	}

	public Map<Filter, Date> GetFilters() {
		return this.filters;
	}

	// ===================================== END REGION Filters ==============================================
	
	
	// ===================================== REGION Parameters ==============================================
	@Override
	public void AddParameterListener(IParameterHubListener listener) {
		if(this.parameterListeners.contains(listener))
			return;
		this.parameterListeners.add(listener);
	}

	@Override
	public void RemoveParameterListener(IParameterHubListener listener) {
		if(this.parameterListeners.contains(listener))
			this.parameterListeners.remove(listener);
	}

	@Override
	public void ClearParameterListeners() {
		this.parameterListeners.clear();
	}

	@Override
	public void NotifyParameterRemoved(Filter filter) {
		this.removeParameter(filter);
		for(IParameterHubListener listener : this.parameterListeners)
			listener.ParameterRemoved(filter);
	}

	@Override
	public void NotifyParameterAdded(Filter filter) {
		this.addParameter(filter);
		for(IParameterHubListener listenter : this.parameterListeners)
			listenter.ParameterAdded(filter);
	}
	
	private void addParameter(Filter filter) {
		if (this.filters.containsKey(filter))
			return;
		this.filters.put(filter, new Date());
	}

	private void removeParameter(Filter filter) {
		if (this.filters.containsKey(filter)) {
			this.filters.remove(filter);
		}
	}
	
	public Map<Filter, Date> GetParameters(){
		return this.parameters;
	}

	// ===================================== END REGION Parameters ==============================================
	
	// ===================================== REGION Requests ==============================================
	
	@Override
	public void AddRequestListener(IRequestHubListener listener) {
		if(this.requestListeners.contains(listener))
			return;
		this.requestListeners.add(listener);
	}

	@Override
	public void RemoveRequestListener(IRequestHubListener listener) {
		if(this.requestListeners.contains(listener))
			this.requestListeners.remove(listener);
	}

	@Override
	public void ClearRequestListeners() {
		this.requestListeners.clear();
	}

	@Override
	public void NotifyRequestRemoved(Filter filter) {
		this.removeParameter(filter);
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestRemoved(filter);
	}

	@Override
	public void NotifyRequestAdded(Filter filter) {
		this.addRequest(filter);
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestAdded(filter);
	}

	private void addRequest(Filter filter) {
		if (this.requests.containsKey(filter))
			return;
		this.requests.put(filter, new Date());
	}

	private void removeRequest(Filter filter) {
		if (this.requests.containsKey(filter)) {
			this.requests.remove(filter);
		}
	}

	public Map<Filter, Date> GetRequests(){
		return this.requests;
	}

	// ===================================== END REGION Filter ==============================================

	public void Execute(HubCommand command){
		if(this.filterController == null)
			return;

		if(command.State != null ){
			this.filterController.DoChangeState(command.ContainerName, command.FilterName, command.State);
		}

		if( command.Count >= 0)
		{
			this.filterController.UpdateCount(command.ContainerName, command.FilterName, command.Count);
		}
	}

	public void Execute(List<HubCommand> commands){
		for(HubCommand c : commands)
			this.Execute(c);

	}


//	public void UpdateCounts(List<HubCommand> items) {
//		if (this.filterController == null)
//			return;
//
//		for (HubCommand item : items) {
//
//			this.filterController.UpdateCount(item.ContainerName, item.FilterName, item.Count);
//		}
//	}
//
//	public void UpdateStates(List<HubCommand> items) {
//		if (this.filterController == null)
//			return;
//
//		for (HubCommand item : items) {
//			this.filterController.DoChangeState(item.ContainerName, item.FilterName, item.State);
//		}
//	}


	
	@Override
	public void SetFilterController(FilterController controller) {
		this.filterController = controller;
	}


}
