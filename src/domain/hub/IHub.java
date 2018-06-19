package domain.hub;

import domain.filtercontroller.FilterController;
import domain.hub.interconnections.FilterInterconnection;
import domain.hub.results.IResult;
import domain.notifier.IFilterHub;
import domain.notifier.IParameterHub;
import domain.notifier.IRequestHub;

import java.util.List;

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

	void ClearAll();
}
