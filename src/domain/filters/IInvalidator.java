package domain.filters;

import domain.filtercontroller.IInvalidatable;

public interface IInvalidator {
	void InvalidateAll(IInvalidatable except);
}
