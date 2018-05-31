package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.model.CellPhone;
import domain.filtercontroller.IRequestHandler;
import domain.hub.Hub;
import domain.hub.HubCommand;

public class SimpleRequestHandler implements IRequestHandler {

	private final String json = "[\r\n" + "  {\r\n" + "    \"manufacturer\": \"samsung\",\r\n"
			+ "    \"name\": \"cell1\",\r\n" + "    \"screen\": \"small\"\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"manufacturer\": \"xiaomi\",\r\n" + "    \"name\": \"cell2\",\r\n" + "    \"screen\": \"small\"\r\n"
			+ "  },\r\n" + "  {\r\n" + "    \"manufacturer\": \"samsung\",\r\n" + "    \"name\": \"cell3\",\r\n"
			+ "    \"screen\": [\r\n" + "      \"small\",\"medium\", \"large\"\r\n" + "    ]\r\n" + "  },\r\n"
			+ "  {\r\n" + "    \"manufacturer\": \"samsung\",\r\n" + "    \"name\": \"cell4\",\r\n"
			+ "    \"screen\": [\r\n" + "      \"small\",\"medium\", \"large\"\r\n" + "    ]\r\n" + "  },\r\n"
			+ "  {\r\n" + "    \"manufacturer\": \"apple\",\r\n" + "    \"name\": \"cell5\",\r\n"
			+ "    \"screen\": \"small\"\r\n" + "  },\r\n" + "  {\r\n" + "    \"manufacturer\": \"xiaomi\",\r\n"
			+ "    \"name\": \"cell6\",\r\n" + "   \"screen\": [\r\n" + "      \"small\",\"medium\", \"large\"\r\n"
			+ "    ]\r\n" + "  },\r\n" + "  {\r\n" + "    \"manufacturer\": \"samsung\",\r\n"
			+ "    \"name\": \"cell7\",\r\n" + "    \"screen\": \"small\"\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"manufacturer\": \"samsung\",\r\n" + "    \"name\": \"cell8\",\r\n" + "   \"screen\": [\r\n"
			+ "      \"small\", \"large\"\r\n" + "    ]\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"manufacturer\": \"apple\",\r\n" + "    \"name\": \"cell9\",\r\n" + "    \"screen\": [\r\n"
			+ "      \"small\",\"medium\", \"large\"\r\n" + "    ]\r\n" + "  },\r\n" + "  {\r\n"
			+ "    \"manufacturer\": \"apple\",\r\n" + "    \"name\": \"cell10\",\r\n" + "    \"screen\": [\r\n"
			+ "      \"medium\", \"large\"\r\n" + "    ]\r\n" + "  }\r\n" + "]";

	String samsung = "samsung";
	String apple = "apple";
	String xiaomi = "xiaomi";
	String sony = "sony";

	String small = "small";
	String medium = "medium";
	String large = "large";
	private List<CellPhone> DB;
	private Hub _hub;

	public SimpleRequestHandler(Hub hub) {
		this._hub = hub;
		this.DB = new ArrayList<CellPhone>();

		List<String> small_medium = new ArrayList<String>();
		small_medium.add(small);
		small_medium.add(medium);
		List<String> small_large = new ArrayList<String>();
		small_large.add(small);
		small_large.add(large);
		List<String> medium_large = new ArrayList<String>();
		medium_large.add(medium);
		medium_large.add(large);
		List<String> only_medium = new ArrayList<String>();
		only_medium.add(medium);

		CellPhone cell1 = new CellPhone(xiaomi, small_large, "cell1", 10);
		CellPhone cell2 = new CellPhone(samsung, only_medium, "cell2", 35.6);
		CellPhone cell3 = new CellPhone(apple, only_medium, "cell3", 23);
		CellPhone cell4 = new CellPhone(xiaomi, only_medium, "cell4", 200);
		CellPhone cell5 = new CellPhone(samsung, medium_large, "cell5", 140);
		CellPhone cell6 = new CellPhone(samsung, medium_large, "cell6", 133);
		CellPhone cell7 = new CellPhone(apple, small_medium, "cell7", 123);
		CellPhone cell8 = new CellPhone(xiaomi, only_medium, "cell8", 321);
		CellPhone cell9 = new CellPhone(apple, small_large, "cell9", 333);
		CellPhone cell10 = new CellPhone(samsung, small_large, "cell10", 200);
		CellPhone cell11 = new CellPhone(samsung, only_medium, "cell12", 45);
		CellPhone cell12 = new CellPhone(sony, small_large, "cell12", 79);
		CellPhone cell13 = new CellPhone(sony, small_large, "cell13", 69);
		CellPhone cell14 = new CellPhone(sony, only_medium, "cell14", 222);
		this.DB.add(cell1);
		this.DB.add(cell2);
		this.DB.add(cell3);
		this.DB.add(cell4);
		this.DB.add(cell5);
		this.DB.add(cell6);
		this.DB.add(cell7);
		this.DB.add(cell8);
		this.DB.add(cell9);
		this.DB.add(cell10);
		this.DB.add(cell11);
		this.DB.add(cell12);
		this.DB.add(cell13);
		this.DB.add(cell14);
	}

