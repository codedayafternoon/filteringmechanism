package domain.filters;

import domain.filtercontroller.FilterContainer;
import domain.filters.formatters.DefaultValuePostFormatter;
import domain.notifier.NotifierChannelType;

public abstract class Filter implements ICountable {

	public Object Id;
	protected String Name;
	protected INotifier notifier;
	protected FilterContainer _container;
	private int count;
	protected IValuePostFormatter postValueFormatter;

	public Filter(Object id, String name, INotifier notifier) {
		if(id == null || name == null)
			throw new Error("id or name of a filter cannot be null");
		if(notifier == null)
			throw new Error("the notifier of a filter cannot be null");
		this.Id = id;
		this.notifier = notifier;
		this.Name = name;
		this.count = -1;
	}

	public NotifierChannelType GetNotifierType(){
		return this.notifier.GetType();
	}

	public String getName() {
		return Name;
	}

	public IValuePostFormatter GetPostFormatter(){
		if(this.postValueFormatter == null)
			this.postValueFormatter = new DefaultValuePostFormatter();
		return this.postValueFormatter;
	}

	public void SetValuePostFormatter(IValuePostFormatter formatter){
		this.postValueFormatter = formatter;
	}

	public void setName(String name) {
		if(name == null || this.Name.equals(name))
			return;
		String old = this.Name;
		this.Name = name;
		this.notifier.NotifyPropertyChanged(this, old, name, FilterPropertyType.Name);
	}

	public abstract FilterMode GetMode();
	public abstract String GetParameterKey();

	public final String GetParameterValue(){
		String res = this.DoGetParameterValue();
		for(String number : this.GetPostFormatter().Extract(res)) {
			String formattedNumber = this.GetPostFormatter().Format(number);
			if(!number.equals(formattedNumber))
				res = res.replace(number, formattedNumber);
		}
		return res;
	}

	protected abstract String DoGetParameterValue();

	@Override
	public void SetCount(int count){
		if(count < 0 || this.count == count)
			return;
		int old = this.count;
		this.count = count;
		this.notifier.NotifyPropertyChanged(this, Integer.toString(old), Integer.toString( this.count ), FilterPropertyType.Count);
	}

	@Override
	public int GetCount(){
		return this.count;
	}

	public void SetContainer(FilterContainer container) {
		this._container = container;
	}
	
	public FilterContainer GetContainer() {
		return this._container;
	}

	public abstract void Reset();
	public abstract String GetState();
	
	public void ChangeState(String state) {
		if(state.equals("reset"))
			this.Reset();
		else 
			this.DoChangeState(state);
	}

	protected abstract void DoChangeState(String state);

	public void UpdateFrom(Filter filter){
		if(filter == null)
			return;
		this.setName(filter.getName());
		if(this.DoUpdateFrom(filter))
			this.notifier.NotifyFilterUpdated(this);
	}

	protected boolean DoUpdateFrom(Filter filter){return false;}

	@Override
	public String toString() {
		return this.Name + "[" + this.Id + "]:"+this.GetState();
	}
}
