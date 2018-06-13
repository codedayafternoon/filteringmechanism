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
		return FilterMode.SIMPLE;
	}
	
	public void SetDefaultValue(String value) {
		this.defaultValue = value;
		if(this.selectedValue == null)
			this.selectedValue = this.defaultValue;
	}
	
	public void SetText(String value) {
		this.selectedValue = value;
		super.notifier.NotifyFilterStateChanged(this);
	}

	@Override
	public String GetParameterKey(){
		return this.Name;
	}

	@Override
	public boolean IsReset(){
		if(this.defaultValue == null || this.selectedValue == null)
			return true;
		return this.defaultValue.equals(this.selectedValue);
	}

	@Override
	public String DoGetParameterValue(){
		return this.GetState();
	}

	@Override
	protected void DoChangeState(String state) {
		this.SetText(state);
	}
	
	@Override
	public void Reset() {
		if(this.defaultValue == null || this.selectedValue == null)
			return;
		if(this.defaultValue.equals(this.selectedValue))
			return;
		this.selectedValue = this.defaultValue;
		super.notifier.NotifyFilterReset(this);
	}

	@Override
	public String GetValue(int index){
		return this.selectedValue == null ? "" : this.selectedValue;
	}

	@Override
	public String GetState() {
		return this.selectedValue;
	}
	
}
