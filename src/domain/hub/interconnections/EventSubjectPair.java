package domain.hub.interconnections;

import domain.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class EventSubjectPair {
    public FilterEvent Event;
    private List<Filter> OfFilters;
    public String Parameters;

    public EventSubjectPair() {
        this.OfFilters = new ArrayList<>();
    }

    public void AddFilter(Filter filter){
        if(this.OfFilters.contains(filter))
            return;
        this.OfFilters.add(filter);
    }

    public void ClearFilters(Filter filter){
        this.OfFilters.clear();
    }

    public void RemoveFilter(Filter filter){
        if(this.OfFilters.contains(filter))
            this.OfFilters.remove(filter);
    }

    public List<Filter> GetFilters(){
        return this.OfFilters;
    }
}
