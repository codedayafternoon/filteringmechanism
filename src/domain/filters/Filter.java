package domain.filters;

import domain.filtercontroller.FilterContainer;

public abstract class Filter implements ICountable {

	public Object Id;
	private String Name;
	protected INotifier notifier;
	protected FilterContainer _container;
	private int count;

	public abstract FilterMode GetMode();
	
	public Filter(Object id, String name, INotifier notifier) {
		this.Id = id;
		this.notifier = notifier;
		this.Name = name;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		if(this.Name.equals(name))
			return;
		String old = this.Name;
		this.Name = name;
		this.notifier.NotifyPropertyChanged(this, old, name, FilterPropertyType.Name);
	}

	@Override
	public void SetCount(int count){
		if(this.count == count)
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
	
	@Override
	public String toString() {
		return this.Name + "[" + this.Id + "]:"+this.GetState();
	}
}
