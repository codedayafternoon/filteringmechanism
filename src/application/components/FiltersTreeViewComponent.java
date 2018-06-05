package application.components;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;

public class FiltersTreeViewComponent implements IFilterHubListener {

	@Override
	public void FilterChanged(Filter filter) {
		System.out.println("FiltersTreeViewComponent => filterAdded : " + filter.getName());
	}

	@Override
	public void FilterReset(Filter filter) {
		System.out.println("FiltersTreeViewComponent => FilterReset : " + filter.getName());
	}

	@Override
	public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

	}

	@Override
	public void FilterUpdated(Filter filter) {

	}

}
