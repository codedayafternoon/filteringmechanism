package domain.filters.types;

import java.util.List;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;

public abstract class SingleTextFilter extends Filter {

	protected String selectedValue;
	protected String defaultValue;
	protected List<String> Values;

	public SingleTextFilter(Object id, String name, INotifier notifier, List<String> values) {
		super(id, name, notifier);
		this.Values = values;
	}
	
	@Override
	public FilterMode GetMode() {
		return FilterMode.SINGLE_VALUE;
	}
	
	public void SetDefaultValue(String value) {
		if(!this.Values.contains(value))
			return;
		this.defaultValue = value;
		if(this.selectedValue == null)
			this.selectedValue = this.defaultValue;
	}
	
	public void SetSelectedValue(String value) {
		if(!this.Values.contains(value))
			return;
		if(this.selectedValue.equals(value))
			return;
		this.selectedValue = value;
		super.notifier.NotifyFilterStateChanged(this);// NotifyStateChanged(this, false);
	}
	
	@Override
	protected void DoChangeState(String state) {
		this.SetSelectedValue(state);
	}
	
	@Override
	public void Reset() {
		this.SetSelectedValue(this.defaultValue);
	}

	@Override
	public String GetState() {
		return this.selectedValue;
	}

	public String GetSelectedValue() {
		return this.selectedValue;
	}
}
