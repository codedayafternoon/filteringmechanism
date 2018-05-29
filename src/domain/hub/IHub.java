package domain.hub;

import domain.filtercontroller.FilterController;
import domain.hub.results.IResult;

public interface IHub {
	void SetFilterController(FilterController controller);
	void ResultReceived(IResult result);

	void AddResultListener(IResultHubListener listener);
	void RemoveResultListener(IResultHubListener listener);
	void ClearResultListeners();
}
