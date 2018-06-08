package domain.filters;

public abstract class BoolValueFilter extends Filter {

    public BoolValueFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }
}
