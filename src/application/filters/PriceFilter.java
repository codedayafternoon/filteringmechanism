package application.filters;

import domain.filters.INotifier;
import domain.filters.types.RangeFilter;

import java.util.List;

public class PriceFilter extends RangeFilter {
	public PriceFilter(Object id, String name, INotifier notifier, List<String> from, List<String> to) {
		super(id, name, notifier, from, to);
	}

	@Override
	public String GetParameterKeyFrom(){
		return this.Name + "Min";
	}

	@Override
	public String GetParameterKeyTo() {
		return super.Name + "Max";
	}

	@Override
	protected String EncodeParameterValueFrom(String from){
		return from;
	}

	@Override
	protected String EncodeParameterValueTo(String to){
		return to;
	}
}
