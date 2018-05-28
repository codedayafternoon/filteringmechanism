package application.components;

import application.infrastructure.IUrlBuilder;
import domain.filters.Filter;
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
		case BOOLEAN:
			this._urlBuilder.AddParameter(f.GetContainer().GetName(), f.Name);
			break;
		case RANGED:
			this._urlBuilder.ReplaceParameter(f.Name, f.GetState());
			break;
		case SINGLE_VALUE:
			this._urlBuilder.AddParameter(f.Name, f.GetState());
			break;
		case COMPLEX:
			List<Filter> activeFilters = ((CompositeFilter)f).GetActiveFilters();
			for(Filter activeFilter : activeFilters)
				this.AddToUrlBuilder(activeFilter);
			break;
		}
	}

	private void Remove(Filter f) {
		this.RemoveFromUrlBuilder(f);
		this.print();
	}
	
	private void RemoveFromUrlBuilder(Filter f) {
		switch (f.GetMode()) {
		case BOOLEAN:
			this._urlBuilder.RemoveParameter(f.GetContainer().GetName(), f.Name);
			break;
		case RANGED:
		case SINGLE_VALUE:
			this._urlBuilder.RemoveParameter(f.Name, f.GetState());
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
	public void ParameterAdded(Filter filter) {
		this.Add(filter);
	}

	@Override
	public void ParameterRemoved(Filter filter) {
		this.Remove(filter);
	}

	@Override
	public void FilterAdded(Filter filter) {
		this.Add(filter);
	}

	@Override
	public void FilterRemoved(Filter filter) {
		this.Remove(filter);
	}

}
