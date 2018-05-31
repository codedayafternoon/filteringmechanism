package domain.hub;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;

public interface IRequestHubListener {
    void RequestChanged(Filter filter);
    void RequestReset(Filter filter);
    void RequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType);
}
