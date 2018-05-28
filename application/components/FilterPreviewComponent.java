package application.components;

import java.util.ArrayList;
import java.util.List;

import domain.filtercontroller.FilterController;
import domain.filters.Filter;
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
			System.out.println("[" + f.Name + " " + f.GetState() + "]");
		}
		System.out.println("=========================================================");
	}

	@Override
	public void FilterAdded(Filter filter) {
		if (this.filters.contains(filter)) {
			this.Print();
			return;
		}
		this.filters.add(filter);
		this.Print();
	}

	@Override
	public void FilterRemoved(Filter filter) {
		this.filters.remove(filter);
		this.Print();
	}

}
