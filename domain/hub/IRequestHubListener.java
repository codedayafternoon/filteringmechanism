package domain.hub;

import domain.filters.Filter;

public interface IRequestHubListener {
    void RequestAdded(Filter filter);
    void RequestRemoved(Filter filter);
}
