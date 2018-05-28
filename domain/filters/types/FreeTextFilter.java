package domain.filters.types;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;

public abstract class FreeTextFilter extends Filter {

	protected String selectedValue;
	protected String defaultValue;
	
	public FreeTextFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}

	@Override
	public FilterMode GetMode() {
		return FilterMode.SINGLE_VALUE;
	}
	
	public void SetDefaultValue(String value) {
		this.defaultValue = value;
		if(this.selectedValue == null)
			this.selectedValue = this.defaultValue;
	}
	
	private void SetText(String value) {
		this.selectedValue = value;
		super.notifier.NotifyFilterStateChanged(this);
	}
	
	@Override
	protected void DoChangeState(String state) {
		this.SetText(state);
	}
	
	@Override
	public void Reset() {
		this.SetText(this.defaultValue);
	}

	@Override
	public String GetState() {
		return this.selectedValue;
	}
	
}
