package application.components;

import domain.filters.Filter;
import domain.hub.IFilterHubListener;

public class FiltersTreeViewComponent implements IFilterHubListener {

	@Override
	public void FilterAdded(Filter filter) {
		System.out.println("FiltersTreeViewComponent => filterAdded : " + filter.Name);
	}

	@Override
	public void FilterRemoved(Filter filter) {
		System.out.println("FiltersTreeViewComponent => FilterRemoved : " + filter.Name);
	}

}
