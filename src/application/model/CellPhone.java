package application.model;

import java.util.List;

public class CellPhone {
	public String Manufacturer;
	public List<String> Screen;
	public String Name;
	public double Price;
	
	public CellPhone(String manufacturer, List<String> screen, String name, double price) {
		this.Manufacturer = manufacturer;
		this.Screen = screen;
		this.Name = name;
		this.Price = price;
	}
	
}
