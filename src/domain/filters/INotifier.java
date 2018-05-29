package domain.filters;

public interface INotifier {
	void NotifyFilterStateChanged(Filter filter);
	void NotifyFilterReset(Filter filter);
}
