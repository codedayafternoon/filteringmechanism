package domain.filtercontroller;

import java.util.ArrayList;
import java.util.List;

import domain.filters.Filter;
import domain.filters.IInvalidator;

import javax.management.openmbean.KeyAlreadyExistsException;

public class FilterContainer implements IInvalidator{
	protected List<Filter> filters;
	protected String name;
	protected Object id;
	
	public FilterContainer(Object id, String name) {
		if(id == null || name == null)
			throw new Error("id or name cannot be null for container");
		this.filters = new ArrayList<Filter>();
		this.id = id;
		this.name = name;
	}
	
	public void AddFilter(Filter filter) {
		if(filter == null)
			return;
		filter.SetContainer(this);
		this.checkForSameId(filter.Id);
		this.checkForSameName(filter.getName());
		this.filters.add(filter);
	}

	private void checkForSameName(String name) {
		for(Filter f : this.filters){
			if(f.getName().equals(name))
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

	public Object GetId(){
		return this.id;
	}

	public void SetId(Object id){
		this.id = id;
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

    public Filter GetFilterById(Object id) {
		for(Filter f : this.filters)
		{
			if(f.Id.equals(id))
				return f;
		}
		return null;
    }

    @Override
	public String toString(){
		return "[id:" + this.id + "] " + this.name + " (" + filters.size() + ")";
	}

    public void RemoveFilter(Filter f) {
		if(!this.filters.contains(f))
			return;
		this.filters.remove(f);
    }

    public boolean UpdateFrom(FilterContainer container) {
		if(container == null)
			return false;
		boolean updated = false;
		if(!this.name.equals(container.GetName()))
		{
			this.name = container.GetName();
			updated = true;
		}

		return updated;
    }
}
