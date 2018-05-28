 package application.infrastructure;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import domain.filtercontroller.IRequestConverter;
import domain.filters.Filter;
import domain.filters.types.CompositeFilter;

public class UrlQueryConverter implements IRequestConverter {

	IUrlBuilder _urlBuilder;
	
	public UrlQueryConverter(IUrlBuilder urlBuilder) {
		this._urlBuilder = urlBuilder;
	}
	
	@Override
	public String Convert(Map<Filter, Date> items) {
		
		Map<Filter, Date> sortedMap = 
				items.entrySet().stream()
			    .sorted(Entry.comparingByValue())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, HashMap::new));
		
		for(Filter f : sortedMap.keySet()) {
			this.addToUrlBuilder(f);
		}
		
		return this._urlBuilder.Build();
	}
	
	private void addToUrlBuilder(Filter f) {
		switch (f.GetMode()) {
		case BOOLEAN:
			this._urlBuilder.AddParameter(f.GetContainer().GetName(), f.Name);
			break;
		case RANGED:
		case SINGLE_VALUE:
			this._urlBuilder.AddParameter(f.Name, f.GetState());
			break;
		case COMPLEX:
			List<Filter> activeFilters = ((CompositeFilter)f).GetActiveFilters();
			for(Filter activeFilter : activeFilters)
				this.addToUrlBuilder(activeFilter);
		}
	}

	@Override
	public void AddCustomParameter(String paramName, String value) {
		this._urlBuilder.AddParameter(paramName, value);
	}

	@Override
	public void RemoveCustomParameter(String paramName, String value) {
		this._urlBuilder.RemoveParameter(paramName, value);
	}

	@Override
	public void RemoveCustomParameter(String paramName) {
		this._urlBuilder.RemoveParameter(paramName);
	}

	
	
	
}
