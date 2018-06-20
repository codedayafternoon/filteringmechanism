package application.components;

import application.infrastructure.SimpleConsolePrinter;
import domain.FilterContext;
import domain.filtercontroller.IFilterController;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;

public class FiltersTreeViewComponent implements IFilterHubListener {

	IFilterController controller;
	SimpleConsolePrinter printer;

	public FiltersTreeViewComponent(FilterContext context) {
		context.GetHub().AddFilterListener(this);
		this.controller = context.GetController();
		this.printer = new SimpleConsolePrinter();
	}

	@Override
	public void FilterChanged(Filter filter) {
		System.out.println("FiltersTreeViewComponent => filterAdded : " + filter.getName());
		this.Print();
	}

	@Override
	public void FilterReset(Filter filter) {
		System.out.println("FiltersTreeViewComponent => FilterReset : " + filter.getName());
		this.Print();
	}

	@Override
	public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

	}

	@Override
	public void FilterUpdated(Filter filter) {

	}

	public void Print() {
		System.out.println("=========================FiltersTreeViewComponent=========================");
		this.printer.Print(System.out,this.controller.GetContainers());
		System.out.println("==========================================================================");
		System.out.println();
	}

	public void changeState(String containerId, String filterId, String state) {
		this.controller.ChangeState(containerId, filterId, state);
	}
}
