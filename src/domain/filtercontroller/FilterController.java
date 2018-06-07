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
		if(container == null)
			return;
		if(this.containers.contains(container))
			return;
		if(this.containers.stream().anyMatch(x->x.GetId().equals(container.GetId())))
			throw new Error("there is already a container with the same id " + container.GetId());

		this.containers.add(container);
	}


	public void RemoveContainerById(FilterContainer container) {
		if(container == null)
			return;
		FilterContainer c = this.GetContainerById(container.GetId());
		this.containers.remove(c);
	}

	public List<FilterContainer> GetContainers(){
		return this.containers;
	}

	public void ChangeState(Object containerId, Object filterId, ReservedState state) {
		if(containerId == null || filterId == null)
			return;
		if(this.DoChangeState(containerId, filterId, state.toString()))
			this.Update();
	}

	public void ChangeState(Object containerId, Object filterId, String state) {
		if(containerId == null || filterId == null || state == null)
			return;
		if(this.DoChangeState(containerId, filterId, state))
			this.Update();
	}

	/**
	 * change filters states without notify request handler
	 * @param containerId
	 * @param filterId
	 * @param state
	 */
	public boolean DoChangeState(Object containerId, Object filterId, String state) {
		if(containerId == null || filterId == null || state == null)
			return false;

		FilterContainer container = this.GetContainerById(containerId);
		if (container == null)
			return false;
		Filter filter = this.GetFilterById(container, filterId);
		if (filter == null)
			return false;
		filter.ChangeState(state);
		return true;
	}

//	public FilterContainer GetContainerByName(String name) {
//		FilterContainer container = this.containers.stream().filter(x -> x.GetName().equals(name)).findFirst().get();
//		return container;
//	}


	public FilterContainer GetContainerById(Object id) {
		for(FilterContainer c : this.containers){
			if(c.GetId().equals(id))
				return c;
		}
		return null;
	}

//	protected Filter GetFilterByName(FilterContainer container, String name) {
//		Filter filter = container.GetFilters().stream().filter(x -> x.getName().equals(name)).findFirst().get();
//		return filter;
//	}

	// TODO composite filters are not supported
	public void UpdateCount(Object containerId, Object filterId, int count) {
		FilterContainer container = this.GetContainerById(containerId);
		if (container == null)
			return;
		Filter filter = this.GetFilterById(container, filterId);
		if (filter == null)
			return;

		filter.SetCount(count);
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

    public Filter GetFilterById(FilterContainer container, Object filterId) {
		if(container == null)
			return null;
		Filter filter = container.GetFilterById(filterId);
		return filter;
    }

//	public FilterContainer FindContainer(Object id) {
//		for(FilterContainer c : this.containers){
//			if(c.GetId().equals(id))
//				return c;
//		}
//		return null;
//	}

    public void Clear() {
		this.containers.clear();
    }
}
