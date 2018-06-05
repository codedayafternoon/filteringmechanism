package domain.configuration;

import domain.filtercontroller.FilterContainer;
import domain.filters.Filter;

public interface IBuilderObserver {
    void FilterAdded(ActionType actionType, Filter filter);
    void FilterRemoved(ActionType actionType, Filter filter);
    void FilterUpdated(ActionType actionType, Filter filter);
    void ContainerAdded(ActionType actionType, FilterContainer container);
    void ContainerRemoved(ActionType actionType, FilterContainer container);
}
