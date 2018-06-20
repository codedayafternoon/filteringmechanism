package domain.hub;

import domain.filtercontroller.FilterController;
import domain.filters.Filter;
import domain.hub.interconnections.FilterInterconnection;
import domain.hub.results.IResult;
import domain.notifier.IFilterHub;
import domain.notifier.IParameterHub;
import domain.notifier.IRequestHub;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IHub extends IParameterHub, IFilterHub, IRequestHub {
	void ResultReceived(IResult result);

	void AddResultListener(IResultHubListener listener);
	void RemoveResultListener(IResultHubListener listener);
	void ClearResultListeners();

	void AddInterconnection(FilterInterconnection interconnection);
	void RemoveInterconnection(FilterInterconnection interconnection);
	boolean HasInterconnection(FilterInterconnection interconnection);

	void Execute(HubCommand command);
	void Execute(List<HubCommand> commands);

	Map<Filter, Date> GetFilters();
	Map<Filter, Date> GetParameters();
	Map<Filter, Date> GetRequests();

	void ClearAll();
}
