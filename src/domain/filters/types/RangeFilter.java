package domain.filters.types;

import java.util.List;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;
import domain.filters.policies.DefaultValueAsSelected;
import domain.filters.policies.NullValueAsSelected;
import domain.filters.policies.SelectedValuePolicyType;
import domain.filters.structures.RangePart;

public abstract class RangeFilter extends Filter {

	private RangePart rangeFrom;
	private RangePart rangeTo;

	private boolean fromIsReset;
	private boolean toIsReset;

	public RangeFilter(Object id, String name, INotifier notifier, List<String> fromValues, List<String> toValues) {
		super(id, name, notifier);
		if(fromValues == null || toValues == null || fromValues.size() == 0 || toValues.size() == 0)
			throw new Error("range values cannot be null or empty");

		this.rangeFrom = new RangePart(new DefaultValueAsSelected(), fromValues, null);
		this.rangeTo = new RangePart(new DefaultValueAsSelected(), toValues, null);
	}

	public void SetSelectedValuePolicy(SelectedValuePolicyType policy){
		switch (policy)
		{
			case DefaultIfNull:
				this.rangeTo.SetSelectedValuePolicy(new DefaultValueAsSelected());
				this.rangeFrom.SetSelectedValuePolicy(new DefaultValueAsSelected());
				break;
			case Null:
				this.rangeTo.SetSelectedValuePolicy(new NullValueAsSelected());
				this.rangeFrom.SetSelectedValuePolicy(new NullValueAsSelected());
				break;
		}
	}

	public RangePart getRangeFrom() {
		return rangeFrom;
	}

	public RangePart getRangeTo() {
		return rangeTo;
	}

	@Override
	public boolean IsReset(){
		return this.rangeFrom.IsReset() && this.rangeTo.IsReset();
	}

	@Override
	public void Reset() {
		this.rangeFrom.setSelectedValue(this.rangeFrom.getDefaultValue());
		this.rangeTo.setSelectedValue(this.rangeTo.getDefaultValue());
		super.notifier.NotifyFilterReset(this);// NotifyStateChanged(this, true);
	}

	public void UpdateFromValues(List<String> from) {
		this.rangeFrom.setItems( from );
	}

	public void UpdateToValues(List<String> to) {
		this.rangeTo.setItems( to );
	}

	public void SetDefaultFrom(String from) {
		if (!this.rangeFrom.getItems().contains(from))
			return;
		this.rangeFrom.setDefaultValue( from );
		if (this.rangeFrom.getSelectedValue() == null)
			this.rangeFrom.setSelectedValue(this.rangeFrom.getDefaultValue() );
	}

	public void SetDefaultTo(String to) {
		if (!this.rangeTo.getItems().contains(to))
			return;
		this.rangeTo.setDefaultValue( to );
		if (this.rangeTo.getSelectedValue() == null)
			this.rangeTo.setSelectedValue( this.rangeTo.getDefaultValue() );
	}

	@Override
	public String GetState() {
		return "from:" + this.rangeFrom.getSelectedValue() + "-" + "to:" + this.rangeTo.getSelectedValue();
	}

	@Override
	protected boolean DoUpdateFrom(Filter filter){
		RangeFilter rf = (RangeFilter)filter;
		if(rf == null)
			return false;

		boolean fromUpdated = this.rangeTo.UpdateFrom(rf.getRangeTo());
		boolean toUpdated = this.rangeFrom.UpdateFrom(rf.getRangeFrom());
		return fromUpdated || toUpdated;
	}

	@Override
	protected void DoChangeState(String state) {
		String[] parts;
		if (state.contains("-")) {
			parts = state.split("-");
		} else {
			parts = new String[1];
			parts[0] = state;
		}
		boolean fromchanged = false;
		boolean tochanged = false;
		for (String part : parts) {
			if (part.contains("to:")) {
				String to = part.split("to:")[1];
				if (this.rangeTo.getItems().contains(to)) {
					if(this.rangeTo.getSelectedValue() == null || !this.rangeTo.getSelectedValue().equals(to)) {
						this.rangeTo.setSelectedValue(to);
						this.checkToReset();
						tochanged = true;
					}
				}else if(to.equals("reset")){
					this.rangeTo.Reset();
					this.checkToReset();
					tochanged = true;
				}
			} else if (part.contains("from:")) {
				String from = part.split("from:")[1];
				if (this.rangeFrom.getItems().contains(from)) {
					if(this.rangeFrom.getSelectedValue() == null || !this.rangeFrom.getSelectedValue().equals(from)) {
						this.rangeFrom.setSelectedValue(from );
						this.checkFromReset();
						fromchanged = true;
					}
				}else if(from.equals("reset")){
					this.rangeFrom.Reset();
					this.checkFromReset();
					fromchanged = true;
				}
			}
		}

		if(this.fromIsReset && this.toIsReset)
			super.notifier.NotifyFilterReset(this);
		else if (tochanged || fromchanged)
			super.notifier.NotifyFilterStateChanged(this);

	}

	private void checkToReset() {
		if(this.rangeTo.IsReset())
			this.toIsReset = true;
		else
			this.toIsReset = false;
	}

	private void checkFromReset() {
		if(this.rangeFrom.IsReset())
			this.fromIsReset = true;
		else
			this.fromIsReset = false;
	}

	@Override
	public String GetParameterKey(){
		return this.Name;
	}


	@Override
	public String GetValue(int index){
		if(index == 0){
			return this.rangeFrom.getSelectedValue();
		}else if(index == 1){
			return this.rangeTo.getSelectedValue();
		}

		return "";
	}

	public String GetParameterKeyFrom(){
		return this.Name + "From";
	}

	public String GetParameterValueFrom(){
		if(this.rangeFrom.getSelectedValue() == null)
			return null;
		String[]parts = this.GetState().split("-");
		String from = parts[0].split(":")[1];
		return super.formatValue(this.EncodeParameterValueFrom(from));
	}

	public String GetParameterValueTo(){
		if(this.rangeTo.getSelectedValue() == null)
			return null;
		String[]parts = this.GetState().split("-");
		String to = parts[1].split(":")[1];

		return super.formatValue(this.EncodeParameterValueTo(to));
	}

	protected String EncodeParameterValueFrom(String from){
		return "from:" + from;
	}

	public String GetParameterKeyTo() {
		return this.Name + "To";
	}

	protected String EncodeParameterValueTo(String to){
		return "to:" + to;
	}

	@Override
	public FilterMode GetMode() {
		return FilterMode.RANGED;
	}

	public void SetFrom(String from) {
		this.DoChangeState("from:"+from);
	}

	public void SetTo(String to){
		this.DoChangeState("to:" + to);
	}

	public boolean IsToReset() {
		return this.toIsReset;
	}

	public boolean IsFromReset() {
		return this.fromIsReset;
	}
}
