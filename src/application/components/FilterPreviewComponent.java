package application.components;

import java.util.ArrayList;
import java.util.List;

import domain.filtercontroller.FilterController;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.ReservedState;
import domain.hub.IFilterHubListener;

public class FilterPreviewComponent implements IFilterHubListener {

	List<Filter> filters;
	private FilterController _filterController;

	public FilterPreviewComponent(FilterController filterController) {
		this.filters = new ArrayList<Filter>();
		this._filterController = filterController;
	}

	public void RemoveEntry(String containerName, String filterName) {
		this._filterController.ChangeState(containerName, filterName, "reset");
	}

	private void Print() {
		System.out.println("=========================================================");
		for (Filter f : this.filters) {
			System.out.println("[" + f.getName() + " " + f.GetState() + "]");
		}
		System.out.println("=========================================================");
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

}
