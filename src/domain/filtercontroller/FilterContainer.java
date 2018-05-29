package domain.filtercontroller;

import java.util.ArrayList;
import java.util.List;

import domain.filters.Filter;
import domain.filters.IInvalidator;

import javax.management.openmbean.KeyAlreadyExistsException;

public class FilterContainer implements IInvalidator{
	protected List<Filter> filters;
	protected String name;
	
	public FilterContainer(String name) {
		this.filters = new ArrayList<Filter>();
		this.name = name;
	}
	
	public void AddFilter(Filter filter) {
		filter.SetContainer(this);
		this.checkForSameId(filter.Id);
		this.checkForSameName(filter.Name);
		this.filters.add(filter);
	}

	private void checkForSameName(String name) {
		for(Filter f : this.filters){
			if(f.Name.equals(name))
				throw new IllegalArgumentException("there is already a filter " + f + " with the same name");
		}
	}

	private void checkForSameId(Object id) {
		for(Filter f : this.filters){
			if(f.Id.equals(id))
				throw new KeyAlreadyExistsException("there is already a filter " + f + " with the same key");
		}
	}


	public void InvalidateAll(IInvalidatable except) {
		for (Filter filter : this.filters) {
			if(filter == except)
				continue;
			if(filter instanceof IInvalidatable)
				filter.Reset();
		}
	}

	public void Clear(){
		this.filters.clear();
	}
	
	public List<Filter> GetFilters() {
		return this.filters;
	}
	public String GetName() {
		return this.name;
	}
}
