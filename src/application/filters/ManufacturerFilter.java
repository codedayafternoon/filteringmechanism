package application.filters;

import domain.filters.ICountable;
import domain.filters.INotifier;
import domain.filters.types.CheckBoxFilter;

public class ManufacturerFilter extends CheckBoxFilter implements ICountable {

	private int count;
	
	public ManufacturerFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}

	@Override
	public void SetCount(int count) {
		this.count = count;
	}

	@Override
	public int GetCount() {
		return this.count;
	}

}
