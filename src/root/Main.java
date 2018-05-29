package root;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import application.Container;
import application.Controller;
import application.SimpleRequestHandler;
import application.components.FilterPreviewComponent;
import application.components.UrlManagerComponent;
import application.filters.AreaDistanceFilter;
import application.filters.AreaFilter;
import application.filters.DistanceFilter;
import application.filters.ManufacturerFilter;
import application.filters.PageFilter;
import application.filters.PriceFilter;
import application.filters.ScreenSizeFilter;
import application.filters.SortingFilter;
import application.infrastructure.IUrlBuilder;
import application.infrastructure.SimpleConsolePrinter;
import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IRequestHandler;
import domain.filtercontroller.IRequestConverter;
import domain.hub.Hub;
import domain.notifier.FilterNotifier;
import domain.notifier.ParameterNotifier;

public class Main {

	public static void main(String[] args) {
		System.out.println("System started");
		TestWithConsoleInput();
	}

	private static void TestWithConsoleInput() {
		System.out.println("TestWithConsoleInput");

		List<FilterContainer> groups = new ArrayList<>();
		Hub hub = new Hub();

		IRequestHandler receiver = new SimpleRequestHandler(hub);
		ParameterNotifier parameterNotifier = new ParameterNotifier(hub);
		FilterNotifier filterNotifier = new FilterNotifier(hub);

		Container group = new Container("manufacturer");
		group.AddFilter(new ManufacturerFilter(1, "samsung", filterNotifier));
		group.AddFilter(new ManufacturerFilter(2, "apple", filterNotifier));
		group.AddFilter(new ManufacturerFilter(3, "xiaomi", filterNotifier));
		group.AddFilter(new ManufacturerFilter(4, "sony", filterNotifier));

		Container group2 = new Container("screen");
		group2.AddFilter(new ScreenSizeFilter(group2, 5, "small", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 6, "medium", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 7, "large", filterNotifier));

		Container group3 = new Container("price");
		PriceFilter priceFilter = new PriceFilter(9, "price", filterNotifier);
		List<String> toValues = new ArrayList<String>();
		toValues.add("50");
		toValues.add("100");
		toValues.add("150");
		toValues.add("200");
		toValues.add("300");

		List<String> fromValues = new ArrayList<String>();
		fromValues.add("10");
		fromValues.add("50");
		fromValues.add("100");
		fromValues.add("150");
		fromValues.add("200");
		fromValues.add("300");
		priceFilter.AddToValues(toValues);
		priceFilter.AddFromValues(fromValues);

		priceFilter.SetDefaultFrom("100");
		priceFilter.SetDefaultTo("100");
		group3.AddFilter(priceFilter);

		Container customContainer = new Container("params");
		List<String> sortValues = new ArrayList<String>();
		sortValues.add("asc");
		sortValues.add("desc");
		SortingFilter sorting = new SortingFilter(10, "sorting", parameterNotifier, sortValues);
		sorting.SetDefaultValue("asc");
		
		
		List<String> pageValues = new ArrayList<String>();
		pageValues.add("1");
		pageValues.add("2");
		pageValues.add("3");
		PageFilter paging = new PageFilter(11, "page", parameterNotifier, pageValues);
		paging.SetDefaultValue("1");
		customContainer.AddFilter(paging);
		customContainer.AddFilter(sorting);
		
		groups.add(group);
		groups.add(group2);
		groups.add(group3);
		groups.add(customContainer);
		
		AreaDistanceFilter areaDistance = new AreaDistanceFilter(14, "areadistance", filterNotifier);
		List<String> distanceValues = new ArrayList<String>();
		distanceValues.add("10");
		distanceValues.add("20");
		distanceValues.add("50");
		distanceValues.add("100");
		distanceValues.add("200");
		distanceValues.add("500");
		DistanceFilter distance = new DistanceFilter(12, "distance", areaDistance, distanceValues);
		distance.SetDefaultValue("50");
		
		AreaFilter area = new AreaFilter(13, "area", areaDistance);
		area.SetDefaultValue("athens");
		
		
		areaDistance.AddFilter(distance);
		areaDistance.AddFilter(area);
		
		Container complexContainer = new Container("complex");
		complexContainer.AddFilter(areaDistance);
		groups.add(complexContainer);
		

		IUrlBuilder urlBuilder = new UrlBuilder(",", "&");
		IRequestConverter requestConverter = new UrlQueryConverter(urlBuilder);
		Controller manager = new Controller(groups, hub, receiver, requestConverter);

		UrlManagerComponent urlManager = new UrlManagerComponent(new UrlBuilder("*", "%"));

		FilterPreviewComponent banner = new FilterPreviewComponent(manager);
		hub.AddFilterListener(urlManager);
		hub.AddParameterListener(urlManager);
		hub.AddFilterListener(banner);

		receiver.Initialize("");

		System.out.println("press e for exit");
		SimpleConsolePrinter printer = new SimpleConsolePrinter();
		Scanner in = new Scanner(System.in);
		
		while (true) {
			try {
				String input = in.nextLine();
				System.out.println("command: " + input);
				if (input == "e")
					break;
				if (input.equals("print")) {
					printer.Print(System.out, groups);
				} else if (input.contains("banner")) {
					String parts[] = input.split(" ");
					String containerName = parts[1];
					String filterName = parts[2];
					banner.RemoveEntry(containerName, filterName);
				} else if (input.contains("customr")) {
					String paramName = input.split(" ")[1];
					String value = input.split(" ")[2];
					//manager.RemoveCustomParameter(paramName, value);
					manager.Update();
				} else if (input.contains("custom")) {
					String paramName = input.split(" ")[1];
					String value = input.split(" ")[2];
					//manager.AddCustomParameter(paramName, value);
					manager.Update();
				} else {
					String parts[] = input.split(" ");
					String groupName = parts[0];
					String filterName = parts[1];
					String state = parts[2];
					manager.ChangeState(groupName, filterName, state);
				}
			} catch (Exception e) {
				System.out.println("error=>" + e.getMessage());
			}
		}

	}

