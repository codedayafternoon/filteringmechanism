package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filters.Filter;

public interface IActionObserver {
    void ContainerAdded(ActionType actionType, FilterContainer container);
    void FilterAdded(ActionType actionType, Filter f);
    void ContainerRemoved(ActionType actionType, FilterContainer container);
    void FilterRemoved(ActionType actionType, Filter f);
}
