package domain.filtercontroller;

import java.util.*;

import domain.configuration.Builder;
import domain.filters.Filter;
import domain.filters.ICountable;
import domain.filters.ReservedState;
import domain.hub.Hub;

public class FilterController {

	protected List<FilterContainer> containers;
	protected Hub hub;
	protected IRequestHandler requestHandler;
	protected IRequestConverter requestConverter;
	private Builder builder;

	public FilterController(List<FilterContainer> containers, Hub hub, IRequestHandler handler,
			IRequestConverter requestConverter) {
		this.containers = containers;
		this.hub = hub;
		this.requestHandler = handler;
		this.requestConverter = requestConverter;
		this.hub.SetFilterController(this);
	}

	public FilterController( Hub hub, IRequestHandler handler, IRequestConverter requestConverter) {
		this.containers = new ArrayList<>();
		this.hub = hub;
		this.requestHandler = handler;
		this.requestConverter = requestConverter;
		this.hub.SetFilterController(this);
	}

	public Hub GetHub(){
		return this.hub;
	}

	public void AddContainer(FilterContainer container){
		this.containers.add(container);
	}

	public List<FilterContainer> GetContainers(){
		return this.containers;
	}

	public Builder GetBuilder(){
		if(this.builder == null)
			this.builder = new Builder(this);

		return this.builder;
	}

	public void ChangeState(String containerName, String filterName, ReservedState state) {
		if(this.DoChangeState(containerName, filterName, state.toString()))
			this.Update();
	}

	public void ChangeState(String containerName, String filterName, String state) {
		if(this.DoChangeState(containerName, filterName, state))
			this.Update();
	}

	/**
	 * change filters states without notify request handler
	 * @param containerName
	 * @param filterName
	 * @param state
	 */
	public boolean DoChangeState(String containerName, String filterName, String state) {
		System.out.println("ChangeState of " + containerName + "==>" + filterName + " to " + state);
		FilterContainer container = this.GetContainerByName(containerName);
		if (container == null)
			return false;
		Filter filter = this.GetFilterByName(container, filterName);
		if (filter == null)
			return false;
		filter.ChangeState(state);
		return true;
	}

	protected FilterContainer GetContainerByName(String name) {
		FilterContainer container = this.containers.stream().filter(x -> x.GetName().equals(name)).findFirst().get();
		return container;
	}

	protected Filter GetFilterByName(FilterContainer container, String name) {
		Filter filter = container.GetFilters().stream().filter(x -> x.getName().equals(name)).findFirst().get();
		return filter;
	}

	// TODO composite filters are not supported
	public void UpdateCount(String containerName, String filterName, int count) {
		FilterContainer container = this.GetContainerByName(containerName);
		if (container == null)
			return;
		Filter filter = this.GetFilterByName(container, filterName);
		if (filter == null)
			return;
		if (!(filter instanceof ICountable))
			return;

		((ICountable)filter).SetCount(count);

		// TODO replace with propertychanged from inside the filter
		filter.TriggerStateChanged(); // TODO execute with flag? not always..
	}

	private void updateCountHelper(String container, String filterName, int count) {

	}


	public void Update() {
		Map<Filter, Date> s = getRequestParameters();
		String request = this.requestConverter.Convert(s);
		this.requestHandler.makeRequest(request);
	}

	private Map<Filter, Date> getRequestParameters() {
		Map<Filter, Date> s = new HashMap<Filter, Date>();

		if(this.requestHandler.IsRetrieveFromFilters())
			s.putAll(this.hub.GetFilters());
		if(this.requestHandler.IsRetrieveFromParameters())
			s.putAll(this.hub.GetParameters());
		if(this.requestHandler.IsRetrieveFromRequest())
			s.putAll(this.hub.GetRequests());
		return s;
	}

    public Filter GetFilterById(String containerName, Object id) {
		FilterContainer container = this.GetContainerByName(containerName);
		Filter filter = container.GetFilterById(id);
		return filter;
    }
}
