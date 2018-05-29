package domain.hub.results;

import domain.filters.Filter;
import domain.hub.HubCommand;

import java.util.List;

public interface IResult {
    List<Filter> GetFilters();
    Object GetResults();
    List<HubCommand> GetHubCommands();
}
