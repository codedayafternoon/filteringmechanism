package domain.hub;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.interconnections.EventSubjectPair;
import domain.hub.interconnections.FilterEvent;
import domain.hub.interconnections.FilterInterconnection;
import domain.hub.results.IResult;
import domain.notifier.IFilterHub;
import domain.notifier.IParameterHub;
import domain.notifier.IRequestHub;

public class Hub implements IParameterHub, IFilterHub, IRequestHub, IHub {

	private FilterController filterController;

	private Map<Filter, Date> filters;
	private Map<Filter, Date> parameters;
	private Map<Filter, Date> requests;

	private List<IParameterHubListener> parameterListeners;
	private List<IFilterHubListener> filterListeners;
	private List<IRequestHubListener> requestListeners;

	private List<IResultHubListener> resultListeners;

	public List<FilterInterconnection> Interconnections;


	public Hub() {
		this.filters = new HashMap<>();
		this.parameters = new HashMap<>();
		this.requests = new HashMap<>();

		this.parameterListeners = new ArrayList<IParameterHubListener>();
		this.filterListeners = new ArrayList<IFilterHubListener>();
		this.requestListeners = new ArrayList<IRequestHubListener>();

		this.resultListeners = new ArrayList<>();

		this.Interconnections = new ArrayList<>();
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
	public void NotifyFilterReset(Filter filter) {
		this.removeFilter(filter);
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterReset(filter);

		this.notifyInterconnections(filter, FilterEvent.Reset);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		this.addFilter(filter);
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterChanged(filter);

		this.notifyInterconnections(filter, FilterEvent.StateChange);
	}

	@Override
	public void NotifyFilterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterPropertyChanged(filter, old, aNew, propType);
	}

	@Override
	public void NotifyFilterUpdated(Filter filter) {
		for(IFilterHubListener listener : this.filterListeners)
			listener.FilterUpdated(filter);
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
	public void NotifyParameterReset(Filter filter) {
		this.removeParameter(filter);
		for(IParameterHubListener listener : this.parameterListeners)
			listener.ParameterReset(filter);
	}

	@Override
	public void NotifyParameterStateChanged(Filter filter) {
		this.addParameter(filter);
		for(IParameterHubListener listener : this.parameterListeners)
			listener.ParameterChanged(filter);

		this.notifyInterconnections(filter, FilterEvent.StateChange);
	}

	@Override
	public void NotifyParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {
		for(IParameterHubListener listener : this.parameterListeners)
			listener.ParameterPropertyChanged(filter, old, aNew, propType);
	}

	@Override
	public void NotifyParameterUpdated(Filter filter) {
		for(IParameterHubListener listener : this.parameterListeners)
			listener.ParameterUpdated(filter);
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
	public void NotifyRequestReset(Filter filter) {
		this.removeParameter(filter);
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestReset(filter);
	}

	@Override
	public void NotifyRequestStateChanged(Filter filter) {
		this.addRequest(filter);
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestChanged(filter);

		this.notifyInterconnections(filter, FilterEvent.StateChange);
	}

	@Override
	public void NotifyRequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestPropertyChanged(filter, old, aNew, propType);
	}

	@Override
	public void NotifyRequestUpdated(Filter filter) {
		for(IRequestHubListener listener : this.requestListeners)
			listener.RequestUpdated(filter);
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

	// ===================================== Start Helpers ==============================================
	private void notifyInterconnections(Filter filter, FilterEvent state) {
		List<FilterInterconnection> filtered = getFilterActorsFromInterconnections(filter, state);
		notifySubjectsFromInterconnections(filtered);
	}

	private List<FilterInterconnection> getFilterActorsFromInterconnections(Filter filter, FilterEvent state) {
		return this.Interconnections.stream().filter(x -> x.When.Event == state && x.When.GetSubjects().contains(filter)).collect(Collectors.toList());
	}

	private void notifySubjectsFromInterconnections(List<FilterInterconnection> filtered) {
		if(filtered.isEmpty())
			return;

		for(FilterInterconnection interconnection : filtered){
			this.notifyInterconnection(interconnection);
		}

	}

	private void notifyInterconnection(FilterInterconnection interconnection) {
		List<EventSubjectPair> clauses = interconnection.Then;
		for(EventSubjectPair clause : clauses){
			this.notifyClause(clause);
		}
	}

	private void notifyClause(EventSubjectPair clause) {
		List<Filter> subjects = clause.GetSubjects();
		FilterEvent event = clause.Event;
		for(Filter f : subjects){
			if(event == FilterEvent.Reset)
				f.Reset();
			if(event == FilterEvent.StateChange)
				f.ChangeState(clause.Parameters);
		}
	}
	// ===================================== End Helpers ==============================================

	public void Execute(HubCommand command){
		if(this.filterController == null)
			return;

		FilterContainer container = this.filterController.GetContainerById(command.ContainerId);
		if(container == null)
			return;

		Filter filter = this.filterController.GetFilterById(container, command.FilterId);
		if(filter == null)
			return;

		if(!filter.getName().equals(command.FilterName)){
			filter.setName(command.FilterName);
		}

		if(command.State != null ){
			this.filterController.DoChangeState(command.ContainerId, command.FilterId, command.State);
		}

		if( command.Count >= 0)
		{
			this.filterController.UpdateCount(command.ContainerId, command.FilterId, command.Count);
		}
	}

	public void Execute(List<HubCommand> commands){
		if(commands == null)
			return;
		for(HubCommand c : commands)
			this.Execute(c);
	}

	// ============================================= START IHub =====================================================
	@Override
	public void SetFilterController(FilterController controller) {
		this.filterController = controller;
	}


	@Override
	public void ResultReceived(IResult result) {
		Object resultObject = result.GetResults();
		for(IResultHubListener listener : this.resultListeners)
			listener.ResultReceived(resultObject);
		List<HubCommand> commands = result.GetHubCommands();
		this.Execute(commands);
	}

	@Override
	public void AddResultListener(IResultHubListener listener) {
		if(this.resultListeners.contains(listener))
			return;
		this.resultListeners.add(listener);
	}

	@Override
	public void RemoveResultListener(IResultHubListener listener) {
		if(this.resultListeners.contains(listener))
			this.resultListeners.remove(listener);
	}

	@Override
	public void ClearResultListeners() {
		this.resultListeners.clear();
	}

	@Override
	public void ClearAll() {
		this.ClearResultListeners();
		this.ClearRequestListeners();
		this.ClearParameterListeners();
		this.ClearFilterListeners();
	}

	// ============================================= END IHub =====================================================

}
