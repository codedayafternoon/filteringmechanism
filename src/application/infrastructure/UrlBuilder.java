package application.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder implements IUrlBuilder {
	
	Map<String, String> _params;
	String _parameterValueDelimiter;
	String _parameterDelimiter;
	
	public UrlBuilder(String parameterValueDelimiter, String parameterDelimiter) {
		this._params = new HashMap<String, String>();
		this._parameterValueDelimiter = parameterValueDelimiter;
		this._parameterDelimiter = parameterDelimiter;
	}
	
	@Override
	public void AddParameter(String name, String value) {
		if(this._params.containsKey(name)) {
			if(this._params.get(name).contains(value))
				return;
			this._params.put(name, this._params.get(name) + this._parameterValueDelimiter + value);
		}else {
			this._params.put(name, value);
		}
	}
	
	private String constructUrl() {
		String res = "";
		for(Entry<String, String> entry : this._params.entrySet()) {
			res += entry.getKey() + "=" + entry.getValue();
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
		
		String value = this._params.get(name);
		if(!value.contains(this._parameterValueDelimiter)) {
			this._params.remove(name);
		}else {
			String pat = this._parameterValueDelimiter + paramValue + this._parameterValueDelimiter;
			if(value.contains(pat)) {
				value = value.replace(this._parameterValueDelimiter + paramValue, "");
			}else if(value.contains(this._parameterValueDelimiter + paramValue)) {
				value = value.replace(this._parameterValueDelimiter + paramValue, "");
			}else if(value.contains(paramValue + this._parameterValueDelimiter)) {
				value = value.replace(paramValue + this._parameterValueDelimiter, "");
			}
		}
		this._params.put(name, value);
	}

	@Override
	public void ReplaceParameter(String name, String value) {
		this.RemoveParameter(name);
		this.AddParameter(name, value);
	}
	
}
