package domain.filtercontroller;

import java.util.Date;
import java.util.Map;

import domain.filters.Filter;

public interface IRequestConverter {
	String Convert(Map<Filter, Date> items);

	void AddCustomParameter(String paramName, String value);
	void RemoveCustomParameter(String paramName, String value);
	void RemoveCustomParameter(String paramName);
}