	public <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			if (list2.contains(t)) {
				list.add(t);
			}
		}

		return list;
	}

	@Override
	public void makeRequest(String request) {
		this.doMakeRequest(request, false);
	}

	@Override
	public void Initialize(String request) {

		this.doMakeRequest(request, true);
	}

	@Override
	public boolean IsRetrieveFromRequest() {
		return true;
	}

	@Override
	public boolean IsRetrieveFromParameters() {
		return true;
	}

	@Override
	public boolean IsRetrieveFromFilters() {
		return true;
	}

	private void doMakeRequest(String request, boolean updateStates) {
		System.out.println("changeHappended MAKING HTTP REQUEST... => " + request);

		String[] params = request.split("&");
		List<CellPhone> manufacturerList = new ArrayList<CellPhone>();
		List<CellPhone> priceList = new ArrayList<CellPhone>();
		List<CellPhone> screenList = new ArrayList<CellPhone>();

		for (String param : params) {
			String[] paramParts = param.split("=");
			if (paramParts.length != 2)
				continue;
			String paramName = paramParts[0];
			String paramValue = paramParts[1];

			if (paramName.equals("manufacturer")) {
				String[] values = paramValue.split(",");
				for (String v : values) {
					manufacturerList.addAll(
							this.DB.stream().filter(x -> x.Manufacturer.contains(v)).collect(Collectors.toList()));
				}
				manufacturerList = manufacturerList.stream().distinct().collect(Collectors.toList());
			} else if (paramName.equals("price")) {
				int from = Integer.parseInt(paramValue.split("-")[0]);
				int to = Integer.parseInt(paramValue.split("-")[1]);
				priceList = this.DB.stream().filter(x -> x.Price >= from && x.Price <= to).collect(Collectors.toList());
			} else if (paramName.equals("screen")) {
				screenList = this.DB.stream().filter(x -> x.Screen.contains(paramValue)).collect(Collectors.toList());
			}

		}

		List<CellPhone> filtered = new ArrayList<CellPhone>();

		if (!manufacturerList.isEmpty() && !screenList.isEmpty()) {
			filtered = this.intersection(manufacturerList, screenList);
		} else if (!manufacturerList.isEmpty()) {
			filtered = manufacturerList;
		} else if (!screenList.isEmpty()) {
			filtered = screenList;
		} else if (manufacturerList.isEmpty() && screenList.isEmpty()) {
			filtered = this.DB;
		}

		if (!priceList.isEmpty())
			filtered = this.intersection(filtered, priceList);

		/// count
		int xiaomi = 0;
		int samsung = 0;
		int apple = 0;
		int sony = 0;

		int small = 0;
		int large = 0;
		int medium = 0;

		for (CellPhone c : filtered) {
			if (c.Manufacturer.equals("xiaomi"))
				xiaomi++;
			else if (c.Manufacturer.equals("samsung"))
				samsung++;
			else if (c.Manufacturer.equals("apple"))
				apple++;
			else if (c.Manufacturer.equals("sony"))
				sony++;

			if (c.Screen.contains("small"))
				small++;
			if (c.Screen.contains("medium"))
				medium++;
			if (c.Screen.contains("large"))
				large++;
		}

		HubCommand manufacturer_samsung = new HubCommand(1, "manufacturer", this.samsung, samsung, "0");
		HubCommand manufacturer_apple = new HubCommand(2, "manufacturer", this.apple, apple, "0");
		HubCommand manufacturer_xiaomi = new HubCommand(3, "manufacturer", this.xiaomi, xiaomi, "0");
		HubCommand manufacturer_sony = new HubCommand(4, "manufacturer", this.sony, sony, "0");

		HubCommand screen_small = new HubCommand(5, "screen", this.small, small, "0");
		HubCommand screen_large = new HubCommand(6, "screen", this.large, large, "0");
		HubCommand screen_medium = new HubCommand(7, "screen", this.medium, medium, "0");

		HubCommand price = new HubCommand(8, "price", "price", priceList.size(), "");

		List<HubCommand> hubCommands = new ArrayList<HubCommand>();
		hubCommands.add(manufacturer_samsung);
		hubCommands.add(manufacturer_apple);
		hubCommands.add(manufacturer_xiaomi);
		hubCommands.add(manufacturer_sony);
		hubCommands.add(screen_small);
		hubCommands.add(screen_large);
		hubCommands.add(screen_medium);
		hubCommands.add(price);
		this._hub.Execute(hubCommands);
		if(updateStates)
			this._hub.Execute(hubCommands);
	}

}
