package domain.filters.types;

import java.util.List;

import domain.filters.Filter;
import domain.filters.FilterMode;
import domain.filters.INotifier;

public abstract class RangeFilter extends Filter {

	private String SelectedFrom;
	private String SelectedTo;

	private String defaultFrom;
	private String defaultTo;

	private List<String> FromValues;
	private List<String> ToValues;

	public RangeFilter(Object id, String name, INotifier notifier) {
		super(id, name, notifier);
	}

	@Override
	public void Reset() {
		this.SelectedFrom = this.defaultFrom;
		this.SelectedTo = this.defaultTo;
		super.notifier.NotifyFilterReset(this);// NotifyStateChanged(this, true);
	}

	public void AddFromValues(List<String> from) {
		this.FromValues = from;
	}

	public void AddToValues(List<String> to) {
		this.ToValues = to;
	}

	public void SetDefaultFrom(String from) {
		if (!this.FromValues.contains(from))
			return;
		this.defaultFrom = from;
		if (this.SelectedFrom == null)
			this.SelectedFrom = this.defaultFrom;
	}

	public void SetDefaultTo(String to) {
		if (!this.ToValues.contains(to))
			return;
		this.defaultTo = to;
		if (this.SelectedTo == null)
			this.SelectedTo = this.defaultTo;
	}

	@Override
	public String GetState() {
		return "from:" + this.SelectedFrom + "-" + "to:" + this.SelectedTo;
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
		boolean changed = false;
		for (String part : parts) {
			if (part.contains("to:")) {
				String to = part.split("to:")[1];
				if (this.ToValues.contains(to)) {
					if(!this.SelectedTo.equals(to)) {
						this.SelectedTo = to;
						changed = true;
					}
				}
			} else if (part.contains("from:")) {
				String from = part.split("from:")[1];
				if (this.FromValues.contains(from)) {
					if(!this.SelectedFrom.equals(from)) {
						this.SelectedFrom = from;
						changed = true;
					}
				}
			}
		}

		if (changed)
			super.notifier.NotifyFilterStateChanged(this);// NotifyStateChanged(this, false);

	}

	@Override
	public FilterMode GetMode() {
		return FilterMode.RANGED;
	}

}
