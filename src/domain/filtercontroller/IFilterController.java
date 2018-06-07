package domain.filtercontroller;

import domain.filters.Filter;

import java.util.List;

public interface IFilterController {
    void AddContainer(FilterContainer container);
    void RemoveContainerById(FilterContainer container);
    FilterContainer GetContainerById(Object id);
    List<FilterContainer> GetContainers();
    void ChangeState(Object containerId, Object filterId, String state);
    Filter GetFilterById(FilterContainer container, Object filterId);
    void Clear();
}
