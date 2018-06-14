package domain.channelmanipulation;

import domain.filtercontroller.IRequestHandler;

public class OpenRequestHandler implements IRequestHandler {

    private final IRequestHandler requestHandler;

    public OpenRequestHandler(IRequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public IRequestHandler getRequestHandler() {
        return requestHandler;
    }

    @Override
    public void makeRequest(String request) {

    }

    @Override
    public void Initialize(String request) {

    }

    @Override
    public boolean IsRetrieveFromRequest() {
        return this.requestHandler.IsRetrieveFromRequest();
    }

    @Override
    public boolean IsRetrieveFromParameters() {
        return this.requestHandler.IsRetrieveFromParameters();
    }

    @Override
    public boolean IsRetrieveFromFilters() {
        return this.requestHandler.IsRetrieveFromFilters();
    }
}
