package domain.buildins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder implements IUrlBuilder {
	
	Map<String, List<String>> _params;
	String _parameterValueDelimiter;
	String _parameterDelimiter;
	
	public UrlBuilder(String parameterValueDelimiter, String parameterDelimiter) {
		this._params = new HashMap<String, List<String>>();
		this._parameterValueDelimiter = parameterValueDelimiter;
		this._parameterDelimiter = parameterDelimiter;
	}
	
	@Override
	public void AddParameter(String name, String value) {
		if(name == null || value == null || value.trim().equals(""))
			return;
		if(this._params.containsKey(name)) {
			if(this._params.get(name).contains(value))
				return;
			List<String> values = this._params.get(name);
			values.add(value);
		}else {
			List<String> values = new ArrayList<>();
			values.add(value);
			this._params.put(name, values);
		}
	}

	private String serializeList(List<String> values){
		String res = "";
		for(String v : values){
			res += v + this._parameterValueDelimiter;
		}
		return res.substring(0, res.length() - this._parameterValueDelimiter.length());
	}

	private String constructUrl() {
		String res = "";
		for(Entry<String, List<String>> entry : this._params.entrySet()) {
			res += entry.getKey() + "=" + this.serializeList( entry.getValue() );
			res += this._parameterDelimiter;
		}
		if(!res.equals(""))
			res = res.substring(0, res.length() - this._parameterDelimiter.length());
		return res;
	}
	
	@Override
	public String Build() {
		String res = this.constructUrl();
		this._params.clear();
		return res;
	}

	@Override
	public String Peek() {
		String res = this.constructUrl();
		return res;
	}

	@Override
	public void RemoveParameter(String name) {
		if(!this._params.containsKey(name))
			return;
		this._params.remove(name);
	}

	@Override
	public void RemoveParameter(String name, String paramValue) {
		if(!this._params.containsKey(name))
			return;
		
		List<String> values = this._params.get(name);
		if(values.contains(paramValue))
			values.remove(paramValue);

	}

	@Override
	public void ReplaceParameter(String name, String value) {
		this.RemoveParameter(name);
		this.AddParameter(name, value);
	}
	
}
