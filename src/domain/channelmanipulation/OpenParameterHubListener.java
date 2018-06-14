package domain.channelmanipulation;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IParameterHubListener;

public class OpenParameterHubListener implements IParameterHubListener {
    private final IParameterHubListener listener;

    public OpenParameterHubListener(IParameterHubListener listener) {
        this.listener = listener;
    }

    public IParameterHubListener getListener() {
        return listener;
    }

    @Override
    public void ParameterChanged(Filter filter) {

    }

    @Override
    public void ParameterReset(Filter filter) {

    }

    @Override
    public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

    }

    @Override
    public void ParameterUpdated(Filter filter) {

    }
}