	private static void TestWithGroupManager() {
		System.out.println("TestWithGroupManager");

		List<FilterContainer> groups = new ArrayList<>();
		Hub hub = new Hub();
		IRequestHandler receiver = new SimpleRequestHandler(hub);
		
		ParameterNotifier parameterNotifier = new ParameterNotifier(hub);
		FilterNotifier filterNotifier = new FilterNotifier(hub);

		Container group = new Container("manufacturer");
		group.AddFilter(new ManufacturerFilter(1, "samsung", filterNotifier));
		group.AddFilter(new ManufacturerFilter(2, "apple", filterNotifier));
		group.AddFilter(new ManufacturerFilter(3, "xiaomi", filterNotifier));
		group.AddFilter(new ManufacturerFilter(4, "sony", filterNotifier));

		Container group2 = new Container("screen");
		group2.AddFilter(new ScreenSizeFilter(group2, 5, "small", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 6, "medium", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 7, "large", filterNotifier));

		groups.add(group);
		groups.add(group2);

		IUrlBuilder urlBuilder = new UrlBuilder(",", "&");
		IRequestConverter requestConverter = new UrlQueryConverter(urlBuilder);
		Controller manager = new Controller(groups, hub, receiver, requestConverter);

		receiver.Initialize("");

		SimpleConsolePrinter printer = new SimpleConsolePrinter();
		printer.Print(System.out, groups);

		manager.ChangeState(group.GetName(), "samsung", "1");
		printer.Print(System.out, groups);

		manager.ChangeState(group2.GetName(), "small", "1");
		printer.Print(System.out, groups);

		manager.ChangeState(group2.GetName(), "medium", "1");
		printer.Print(System.out, groups);

		manager.ChangeState(group2.GetName(), "medium", "0");
		printer.Print(System.out, groups);

		manager.ChangeState(group2.GetName(), "large", "1");
		printer.Print(System.out, groups);

		manager.ChangeState(group2.GetName(), "small", "1");
		printer.Print(System.out, groups);

	}

	private static void Test1() {
		System.out.println("Test1");

		List<FilterContainer> groups = new ArrayList<>();
		Hub hub = new Hub();
		
		ParameterNotifier parameterNotifier = new ParameterNotifier(hub);
		FilterNotifier filterNotifier = new FilterNotifier(hub);

		Container group = new Container("kataskeuastes");
		group.AddFilter(new ManufacturerFilter(1, "samsung", filterNotifier));
		group.AddFilter(new ManufacturerFilter(2, "lg", filterNotifier));
		group.AddFilter(new ManufacturerFilter(3, "xiaomi", filterNotifier));
		group.AddFilter(new ManufacturerFilter(4, "sony", filterNotifier));

		Container group2 = new Container("othoni");
		group2.AddFilter(new ScreenSizeFilter(group2, 5, "mikri", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 6, "mesaia", filterNotifier));
		group2.AddFilter(new ScreenSizeFilter(group2, 7, "megali", filterNotifier));

		groups.add(group);
		groups.add(group2);

		SimpleConsolePrinter printer = new SimpleConsolePrinter();
		printer.Print(System.out, groups);

	}

}
