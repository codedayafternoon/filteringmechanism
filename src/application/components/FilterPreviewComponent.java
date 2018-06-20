package application.components;

import java.util.ArrayList;
import java.util.List;

import domain.FilterContext;
import domain.filtercontroller.IFilterController;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;

public class FilterPreviewComponent implements IFilterHubListener {

	List<Filter> filters;
	private IFilterController controller;

	public FilterPreviewComponent(FilterContext context) {
		this.filters = new ArrayList<Filter>();
		this.controller = context.GetController();
		context.GetHub().AddFilterListener(this);
	}

	public void RemoveEntry(String containerName, String filterName) {
		this.controller.ChangeState(containerName, filterName, "reset");
	}

	public void Print() {
		System.out.println("==========================FilterPreviewComponent==============================");
		for (Filter f : this.filters) {
			System.out.println("[" + f.getName() + " " + f.GetState() + "]");
		}
		System.out.println("==============================================================================");
		System.out.println();
	}

	@Override
	public void FilterChanged(Filter filter) {
		if (this.filters.contains(filter)) {
			this.Print();
			return;
		}
		this.filters.add(filter);
		this.Print();
	}

	@Override
	public void FilterReset(Filter filter) {
		this.filters.remove(filter);
		this.Print();
	}

	@Override
	public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

	}

	@Override
	public void FilterUpdated(Filter filter) {

	}


	public void remove(String containerId, String filterId) {
		this.controller.ChangeState(containerId, filterId, "reset");
	}

}
