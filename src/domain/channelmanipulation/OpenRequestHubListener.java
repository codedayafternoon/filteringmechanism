package domain.channelmanipulation;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IRequestHubListener;
import domain.hub.IResultHubListener;

public class OpenRequestHubListener implements IRequestHubListener {
    private final IRequestHubListener listener;

    public OpenRequestHubListener(IRequestHubListener listener) {
        this.listener = listener;
    }

    public IRequestHubListener getListener() {
        return listener;
    }

    @Override
    public void RequestChanged(Filter filter) {

    }

    @Override
    public void RequestReset(Filter filter) {

    }

    @Override
    public void RequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

    }

    @Override
    public void RequestUpdated(Filter filter) {

    }
}
