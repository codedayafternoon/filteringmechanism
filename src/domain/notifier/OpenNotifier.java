package domain.notifier;

import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;

public class OpenNotifier implements INotifier {

    private final INotifier notifier;

    public OpenNotifier(INotifier notifier) {
        this.notifier = notifier;
    }

    public INotifier getNotifier() {
        return notifier;
    }

    @Override
    public void NotifyFilterStateChanged(Filter filter) {

    }

    @Override
    public void NotifyFilterReset(Filter filter) {

    }

    @Override
    public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

    }

    @Override
    public void NotifyFilterUpdated(Filter filter) {

    }

    @Override
    public NotifierChannelType GetType() {
        return this.notifier.GetType();
    }
}
