package domain.filters.types;

import domain.filtercontroller.IInvalidatable;
import domain.filters.IInvalidator;
import domain.filters.INotifier;

public abstract class SingleSelectFilter extends CheckBoxFilter implements IInvalidatable {

	private IInvalidator _invalidator;
	
	public SingleSelectFilter(IInvalidator invalidator, Object id, String name, INotifier notifier){
		super(id, name, notifier);
		this._invalidator = invalidator;
	}


	@Override
	public void Check() {
		if(this._isChecked)
			return;
		this._isChecked = true;
		super.notifier.NotifyFilterStateChanged(this);// NotifyStateChanged(this, false);
		this._invalidator.InvalidateAll(this);
	}
	
}
