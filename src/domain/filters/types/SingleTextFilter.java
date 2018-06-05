package domain.filters.types;

import java.util.List;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;
import domain.filters.structures.RangePart;

public abstract class SingleTextFilter extends Filter {

//	protected String selectedValue;
//	protected String defaultValue;
//	protected List<String> Values;

	protected RangePart range;

	public SingleTextFilter(Object id, String name, INotifier notifier, List<String> values) {
		super(id, name, notifier);
		if(values == null || values.size() == 0)
			throw new Error("values cannot be null or empty");

		this.range = new RangePart(values, values.get(0));
		//this.Values = values;
	}

	public RangePart getRange() {
		return range;
	}

	@Override
	public FilterMode GetMode() {
		return FilterMode.SIMPLE;
	}
	
	public void SetDefaultValue(String value) {
		if(!this.range.getItems().contains(value))
			return;
		this.range.setDefaultValue(value);
		if(this.range.getSelectedValue() == null)
			this.range.setSelectedValue(this.range.getDefaultValue() );
	}
	
	public void SetSelectedValue(String value) {
		if(!this.range.getItems().contains(value))
			return;
		if(this.range.getSelectedValue().equals(value))
			return;
		this.range.setSelectedValue( value );
		if(this.range.getSelectedValue().equals(this.range.getDefaultValue()))
			super.notifier.NotifyFilterReset(this);
		else
			super.notifier.NotifyFilterStateChanged(this); // NotifyStateChanged(this, false);
	}

	@Override
	public boolean DoUpdateFrom(Filter filter){
		SingleTextFilter stf = (SingleTextFilter)filter;
		if(stf == null)
			return false;

		return this.range.UpdateFrom(stf.getRange());



//		if(stf.Values != null){
//			int selectedValueIndex = this.Values.indexOf(this.selectedValue);
//			this.Values.clear();
//			for(String x : stf.Values){
//				this.Values.add(x);
//			}
//			this.selectedValue = this.Values.get(selectedValueIndex);
//		}
//		if(stf.defaultValue != null)
//		{
//			this.SetDefaultValue(stf.defaultValue);
//		}

		//return true;
	}

	@Override
	protected void DoChangeState(String state) {
		this.SetSelectedValue(state);
	}

	@Override
	public String GetParameterKey(){
		return this.Name;
	}

	@Override
	public String GetParameterValue(){
		return this.GetState();
	}

	@Override
	public void Reset() {
		this.SetSelectedValue(this.range.getDefaultValue());
	}

	@Override
	public String GetState() {
		return this.range.getSelectedValue();
	}

	public String GetSelectedValue() {
		return this.range.getSelectedValue();
	}
}
