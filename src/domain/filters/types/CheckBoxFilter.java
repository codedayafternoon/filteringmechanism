package domain.filters.types;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;

public abstract class CheckBoxFilter extends Filter {
	
	protected boolean _isChecked;
	
	public CheckBoxFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
		this._isChecked = false;
	}
	
	public void Check() {
		if(this._isChecked)
			return;
		this._isChecked = true;
		super.notifier.NotifyFilterStateChanged(this);
	}
	
	public void UnCheck() {
		this.Reset();
	}

	@Override
	public void Reset() {
		if(!this._isChecked)
			return;
		this._isChecked = false;
		super.notifier.NotifyFilterReset(this);
	}

	@Override
	public String GetState() {
		return "" + this._isChecked;
	}

	@Override
	protected void DoChangeState(String state) {
		if(state.equals("1")) {
			this.Check();
		}else if(state.equals("0")) {
			this.UnCheck();
		}
	}

	@Override
	public String GetParameterKey(){
		return this.GetContainer().GetName();
	}

	@Override
	public String GetParameterValue(){
		return this.Name;
	}

	@Override
	public FilterMode GetMode() {
		return FilterMode.SIMPLE;
	}

    public boolean IsChecked() {
		return this._isChecked;
    }
}
