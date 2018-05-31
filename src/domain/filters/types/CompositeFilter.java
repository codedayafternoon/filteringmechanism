package domain.filters.types;

import java.util.ArrayList;
import java.util.List;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;

public abstract class CompositeFilter extends Filter implements INotifier {

	private List<Filter> filters;
	private List<Filter> activeFilters;
	
	public CompositeFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
		this.filters = new ArrayList<Filter>();
		this.activeFilters = new ArrayList<>();
	}
	
	public void AddFilter(Filter f) {
		if(this.filters.contains(f))
			return;
		this.filters.add(f);
	}
	
	public void RemoveFilter(Filter f) {
		if(this.filters.contains(f))
			this.filters.remove(f);
	}
	
	@Override
	protected void DoChangeState(String state) {
		String[] parts = state.split(":");
		if(parts.length != 2)
			return;
		String filterName = parts[0];
		String s = parts[1];
		
		Filter f = this.filters.stream().filter(x->x.getName().equals(filterName)).findFirst().get();

		f.ChangeState(s);
		
	}
	
	@Override
	public String GetState() {
		String state = "";
		for(Filter f : this.filters)
			state += f.GetState() + " | ";
		return state.substring(0, state.length() - 3);
	}
	
	@Override
	public FilterMode GetMode() {
		return FilterMode.COMPLEX;
	}
	
	@Override
	public void Reset() {
		for(Filter f : this.filters)
			f.Reset();
	}
	
	@Override
	public void NotifyFilterReset(Filter filter)
	{
		if(this.activeFilters.contains(filter))
			this.activeFilters.remove(filter);
		this.notifier.NotifyFilterReset(this);
	}

	@Override
	public void NotifyFilterStateChanged(Filter filter) {
		if(!this.activeFilters.contains(filter))
			this.activeFilters.add(filter);
		this.notifier.NotifyFilterStateChanged(this);
	}

	public List<Filter> GetActiveFilters() {
		return this.activeFilters;
	}
	
}
