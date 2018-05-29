package domain.filtercontroller;

public interface IRequestHandler {
	void makeRequest(String request);
	void Initialize(String request);

	boolean IsRetrieveFromRequest();
	boolean IsRetrieveFromParameters();
	boolean IsRetrieveFromFilters();
}
