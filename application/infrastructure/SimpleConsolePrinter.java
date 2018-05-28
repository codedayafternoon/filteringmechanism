package application.infrastructure;

import java.io.PrintStream;
import java.util.List;

import domain.filtercontroller.FilterContainer;
import org.hamcrest.core.IsInstanceOf;

import application.Container;
import domain.filters.Filter;
import domain.filters.ICountable;
import domain.filters.types.SingleSelectFilter;

public class SimpleConsolePrinter {
	private String Print(List<FilterContainer> groupFilters) {
		StringBuilder sb = new StringBuilder();
	
		for(FilterContainer g : groupFilters) {
			sb.append(this.PrintGroup(g));
			sb.append(System.getProperty("line.separator"));
		}
		
		return sb.toString();
	}

	private String PrintGroup(FilterContainer g) {
		StringBuilder sb = new StringBuilder();
		sb.append(g.GetName());
		sb.append(System.getProperty("line.separator"));
		for(Filter f : g.GetFilters()) {
			sb.append("\t");
			sb.append(this.PrintFilter(f));
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	private String PrintFilter(Filter f) {
		StringBuilder sb = new StringBuilder();
		sb.append(f.Name);
		sb.append(" [" + f.GetState() + "]");
		if(f instanceof  ICountable) {
			sb.append("(" + ((ICountable)f).GetCount() + ")");
		}
		return sb.toString();
	}
	
	public void Print(PrintStream out, List<FilterContainer> groups) {
		out.println(this.Print(groups));
	}
	
}
