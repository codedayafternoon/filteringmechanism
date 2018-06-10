package domain.hub.interconnections;

import domain.filtercontroller.FilterContainer;
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

    public void Who(Filter filter){
        if(this.OfFilters.contains(filter))
            return;
        this.OfFilters.add(filter);
    }

    public void Who(FilterContainer container){
        if(container == null)
            return;
        if(container.isEmpty())
            return;
        for(Filter f : container.GetFilters()){
            this.Who(f);
        }
    }

    public void ClearFilters(Filter filter){
        this.OfFilters.clear();
    }

    public void RemoveWho(Filter filter){
        if(this.OfFilters.contains(filter))
            this.OfFilters.remove(filter);
    }

    public List<Filter> GetSubjects(){
        return this.OfFilters;
    }
}
