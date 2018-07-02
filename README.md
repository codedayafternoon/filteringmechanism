Fireman
=============

Table of contents
=================

* [Introduction]() ok
* [The Overall Image]()
* [Filters](#gh-md-toc) ok
    * [Types](#types) ok
    * [States](#states) ok
    * [Filter Channels, FilterNotifiers]() ok
    * [Simple Filter Usage Example](#simple-filter-usage-example) ok
    * [Filter Value Policy](filter-value-policy) ok
    * [Value Formatters](value-formatters) ok
    * [FilterFormatter]() ok
    * [Filter to Request Parameter]() ok
* [Boundary and Control Objects](#boundary-and-control-objects) ok
    * [FilterContext](#filtercontext) ok
    * [IFilterController - How to Control Filters](#ifiltercontroller) ok
    * [IHub](#ihub) ok
        * [How to Register for Events from Framework](#hublisteners) ok
        * [How to Change Filter States Without Making a Request]() ok
            * [HubCommand]() ok
    * [Builder](#builder) ok
        * [BuilderItems]() ok
        * [How to Receive Creational Events]() ok
            * [IBuilderObserver]()
    * [ChannelManipulator](#channelmanipulator) ok
* [Request Instruction/Handling]() ok
* [Configuration](#configuration) ok
    * [Request Parameter Configuration](#df)
* [Filter Rules](#rules) ok
* [A Case Study - How All Fit Together]()

## Introduction


Fireman will help you build multiselection filtering support in your web, desktop or other types of applications. It decouples the client from managing the filters complexity, allowing the client to concentrate on the other aspects of the problem.

The library supports:

1. Filter state management.
2. When a filter state changes, the framework automatically instructs a request with parameters for fetching results.
3. Three channels for listening the filter state changes. For example, if you want to isolate filters from other filters, then various client-components can be notified for a subset of them.
4. Localization support. Filters content, such as name, values and other properties are easily changed through the API.
5. Control over how a filter is serialized in order to use it as a url request parameter.
6. Bookmark and landing page can be supported, by restoring filter states.
7. Apply rules such as, when a filter state changes then another filter resets.
8. Filter value formatting. For example, ability to replace all commas with dots in the arithmetic values of filters.
9. Easy formatting of filters as a user-friendly string (for display purposes).
10. Allows the client to extend various core functionalities to meet their requirements.

## Filters

The abstract `Filter` inside the framework represents any filter, such as dropdowns or checkboxes. It has many attributes and behaviors that are consistent to all the subtypes of the `Filter`.
The most important part of the `Filter` interface is depicted in the following table:

| Function | Parameters  | Return type | Description |
| ------------- | ----- | ----- | ----- |
|**GetNotifierType**| none |`NotifierChannelType`|Returns which type of notifier the filter is using {FilterNotifier, ParameterNotifier, RequestNotifier} |
|**SetValuePostFormatter**|`IValuePostFormatter formatter`|`void`|Sets the formatter that formats the value of the `Filter` |
|**GetMode**|none |`FilterMode`|Returns the mode of the `Filter` {SIMPLE, RANGED, COMPLEX}. The `Filter`'s mode depends on the number of its separated values. For example, a `RangeFilter` has two values |
|**GetParameterKey**|none |`String`|Returns a distinctive key of the `Filter`, which is used to convert the `Filter` into a parameter |
|**GetParameterValue**|none |`void`|Returns a distinctive value of the `Filter`, which is used to convert the `Filter` into a parameter |
|**Pause**|none |`void`|Pauses the `Filter`. State changes do not call the notifier and events are not propagated |
|**UnPause**|none |`void`|Unpauses the `Filter`, causing all `Filter` events to be propagated |
|**SetCount**|`int count`|`void`|Sets the count of the `Filter` |
|**GetCount**|none |`int`|Gets the count of the `Filter` |
|**GetValue**|`int index`|`String`|Gets the i-th value of the `Filter`. In case of a `RangeFilter`, which has two values, the index can be 0 or 1 |
|**GetContainer**|none |`FilterContainer`|Returns the `Filter` container |
|**Reset**|none |`void`|Resets the `Filter`, either to null or to a default value |
|**GetState**|none |`String`|Returns the `Filter`'s state |
|**ChangeState**|`String state`|`void`|Changes the `Filter`'s state |
|**UpdateFrom**|`Filter filter`|`void`|Updates the `Filter` from another `Filter` |
|**IsReset**|none |`boolean`|Returns `true` when the `Filter` is reseted |
|**GetFormattedText**|`Object formatterId`|`String`|Gets a user friendly formatted text, using the formatter by formatterId |
|**GetFormattedText**|`Object formatterId, Map<String, String> _params`|`String`|Gets a user friendly formatted text, using the formatter with formatterId. In addition, it allows the injection of other parameters, which are taken into account by the formatter |
|**AddFormatter**|`FilterFormatter formatter`|`void`|Adds a formatter to the `Filter` |

### types
The various filter types which are supported by the system are:

| Filter        | Constructor params | Description  |
| :------------- |:-------------|:-----|
| **CheckBoxFilter** | `Object id, String name, INotifier notifier` | Represents a checkbox that is either selected or not|
| **FreeTextFilter**  | `Object id, String name, INotifier notifier` | Represents a textbox that takes arbitrary values |
| **SingleTextFilter** | `Object id, String name, INotifier notifier, List<String> values` | Represents a filter that takes one value among a list of values, like a dropdown |
| **SingleSelectFilter** | `IInvalidator invalidator, Object id, String name, INotifier notifier` | It is a special case of a checkboxFilter, but acts as a radio button, which means that upon selection it can deselect/reset other singleselect filters |
| **RangeFitler** | `Object id, String name, INotifier notifier, List<String> fromValues, List<String> toValues`  |  Represents a double (from-to) dropdown filter |
| **CompositeFilter** | `Object id, String name, INotifier notifier`  | It is a filter containing a list of other filters, which can be manipulated as a group |

### Filter States
Each filter can take various states. These states have various formats depending on the type of the filter. In the following table we present how a state is set and retrieved depending on the type of the filter. The state of a filter is its current value. Sometimes this consists of two or more values (for example a `RangeFilter` has two values).

| Filter Type | State Format, `ChangeState(String state)` | How to Set the State | Returned Format, `String GetState()` |
| :---------- |:---------|:-----|:-----|
|**CheckBoxFilter**|`1` or `0`| `Check() UnCheck()` | `true` or `false` |
|**FreeTextFilter**|the text we want|`SetText(String text)`|the text we set |
|**SingleTextFilter**|one value from the list|`SetSelectedValue(String value)`|the value we set or `null` |
|**SingleSelectFilter**|`1` or `0`| `Check() UnCheck()` | `true` or `false` |
|**RangeFitler**|`from:value` or `to:value` or `from:value1-to:value2`|`SetFrom(String from)`,`SetTo(String to)` |`from:value1-to:value2` |
|**CompositeFilter**|`id_of_internal_filter:desired_state`|NONE|states of internal filters divided with `|` |

A simple usage of the above statements is displayed below:

 For a `SingleTextFilter`:
```java
List<String> singleTextValues = new ArrayList<String>();
singleTextValues.add("x");
singleTextValues.add("y");
singleTextValues.add("z");
MockSingleTextFilter singleText1 = new MockSingleTextFilter(1, "f1", this.notifier, singleTextValues);
singleText1.SetDefaultValue("q"); // there is no such value in list, so do nothing.
Assert.assertEquals("x", singleText1.GetSelectedValue());
singleText1.SetDefaultValue("x");
Assert.assertEquals("x", singleText1.GetSelectedValue());

singleText1.SetSelectedValue("y");
Assert.assertEquals("y", singleText1.GetSelectedValue());

singleText1.Reset();
Assert.assertEquals("x", singleText1.GetSelectedValue());

singleText1.ChangeState("z"); // same effect as SetSelectedValue
Assert.assertEquals("z", singleText1.GetSelectedValue());
```
For a `CheckBoxFilter`:
```java
MockCheckBoxFilter checkBox1 = new MockCheckBoxFilter(1, "c1", notifier);
MockCheckBoxFilter checkBox2 = new MockCheckBoxFilter(2, "c2", notifier);

checkBox1.Check();
Assert.assertEquals(true, checkBox1.IsChecked());
Assert.assertEquals(false, checkBox2.IsChecked());
```

### Filter Channels, Filter Notifiers
All filters must have an implementation of INotifier for their creation. An INotifier has to be one of the three predefined notifiers the framework supports. They act as channels (observers) in which the filters fire their events. Basically, a filter event is either `filterStateChanged` or a `filterReset`. A `filterReset` event is raised when the filter is getting its default value, or when the `Reset()` function is called. The three predefined notifiers are the following:
1. `FilterNotifier`
2. `ParameterNotifier`
3. `RequestNotifier`

*Their names do not represent anything specific. They could be named Channel1, Channel2, Channel3.*

*Another type of `iNotifier` is the `OpenNotifier`, but it is not supposed to be used by the client. This is a special type of notifier used internally by the system, when the user or the framework instructs the channel to pause. Then, the corresponding notifier is encapsulated by the `OpenNotifier` to open the circuit and stop the event propagation.*

For the creation of a notifier, a `Hub` is needed in its contructor. In the end all notifiers will notify the same hub, but they will fire different functions on it. The `Hub` can easily be obtained by the `FilterContext` and passed to the notifier constructor.

### Simple Filter Usage Example
All filters are abstract, which means that the client must have its own implementation classes.
```java
public void createFilters(Hub hub){
    FilterNotifier notifier = new FilterNotifier(hub); // the FilterNotifier is predefined inside the library.
    FilterContainer container = new FilterContainer(1, "c1");
    SortingFilter sortFilter = new SortingFilter(1, "sort", notifier);
    List<String> from = new ArrayList<>();
    from.add("100");
    from.add("200");
    from.add("300");
    List<String> to = new ArrayList<>();
    to.add("400");
    to.add("500");
    to.add("600");
    PriceFilter priceFilter = new PriceFilter(2,"price", this.notifier, from, to); // the priceFilter inherits from RangeFilter

    filterContainer.AddFilter(sortFilter);
    filterContainer.AddFilter(priceFilter);
}

public class SortingFilter extends SingleTextFilter {
	public SortingFilter(Object id, String name, INotifier notifier, List<String> values) {
		super(id, name, notifier, values);
	}
}
public class PriceFilter extends RangeFilter {
	public PriceFilter(Object id, String name, INotifier notifier, List<String> from, List<String> to) {
		super(id, name, notifier, from, to);
	}
}
//.. other implementations of the filters
```
By extending the abstract filters of the system you can provide new specialized functions or properties. By default all filters have the next same set of attributes:

| Attributes        | Type | Description  |
| :------------- |:-------------|:----- |
| **Id**  | `Object` | A unique id for the filter. Identifies the filter uniquely inside its container. |
| **Name**  | `String` | The name of the filter. Inside a container two filters cannot have the same name. |
| **Count** | `int` | Represents how many items result from the filter. |

### Filter Value Policy
When some filters, such as `RangeFilters` and `SingleTextFilter` (dropdown), are reset, they can take a default value from their list, or they can take a `null` value. This is defined by the `SelectedValuePolicy`, which can be one of the following types:

| Selected Value Type | Description |
| :------------- |:------------- |
|**DefaultIfNull**|When the filter is reset, the selected value falls back to the defined default value. |
|**Null**|When the filter is reset, the selected value falls back to `null`. |

This policy can come in handy depending on the situation we are in. For example, when we reset the dropdown and want its value to fall back to the default value, we use the `DefaultIfNull` policy. When we reset the filter and want its value to fall back to null, then we use the `Null` policy.
An example is shown bellow:

```java
List<String> fromValues = new ArrayList<>();
fromValues.add("100");
fromValues.add("200");
fromValues.add("300");
fromValues.add("400");
List<String> toValues = new ArrayList<>();
toValues.add("400");
toValues.add("500");
toValues.add("600");
toValues.add("700");
MockRangeFilter f1 = new MockRangeFilter(1, "f1", notifier, fromValues, toValues);

f1.SetSelectedValuePolicy(SelectedValuePolicyType.Null);
String state = f1.GetState();
Assert.assertEquals("from:null-to:null", state); // null policy

f1.ChangeState("from:200");
state = f1.GetState();
Assert.assertEquals("from:200-to:null", state);

f1.ChangeState("to:600");
state = f1.GetState();
Assert.assertEquals("from:200-to:600", state);

f1.ChangeState("to:reset");
state = f1.GetState();
Assert.assertEquals("from:200-to:null", state);
```
### Value Formatters

If the client wants the value of a filter to have a specific format (for example when sending an http request), an `IValuePostFormatter` must be provided to the `Filter`. By default the value of this variable is an in-build `DefaultValuePostFormatter` which does nothing. The client has the freedom to implement this interface and provide whatever logic the filter needs. Bellow is the `IValuePostFormatter`interface:

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :----- |
|**Extract**|`String value`|`List<String>`|This function returns all the sub strings that the formatter will format. |
|**Format**|`String value`|`String`|This function formats the current value and returns it. |

As an example we present another build-in formatter, the `NumberValuePostFormatter`, which converts all numbers of the value from comma separated to dot and vise-versa.

```java
MockFreeTextFilter f1 = new MockFreeTextFilter(1, "f1", notifier);

f1.ChangeState("77s7s,dw2,3wdq,dd");
String res = f1.GetParameterValue();
Assert.assertEquals("77s7s,dw2,3wdq,dd", res);

NumberValuePostFormatter formatter = new NumberValuePostFormatter(NumberValueFormatPolicy.CONVERT_COMMA_TO_DOT);
f1.SetValuePostFormatter(formatter);
res = f1.GetParameterValue();
Assert.assertEquals("77s7s,dw2.3wdq,dd", res);
```
*The `GetParameterValue()` function is one abstract `Filter` function that a converter (to Url parameter string) is using.*

### FilterFormatter

Client wants to 'stringify' a `Filter` to a user friendly format. For this purpose `Filter` has an injectable `FilterFormatter`. Already the abstract `FilterFormatter` has the heavy task to format the `Filter` to a desired string, but also enables the client to extend this class in order to provide more control over the formatting of the `Filter`.
To begin with, `FilterFormatter` has a textbased API for setting a string pattern which will guide the formatter to format the `Filter`. The text-based API of this pattern is displayed bellow:

| Literal        |Initials| Description  |
| :------------- | :-----| :----- |
|`$fv`|FilterValue|Renders the value of the `Filter`. |
|`$cn`|ContainerName|Renders the name of the filter container. |
|`$fn`|FilterName|Renders the name of the `Filter`. |
|`$fv[i]`|i-th FilterValue|Renders the i-th value of the filter. For example, to display the second value in a `RangeFilter` that has two values (from and to), we provide $fv[1]. |
|`$f[i].fn`|i-th Filter FilterName|This is used for the `CompositeFilter`, it returns the i-th `Filter`'s name. |
|`$f[i].fv`|i-th Filter FilterValue|This is used for the `CompositeFilter`, it returns the i-th `Filter'`s value. |
|`$f[i].fv[i]`|i-th Filter FilterValue|This is used for the `CompositeFilter`, it returns the i-th `Filter'`s i-th value. |
|`$f[i].cn`|i-th Filter FilterValue|This is used for the `CompositeFilter`, it returns the i-th `Filter'`s container name. |

In addition, the client will possibly need to provide some injectable parameters depending on the locale it currently uses. For example, if we want to display "from 100 euro" in English and "von 100 euro" in German, we can put a placeholder in the pattern as shown:

`$from $fv[0] euro` 
We suppose that this pattern is for a `RangeFilter`, so it gets the filter's first value (from). The client provides the localized text for the $from placeholder.
A `Filter` can have many formatters and the client gets the formatted text by formatter id. An example is displayed bellow:

```java
MockNotifier notifier = new MockNotifier();
FilterContainer container = new FilterContainer(1, "c1");
MockCheckBoxFilter checkBoxFilter = new MockCheckBoxFilter(1,"f1", notifier);

MockEmptyFormatter formatter = new MockEmptyFormatter(10,"$cn, $fn, $fv");
checkBoxFilter.AddFormatter(formatter);
container.AddFilter(checkBoxFilter);

formatted = checkBoxFilter.GetFormattedText(10);
Assert.assertEquals("c1, f1, false", formatted);
```
An example with multiple formatters:
```java
MockNotifier notifier = new MockNotifier();
FilterContainer container = new FilterContainer(10, "container");

List<String> fromValues = new ArrayList<>();
fromValues.add("100");
fromValues.add("200");
fromValues.add("300");
List<String> toValues = new ArrayList<>();
toValues.add("500");
toValues.add("600");
toValues.add("700");
MockRangeFilter rangeFilter = new MockRangeFilter(1, "price",notifier, fromValues, toValues);
rangeFilter.SetDefaultFrom("100");
rangeFilter.SetDefaultTo("500");
container.AddFilter(rangeFilter);

MockEmptyFormatter fromFormatter = new MockEmptyFormatter("from", "$fn from $fv[0]€");
MockEmptyFormatter toFormatter = new MockEmptyFormatter("to", "$fn to $fv[1]€");
rangeFilter.AddFormatter(fromFormatter);
rangeFilter.AddFormatter(toFormatter);

String fromFormatted = rangeFilter.GetFormattedText("from");
String toFormatted = rangeFilter.GetFormattedText("to");
Assert.assertEquals("price from 100€", fromFormatted);
Assert.assertEquals("price to 500€", toFormatted);
```

And an example with injectable parameters for localization support and control over the filter value:
```java
MockNotifier notifier = new MockNotifier();
FilterContainer container = new FilterContainer(1, "c1");

MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
MockCheckBoxFilterFormatter formatter = new MockCheckBoxFilterFormatter(1,"$fv $local");
f1.AddFormatter(formatter);

String formatted = f1.GetFormattedText(1);
Assert.assertEquals("NO $local", formatted); // $local should not be deserialized

Map<String, String> _params = new HashMap<>();
_params.put("local", "el");
formatted = f1.GetFormattedText(1, _params);
Assert.assertEquals("NO el", formatted);

private class MockCheckBoxFilterFormatter extends FilterFormatter{
    public MockCheckBoxFilterFormatter(Object id, String pattern) {
        super(id, pattern);
    }

    @Override
    public String fv(String filterValue, int valueIndex){
        if(filterValue.equals("false")){
            return "NO";
        }else if(filterValue.equals("true")){
            return "YES";
        }
        return "DONT KNOW";
    }
}

```
### Filter to request parameter convertion
All the filters when instructing a request to a server are converted into parameters. The build-in mechanism which converting these Fitlers to parameters takes into account in which container the filter is. Thus the result is a mix on the container, the filter and possible other 'selected' filters from the same container.
The filters are separated with a mode that denotes if the filter has a single value(SIMPLE), has two values (RANGED) or the filter is complex (COMPLEX) as CompositeFilter. The next table show what each filter returns as ParameterValue and parameterKey (key=value)
*For SINGLE mode filter:*

|Filter| parameter key | parameter value |
| :-------------| :------------- | :----- |
|**CheckBoxFilter**|container's name|filters value |
|**CompositeFilter**|container's name|internal filters values |
|**FreeTextFilter**|filter's name|its state, selected value |
|**RangeFilter**|filter's name|its state, selected value |

*For RANGED mode filter:*

|Filter| parameter key from | parameter key to | parameter value from| parameter value to |
| :-------------| :------------- | :-----| :------------- | :----- |
|**RangedFilter**|filter's name + "From"|fitler's name + "To"|"from:" + filter's from value|"to:" + filter's to value |

*For COMPLEX mode filter*
the convertor iterate thought the internal filters of the Composite.

```java
// we initialize some filters
MockContainer checkBoxContainer = new MockContainer(2,"checkContainer");
MockCheckBoxFilter checkBox1 = new MockCheckBoxFilter(4, "c1", filterNotifier);
MockCheckBoxFilter checkBox2 = new MockCheckBoxFilter(5, "c2", filterNotifier);
MockCheckBoxFilter checkBox3 = new MockCheckBoxFilter(6, "c3", filterNotifier);
checkBoxContainer.AddFilter(checkBox1);
checkBoxContainer.AddFilter(checkBox2);
checkBoxContainer.AddFilter(checkBox3);

MockContainer singleSelectContainer = new MockContainer(1,"singleContainer");
MockSingleSelectFilter singleSelect1 = new MockSingleSelectFilter(this.singleSelectContainer, 1, "f1", filterNotifier);
MockSingleSelectFilter singleSelect2 = new MockSingleSelectFilter(this.singleSelectContainer, 2, "f2", filterNotifier);
MockSingleSelectFilter singleSelect3 = new MockSingleSelectFilter(this.singleSelectContainer, 3, "f3", filterNotifier);
singleSelectContainer.AddFilter(singleSelect1);
singleSelectContainer.AddFilter(singleSelect2);
singleSelectContainer.AddFilter(singleSelect3);

// we  initialize the controller. In a production system we will use the filterContext
MockRequestHandler handler = new MockRequestHandler();
// we use the build-in IRequestConverter.
MockController controller = new MockController(containers, hub, handler, new UrlQueryConverter(new UrlBuilder(",", "&")));

// we change some states using the controller
controller.ChangeState(this.singleSelectContainer.GetId(), 2, "1");
controller.ChangeState(checkBoxContainer.GetId(), checkBox1.Id, "1");
controller.ChangeState(checkBoxContainer.GetId(), checkBox2.Id, "0"); // already false, do nothing
controller.ChangeState(checkBoxContainer.GetId(), checkBox2.Id, "1");
// we check how the filters are converted to parameters
Assert.assertTrue(handler.Request.contains("singleContainer=f2"));
Assert.assertTrue(handler.Request.contains("checkContainer=c1,c2") || handler.Request.contains("checkContainer=c2,c1"));

private class MockRequestHandler implements IRequestHandler {
    public String Request;
    @Override
    public void makeRequest(String request) {
        System.out.println("MockRequestHandler->makeRequest:" + request);
        this.Request = request;
    }
    @Override
    public void Initialize(String request) {
        System.out.println("MockRequestHandler->Initialize:" + request);
        this.Request = request;
    }
    @Override
    public boolean IsRetrieveFromRequest() { return true; }
    @Override
    public boolean IsRetrieveFromParameters() { return true; }
    @Override
    public boolean IsRetrieveFromFilters() { return true; }
}

```


## Boundary and Control objects
The system has a various number of ports for interacting with the client and vise versa. That interfaces are accessible through the next control and boundary objects:

| Object        | Description  |
| :------------- | :-----|
| **FilterContext**  | an object controlling the access for the other boundary and control objects |
| **IFilterController**  | an interface for instructing the framework and controlling the state of filters |
| **IHub**  | an interface for registering various client side components as observers for various events of the framework |
| **Builder** | an object that will build all filters inside the framework. The filters are coming from the client side |
| **ChannelManupulator** | a control object that enables the client to control the channels inside the framework |

**All the above objects can easily obtained through a context that is acting as a singleton, the FilterContext its API is the following**

Bellow there are more detailed APIs
### FilterContext

| Function        | Parameters  | Return type | Description |
| ------------- | ----- | ----- | ----- |
|**Initialize**|`IRequestHandler requestHandler, IRequestConverter requestConverter,Configuration configuration` |`void`|initialize must run before all other access functions called |
|**GetController**||`IFilterController`|returns the controller. Every call to this functions returns the same instance of the controller |
|**GetBuilder**||`Builder`|returns the controller. Every call to this functions returns the same instance of the Builder |
|**GetHub**||`IHub`|returns the controller. Every call to this functions returns the same instance of the IHub |
|**GetChannelmanipulator**||`ChannelManipulator`|returns the controller. Every call to this functions returns the same instance of the ChannelManipulator |
|**Dispose**||`void`|this function cleans the context from the created references. It has to be Initialized again to work properly |


### IFilterController, how to control filters

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :-----|
|AddContainer|`FilterContainer container`|`void`| adds a container to controller |
|RemoveContainerById|`FilterContainer container`|`void`| removes a container by the id of the container |
|GetContainerById|`Object id`|`FilterContainer`| returns a container by id, null if not found |
|GetContainers||`List<FilterContainer>`| returns all containers from controller |
|ChangeState|`Object containerId, Object filterId, String state`|`void`| changes the state of a specific filter. The state format depends of the type of filter |
|MakeRequestWithCurrentState||`void`| instructs a request with parameters depending of the current state of the system |
|MakeDirectRequest|`String url`|`void`| instructs a request with the provided url |
|ResetAllWithoutRequestPropagation||`void`| reseting all filters without instructing a request |
|GetFiltersByChannel|`NotifierChannelType filterChannel`|`List<Filter>`| getting all filters per channel |
|GetCurrentSelectedRequestParameters||`Map<Filter, Date>`| returns all the changed filters which will take part in request as parameters |
|GetCurrentConvertedRequest||`Map<Filter, Date>`| returns serialized the current state of the filters |
|Clear||`void`| clears all the containers from their filters |

### IHub

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :----- |
|ResultReceived|`IResult result`|`void`| the client when receives results from the server, calls this function providing the results in the framework throught a client side object implementing IResult |
|AddResultListener|`IResultHubListener listener`|`void`| a client component can register itself if it wants to receives notifications when a results is received |
|RemoveResultListener|`IResultHubListener listener`|`void`| removes the listener |
|ClearResultListeners||`void`| clears all result listeners |
|AddInterconnection|`FilterInterconnection interconnection`|`void`| adds an interconnection |
|RemoveInterconnection|`FilterInterconnection interconnection`|`void`| removes an interconnection by reference or by id |
|HasInterconnection|`FilterInterconnection interconnection`|`boolean`| returns true if an interconnection with the same id exists |
|Execute|`HubCommand command`|`void`| executs the hubcommand |
|Execute|`List<HubCommand> commands`|`void`| executes all hubcommands one by one |

Furthermore, the IHub extends the next interfaces:
1. IFilterHub
2. IParameterHub
3. IRequestHub

These interfaces corresponds to the three predifined internal channel the framework has. The three interfaces have very similar functions.

#### How to register for events from framework
Various component from client, wants to be notified when filters state changed. For example a component has a preview of the selected filters or a component has the responsibility to change the url depending the changed filters from the framework. When a component wants to register itseft to receive notifications it can do this through the IHub interface. First the component must be implement one or  more of the listener interfaces bellow:
* IFilterHubListener
* IParameterHubListener
* IRequestHubListener

These names dont have a special meaning, its just Channel1,2,3. When a filter has a FilterNotifier in its constructor then all the events raised by that filter will fire the corresponding function from IFilterHubListener. The functions of these three listeners have very similar functions. Bellow we present the IFilterHubListener

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :-----|
|**FilterChanged**|`Filter filter`|`void`|is called when a state of a filter is changed. As parameter is the filter its state changed |
|**FilterReset**|`Filter filter`|`void`|is called when a state of a filter is reset. If a filter is already reset then the event is not raised twice |
|**FilterPropertyChanged**|`Filter filter, String old, String _new, FilterPropertyType propType`|`void`|when the name or the count of a filter is changed this event is raised with the appropriate parameter values |
|**FilterUpdated**|`Filter filter`|`void`|when the contents of the filter are changed, for example the list of a singleTextFilter, then this event is raised. When language changes the contents of the filter should be changed |

#### How to change filter states without making request
There are times when we want to change/restore a state of a filter without causing another http request. This could be done using the HubCommands. A hubCommand is an immutable object which acts as a DTO. We can 'execute' a HubCommand with the interface IHub calling Execute. This will cause the desired effect without making an http request. All other events such as FilterChanged will be raised.

##### HubCommand
HubCommand encapsulated the containerId, desiredCotnainerName, filterId, desiredFilterName, state and count. Hub to find the target filter works with the ids from HubCommand. The filter matching will be with the containerid and filterid.

```java
this.hub = new Hub();
FilterNotifier filterNotifier = new FilterNotifier(hub);
ParameterNotifier parameterNotifier = new ParameterNotifier(hub);

MockContainer singleSelectContainer = new MockContainer(1, "singleContainer");
MockSingleSelectFilter singleSelect1 = new MockSingleSelectFilter(this.singleSelectContainer, 1, "f1", filterNotifier);
singleSelectContainer.AddFilter(singleSelect1);

List<String> localeFilterValues = new ArrayList<>();
localeFilterValues.add("el");
localeFilterValues.add("en-us");
localeFilterValues.add("en-au");
MockSingleTextFilter locale = new MockSingleTextFilter(14, "locale", parameterNotifier, localeFilterValues);
locale.SetDefaultValue("en-au");
MockContainer localeContainer = new MockContainer(8,"locale");
localeContainer.AddFilter(locale);

// if we want to change the state of singleSelectContainer then
HubCommand singleContainerStateChangeCommand = new HubCommand(1, "singleContainer",1, "f1", "1"); // the filter notifiers will fire

HubCommand singleContainerStateChangeCommand = new HubCommand(8, "locale",14, "locale", "en-us"); //


private class FilterChannel1 implements IFilterHubListener{

    public int Filter;

    @Override
    public void FilterChanged(Filter filter) {
        System.out.println("FilterChannel->FilterChanged:"+filter);
        this.Filter++;
    }
    // other implementation functions....
}

private class FilterChannel2 implements IParameterHubListener{
    // implementation functions...
}

```

### Builder

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :----- |
|`AddObserver`|`IBuilderObserver observer`|`void`|registers a component as an observer |
|`RemoveObserver`|`IBuilderObserver observer`|`void`|removes a component from observer |
|`ClearObservers`||`void`|clears all observers|
|`Build`|`BuilderItems items`|`void`|build(add, update, remove) filters inside the framework depending on the filters inside the BuildeItems. The items with the already existing filters inside the framework are matched by ids |

#### BuilderItems
A structure must be passed every time we want to synchronize the internal state/number of filters inside the system with a response from a server. This object encapsulates all the containers the client created and want to be passed inside the framework so the framework will rspond with the proper events to notify the rest of the system.
Every time a results is received and various filters are returns from server in a format such as json, and client wants the framework to be updated and corresponding events be raised then client uses the BuilderItems to communicate with the framework throught Builder. Lets see an example
```java
// we have hardcoded items but the items clearly can be deserialized from a json or other formats as well.
private class MockBuilderItems extends BuilderItems
{
    List<FilterContainer> containers ;
    IHub hub;

    public MockBuilderItems(IHub hub) {
        this.hub = hub;
        this.containers = new ArrayList<>();
        this.containers.add(new FilterContainer(1,"c1"));
        this.containers.add(new FilterContainer(2,"c2"));

        CheckBoxFilter checkBoxFilter1 = new MockCheckBoxFilter(1, "c1", new FilterNotifier(this.hub) );
        CheckBoxFilter checkBoxFilter2 = new MockCheckBoxFilter(2 ,"c2", new FilterNotifier(this.hub) );
        this.containers.get(0).AddFilter(checkBoxFilter1);
        this.containers.get(0).AddFilter(checkBoxFilter2);

        MockCheckBoxFilter range = new MockCheckBoxFilter(3, "r1", new ParameterNotifier(this.hub));
        this.containers.get(1).AddFilter(range);
    }

    @Override
    public List<FilterContainer> GetContainers() {
        return this.containers;
    }
}

// to pass the items inside the framework we use the Builder
Builder builder = filterContext.GetBuilder();
builder.Build(new MockBuilderItems(filterContext.GetHub()));
// then the framework raises all the relevant creational events to synchronize itseft with the new results

```
*One comment though. In the build prosess one thing doenst change, the state of the filter. If *


#### How to receive creational events
One can build dynamically the filters in the interface from events from the IBuilderObserver. Once the component which builds the filters in the interface registers itseld as builder observer it can receive the next events:

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :----- |
|**FilterAdded**|`ActionType actionType, Filter filter`|`void`|when a filter is added to the system this function is called |
|**FilterRemoved**|`ActionType actionType, Filter filter`|`void`|when a filter is removed from the system this function is called |
|**ContainerAdded**|`ActionType actionType, FilterContainer container`|`void`|when a container is added to the system this function is called |
|**ContainerRemoved**|`ActionType actionType, FilterContainer container`|`void`|when a container is removed from the system this function is called |
|**ContainerUpdated**|`ActionType actionType, FilterContainer container`|`void`|when a container is updated in the system this function is called, this happens when container name is changed |

## Channel Manipulator
This object controls the state of the channels, paused or active. When a channel is paused no registered component can receive events from it. All filter channels (Filter, Parameter, Request), builder channel and request channel can be controled througth the interface bellow:

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :-----|
|**PauseChannel**|`NotifierChannelType channelType`|`void`|pause any of the filter channels (Filter, Parameter, Request) |
|**UnPauseChannel**|`NotifierChannelType channelType`|`void`|unpause/resume any of the filter channels (Filter, Parameter, Request) |
|**PauseAllFilterChannels**||`void`|pauses all the filter channels (Filter, Parameter, Request) |
|**UnPauseAllFilterChannels**||`void`|unpauses/resumes all the filter channels (Filter, Parameter, Request) |
|**PauseRequestHandlerChannel**||`void`|pause the request channel, that means that the framework will not instruct the client to make a request |
|**UnPauseRequestHandlerChannel**||`void`|unpause/resume the request channel, that means that the framework will instruct the client to make a request |
|**PauseBuilderChannel**||`void`|pause the builder channel will cause the creational events not be called |
|**UnPauseBuilderChannel**||`void`|unpause/resume the builder channel |

## Request instruction/handling
Client is responsible to make the actual request (http or whatever) and get the result. Instructing that request is the responsibility of the framework. When a FilterContext is initialized it must have an IRequestHandler. behind this interface lies the object which is instructed by the framework to make the actual request. Lets see what IRequestHandler must have:

| Function        | Parameters  | Return type | Description |
| :------------- | :-----| :-----| :-----|
|**makeRequest**|`String request`|`void`|framework instructs the client to make a request calling this method. The request string will have all the changed filters converted with the IRequestConverter |
|**IsRetrieveFromFilters**||`boolean`|true if client wants to include the filters that have a FilterNotifier in the request parameters |
|**IsRetrieveFromParameters**||`boolean`|true if client wants to include the filters that have a ParameterNotifier in the request parameters |
|**IsRetrieveFromRequest**||`boolean`|true if client wants to include the filters that have a RequestNotifier in the request parameters |

## Configuration
The framework is configurable through its boundary objects as following:

| Object | Parameter  | Description |
| ------------- | -----| ----- |
|**Filter**|`FilterFormatter`| a filter display formatter |
|**Filter** | `IValuePostFormatter` | a filter value formatter |
|**Filter** | `SelectedValuePolicy` | allow filter to have null as  selected value |
|**FilterContext** | `IRequestHandler` | denotes who will be the handler for the request instructions, and this object must denote which filter channels to include as request parameters |
|**FilterContext** | `IRequestConverter` | the strategy which the filters are converted to request parameters |
|**FilterContext** | `Configuration` | this includes the strategies that will take place in the building process |

*All of the above are configurable and extensible from the client*


## Filter rules
Sometimes we want to reset some filters depending the activity of others before making any request. For example we have a price range filter which have FilterNotifier and a sorting singleTextFilter with a ParameterNotifier. We want to reset the sorting to a default value every time we select other price in the RangeFilter.
For this purpose we use the FilterInterconnection object. A FilterInterconnection replesents/encapsulates a rule. After initializing this kind of object we pass it as parameter to the Hub calling AddInterconnection();

Lets do an example. We want when we reset any of the checkBox1 or the range1 filters to change the state of the freeFilter to "abc" and to change state of the single2 to 1
```java
Hub hub = new Hub();
FilterNotifier filterNotifier = new FilterNotifier(hub);

FilterContainer container1 = new FilterContainer(1, "c1");
MockCheckBoxFilter checkBox1 = new MockCheckBoxFilter(1, "f1", this.filterNotifier);
List<String> fromValues = new ArrayList<>();
fromValues.add("100");
fromValues.add("200");
fromValues.add("300");
List<String> toValues = new ArrayList<>();
toValues.add("200");
toValues.add("300");
toValues.add("400");
this.range1 = new MockRangeFilter(3, "f3", this.filterNotifier,fromValues,toValues);
this.range1.SetDefaultFrom("100");
this.range1.SetDefaultTo("400");
container1.AddFilter(checkBox1);
container1.AddFilter(range1);

FilterContainer container2 = new FilterContainer(2, "c2");
MockFreeTextFilter freeFilter = new MockFreeTextFilter(4, "f1", filterNotifier);
freeFilter.SetDefaultValue("");

FilterContainer container3 = new FilterContainer(3,"c3");
MockSingleSelectFilter single1 = new MockSingleSelectFilter(this.container3, 7, "f1", this.filterNotifier);
MockSingleSelectFilter single2 = new MockSingleSelectFilter(this.container3, 8, "f2", this.filterNotifier);
container3.AddFilter(single1);
container3.AddFilter(single2);

FilterInterconnection interconnection1 = new FilterInterconnection(1);
interconnection1.When.Event = FilterEvent.Reset;
interconnection1.When.Who(this.checkBox1);
interconnection1.When.Who(this.range1);
EventSubjectPair thenClause1_1 = new EventSubjectPair();
thenClause1_1.Event = FilterEvent.StateChange;
thenClause1_1.Parameters = "abc";
thenClause1_1.Who(this.freeFilter);
EventSubjectPair thenClause1_2 = new EventSubjectPair();
thenClause1_2.Event = FilterEvent.StateChange;
thenClause1_2.Parameters = "1";
thenClause1_2.Who(this.single2);
interconnection1.Then.add(thenClause1_1);
interconnection1.Then.add(thenClause1_2);
this.hub.AddInterconnection(interconnection1);

// we check the result
checkBox1.Check();
Assert.assertEquals("", freeFilter.GetState());
Assert.assertEquals(false, single2.IsChecked());
checkBox1.UnCheck();
Assert.assertEquals("abc", freeFilter.GetState());
Assert.assertEquals(true, single2.IsChecked());
freeFilter.ChangeState("");
single2.Check();
range1.ChangeState("from:200-to:200");
range1.Reset();
Assert.assertEquals("abc", freeFilter.GetState());
Assert.assertEquals(true, single2.IsChecked());

```

## a test use case, how all fits together
bellow we present a snapshot of the system we want to built
[fireman overview image]
Some dinstinct areas are:
1. In left side we have the tree view of the filters. From that component we can manipulate (change state, reset) the filters and we receive events when a filter changes.
2. In the middle component we have the results. The results component is a result listener that responts to the ResultReceived event to draw the results.
3. At the top we have the url of the browser that is changed when a filter state changes. The responsible component handling the apropriate events and controlling the bowsers url could be the container of all the other components.
4. In the 'SELECTED FILTERS PREVIEW' section we present the selected filters. The user can easily remove/reset a filter from that section causing a filterReset event to the other components.
5. in 'PAGING SECTION' we have the page as a filter like the rest, but it has a ParameterNotifier insdead of a FilterNotifier that the filters in the tree view have to separate it from them. This separation means that the page filter will not affects the url (because we dont want to have page parameter in the url) but will affect the http request.

Another view of this system is shown bellow:
[fireman backstage.html image]
in this view we see more separately the different components of the interface and the connections that have with the fireman framework.

