package domain.filtercontroller;

import domain.filters.Filter;
import domain.notifier.NotifierChannelType;

import java.util.List;

public interface IFilterController {
    void AddContainer(FilterContainer container);
    void RemoveContainerById(FilterContainer container);
    FilterContainer GetContainerById(Object id);
    List<FilterContainer> GetContainers();
    void ChangeState(Object containerId, Object filterId, String state);
    Filter GetFilterById(FilterContainer container, Object filterId);
    void MakeRequestWithCurrentState();
    void MakeDirectRequest(String url);
    List<Filter> GetFiltersByChannel(NotifierChannelType filterChannel);
    void Clear();
}
