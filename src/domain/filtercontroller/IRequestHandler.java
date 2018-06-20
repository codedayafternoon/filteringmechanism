package domain.filtercontroller;

public interface IRequestHandler {
	void makeRequest(String request);

	boolean IsRetrieveFromRequest();
	boolean IsRetrieveFromParameters();
	boolean IsRetrieveFromFilters();
}
