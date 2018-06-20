package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import application.filters.ManufacturerFilter;
import application.filters.PriceFilter;
import application.filters.ScreenSizeFilter;
import application.model.CellPhone;
import domain.configuration.Builder;
import domain.configuration.BuilderItems;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.policies.NullValueAsSelected;
import domain.filters.policies.SelectedValuePolicyType;
import domain.hub.HubCommand;
import domain.hub.IHub;
import domain.hub.results.IResult;
import domain.notifier.FilterNotifier;
import javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanProperty;

public class SimpleRequestHandler implements IRequestHandler {

	String samsung = "samsung";
	String apple = "apple";
	String xiaomi = "xiaomi";
	String sony = "sony";

	String small = "small";
	String medium = "medium";
	String large = "large";

	private List<CellPhone> DB;
	private IHub hub;
	private Builder builder;

	public SimpleRequestHandler() {
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

	public void SetHub(IHub hub){
		this.hub = hub;
	}
	public void SetBuilder(Builder builder){
		this.builder = builder;
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

	private List<FilterContainer> GetContainersWithRandomCountsFromDb(){
		List<FilterContainer> containers = new ArrayList<>();
		FilterNotifier notifier = new FilterNotifier(this.hub);
		Random rnd = new Random();
		FilterContainer manufacturerContainer = new FilterContainer(1, "manufacturer");
		ManufacturerFilter apple = new ManufacturerFilter(1, "apple", notifier);
		apple.SetCount(rnd.nextInt(100));
		ManufacturerFilter microsoft = new ManufacturerFilter(2, "microsoft", notifier);
		microsoft.SetCount(rnd.nextInt(100));
		ManufacturerFilter nokia = new ManufacturerFilter(3, "nokia", notifier);
		nokia.SetCount(rnd.nextInt(100));
		manufacturerContainer.AddFilter(apple);
		manufacturerContainer.AddFilter(microsoft);
		manufacturerContainer.AddFilter(nokia);

		FilterContainer screenContainer = new FilterContainer(2, "screen");
		ScreenSizeFilter small = new ScreenSizeFilter(screenContainer, 1, "small", notifier);
		small.SetCount(rnd.nextInt(100));
		ScreenSizeFilter medium = new ScreenSizeFilter(screenContainer, 2, "medium", notifier);
		medium.SetCount(rnd.nextInt(100));
		ScreenSizeFilter large = new ScreenSizeFilter(screenContainer, 3, "large", notifier);
		large.SetCount(rnd.nextInt(100));
		screenContainer.AddFilter(small);
		screenContainer.AddFilter(medium);
		screenContainer.AddFilter(large);

		FilterContainer priceContainer = new FilterContainer(3, "price");
		List<String> priceFrom = new ArrayList<>();
		priceFrom.add("100");
		priceFrom.add("200");
		priceFrom.add("300");
		List<String> priceTo = new ArrayList<>();
		priceTo.add("200");
		priceTo.add("300");
		priceTo.add("400");
		PriceFilter price = new PriceFilter(1, "price", notifier, priceFrom, priceTo);
		price.SetCount(rnd.nextInt(100));
		price.SetSelectedValuePolicy(SelectedValuePolicyType.Null);
		priceContainer.AddFilter(price);

		containers.add(manufacturerContainer);
		containers.add(screenContainer);
		containers.add(priceContainer);

		return containers;

	}

	private class MockBuilderItems extends BuilderItems{

		private final List<FilterContainer> containers;

		public MockBuilderItems(List<FilterContainer> containers) {
			this.containers = containers;
		}

		@Override
		public List<FilterContainer> GetContainers() {
			return this.containers;
		}
	}

	private class MockResult implements IResult{

		private final Object result;
		private final List<Filter> filters;
		private final List<HubCommand> commands;

		public MockResult(Object result, List<Filter> filters, List<HubCommand> commands) {
			this.result = result;
			this.filters = filters;
			this.commands = commands;
		}

		@Override
		public List<Filter> GetFilters() {
			return this.filters;
		}

		@Override
		public Object GetResults() {
			return this.result;
		}

		@Override
		public List<HubCommand> GetHubCommands() {
			return this.commands;
		}
	}

	public static <E> List<E> pickNRandomElements(List<E> list, int n, Random r) {
		int length = list.size();

		if (length < n) return null;

		//We don't need to shuffle the whole list
		for (int i = length - 1; i >= length - n; --i)
		{
			Collections.swap(list, i , r.nextInt(i + 1));
		}
		return list.subList(length - n, length);
	}

	public static <E> List<E> pickNRandomElements(List<E> list, int n) {
		return pickNRandomElements(list, n, ThreadLocalRandom.current());
	}

	private void doMakeRequest(String request, boolean updateStates) {
		System.out.println("changeHappended MAKING HTTP REQUEST... => " + request);

		// this is a place to get a json responce and convert it to BuilderItems
		MockBuilderItems items = new MockBuilderItems(this.GetContainersWithRandomCountsFromDb());
		this.builder.Build(items);

		MockResult result = new MockResult(pickNRandomElements(this.DB, new Random().nextInt(10), new Random()), null, new ArrayList<>());
		this.hub.ResultReceived(result);

//		System.out.println("changeHappended MAKING HTTP REQUEST... => " + request);
//
//		String[] params = request.split("&");
//		List<CellPhone> manufacturerList = new ArrayList<CellPhone>();
//		List<CellPhone> priceList = new ArrayList<CellPhone>();
//		List<CellPhone> screenList = new ArrayList<CellPhone>();
//
//		for (String param : params) {
//			String[] paramParts = param.split("=");
//			if (paramParts.length != 2)
//				continue;
//			String paramName = paramParts[0];
//			String paramValue = paramParts[1];
//
//			if (paramName.equals("manufacturer")) {
//				String[] values = paramValue.split(",");
//				for (String v : values) {
//					manufacturerList.addAll(
//							this.DB.stream().filter(x -> x.Manufacturer.contains(v)).collect(Collectors.toList()));
//				}
//				manufacturerList = manufacturerList.stream().distinct().collect(Collectors.toList());
//			} else if (paramName.equals("price")) {
//				int from = Integer.parseInt(paramValue.split("-")[0]);
//				int to = Integer.parseInt(paramValue.split("-")[1]);
//				priceList = this.DB.stream().filter(x -> x.Price >= from && x.Price <= to).collect(Collectors.toList());
//			} else if (paramName.equals("screen")) {
//				screenList = this.DB.stream().filter(x -> x.Screen.contains(paramValue)).collect(Collectors.toList());
//			}
//
//		}
//
//		List<CellPhone> filtered = new ArrayList<CellPhone>();
//
//		if (!manufacturerList.isEmpty() && !screenList.isEmpty()) {
//			filtered = this.intersection(manufacturerList, screenList);
//		} else if (!manufacturerList.isEmpty()) {
//			filtered = manufacturerList;
//		} else if (!screenList.isEmpty()) {
//			filtered = screenList;
//		} else if (manufacturerList.isEmpty() && screenList.isEmpty()) {
//			filtered = this.DB;
//		}
//
//		if (!priceList.isEmpty())
//			filtered = this.intersection(filtered, priceList);
//
//		/// count
//		int xiaomi = 0;
//		int samsung = 0;
//		int apple = 0;
//		int sony = 0;
//
//		int small = 0;
//		int large = 0;
//		int medium = 0;
//
//		for (CellPhone c : filtered) {
//			if (c.Manufacturer.equals("xiaomi"))
//				xiaomi++;
//			else if (c.Manufacturer.equals("samsung"))
//				samsung++;
//			else if (c.Manufacturer.equals("apple"))
//				apple++;
//			else if (c.Manufacturer.equals("sony"))
//				sony++;
//
//			if (c.Screen.contains("small"))
//				small++;
//			if (c.Screen.contains("medium"))
//				medium++;
//			if (c.Screen.contains("large"))
//				large++;
//		}
//
//		HubCommand manufacturer_samsung = new HubCommand(1, "manufacturer",1, this.samsung, samsung, "0");
//		HubCommand manufacturer_apple = new HubCommand(2, "manufacturer",2, this.apple, apple, "0");
//		HubCommand manufacturer_xiaomi = new HubCommand(3, "manufacturer",3, this.xiaomi, xiaomi, "0");
//		HubCommand manufacturer_sony = new HubCommand(4, "manufacturer",4, this.sony, sony, "0");
//
//		HubCommand screen_small = new HubCommand(5, "screen", 5,this.small, small, "0");
//		HubCommand screen_large = new HubCommand(6, "screen",6, this.large, large, "0");
//		HubCommand screen_medium = new HubCommand(7, "screen",7, this.medium, medium, "0");
//
//		HubCommand price = new HubCommand(8, "price",8, "price", priceList.size(), "");
//
//		List<HubCommand> hubCommands = new ArrayList<HubCommand>();
//		hubCommands.add(manufacturer_samsung);
//		hubCommands.add(manufacturer_apple);
//		hubCommands.add(manufacturer_xiaomi);
//		hubCommands.add(manufacturer_sony);
//		hubCommands.add(screen_small);
//		hubCommands.add(screen_large);
//		hubCommands.add(screen_medium);
//		hubCommands.add(price);
//		this.hub.Execute(hubCommands);
//		if(updateStates)
//			this.hub.Execute(hubCommands);
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

}
