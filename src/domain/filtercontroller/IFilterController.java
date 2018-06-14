package domain.filtercontroller;

import domain.filters.Filter;
import domain.notifier.NotifierChannelType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IFilterController {
    void AddContainer(FilterContainer container);
    void RemoveContainerById(FilterContainer container);
    FilterContainer GetContainerById(Object id);
    List<FilterContainer> GetContainers();
    void ChangeState(Object containerId, Object filterId, String state);
    Filter GetFilterById(FilterContainer container, Object filterId);
    void MakeRequestWithCurrentState();
    void MakeDirectRequest(String url);
    void ResetAllWithoutRequestPropagation();
    List<Filter> GetFiltersByChannel(NotifierChannelType filterChannel);
    Map<Filter, Date> GetCurrentSelectedRequestParameters();
    String GetCurrentConvertedRequest();
    void Clear();
}
