package application.components;

import domain.buildins.IUrlBuilder;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.types.CompositeFilter;
import domain.hub.IFilterHubListener;
import domain.hub.IParameterHubListener;

import java.util.List;

public class UrlManagerComponent implements IFilterHubListener, IParameterHubListener {
	IUrlBuilder _urlBuilder;
	
	public UrlManagerComponent(IUrlBuilder urlBuilder) {
		this._urlBuilder = urlBuilder;
	}
	
	private void Add(Filter f) {
		this.AddToUrlBuilder(f);
		this.print();
	}
	
	private void AddToUrlBuilder(Filter f) {
		switch (f.GetMode()) {
			case SIMPLE:
				this._urlBuilder.AddParameter(f.GetParameterKey(), f.GetParameterValue());
				break;
			case COMPLEX:
				List<Filter> activeFilters = ((CompositeFilter)f).GetActiveFilters();
				for(Filter activeFilter : activeFilters)
					this.AddToUrlBuilder(activeFilter);
		}

	}

	private void Remove(Filter f) {
		this.RemoveFromUrlBuilder(f);
		this.print();
	}
	
	private void RemoveFromUrlBuilder(Filter f) {
		switch (f.GetMode()) {
			case SIMPLE:
				this._urlBuilder.RemoveParameter(f.GetParameterKey(), f.GetParameterValue());
				break;
			case COMPLEX:
				List<Filter> activeFilters = ((CompositeFilter)f).GetActiveFilters();
				for(Filter activeFilter : activeFilters)
					this.RemoveFromUrlBuilder(activeFilter);
		}

	}
	
	public void print() {
		System.out.println("url=>http://[" + this._urlBuilder.Peek() + "]");
	}

	@Override
	public void ParameterChanged(Filter filter) {
		this.Add(filter);
	}

	@Override
	public void ParameterReset(Filter filter) {
		this.Remove(filter);
	}

	@Override
	public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

	}

	@Override
	public void ParameterUpdated(Filter filter) {

	}

	@Override
	public void FilterChanged(Filter filter) {
		this.Add(filter);
	}

	@Override
	public void FilterReset(Filter filter) {
		this.Remove(filter);
	}

	@Override
	public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

	}

	@Override
	public void FilterUpdated(Filter filter) {

	}

}
