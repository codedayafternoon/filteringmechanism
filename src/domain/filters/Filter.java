package domain.filters;

import domain.filtercontroller.FilterContainer;

public abstract class Filter {

	public Object Id;
	private String Name;
	protected INotifier notifier;
	protected FilterContainer _container;
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
		String old = this.Name;
		this.Name = name;
		this.notifier.NotifyPropertyChanged(this, old, name, FilterPropertyType.Name);
	}

	public void TriggerStateChanged(){
		this.notifier.NotifyFilterStateChanged(this);
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
