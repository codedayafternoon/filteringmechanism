package domain.filters;

import domain.filters.types.SingleTextFilter;

public interface INotifier {
	void NotifyFilterStateChanged(Filter filter);
	void NotifyFilterReset(Filter filter);
	void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType);
    void NotifyFilterUpdated(Filter filter);
}
