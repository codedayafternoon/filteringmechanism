package application.infrastructure;

public interface IUrlBuilder {
	void AddParameter(String name, String value);
	void RemoveParameter(String name, String value);
	void RemoveParameter(String name);
	String Build();
	String Peek();
	void ReplaceParameter(String name, String getState);
}
