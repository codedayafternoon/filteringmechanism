package domain.filters;

public interface INotifier {
	void NotifyFilterStateChanged(Filter filter);
	void NotifyFilterReset(Filter filter);
	void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType);
}
