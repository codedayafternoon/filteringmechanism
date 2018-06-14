package domain.channelmanipulation;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;

public class OpenFilterHubListener implements IFilterHubListener {

    private IFilterHubListener listener;

    public OpenFilterHubListener(IFilterHubListener listener) {
        this.listener = listener;
    }

    public IFilterHubListener getListener() {
        return listener;
    }

    @Override
    public void FilterChanged(Filter filter) {

    }

    @Override
    public void FilterReset(Filter filter) {

    }

    @Override
    public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

    }

    @Override
    public void FilterUpdated(Filter filter) {

    }
}
