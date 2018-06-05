package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filtercontroller.IRequestConverter;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.ReservedState;
import domain.hub.Hub;
import domain.hub.IFilterHubListener;
import domain.hub.IParameterHubListener;
import domain.hub.IRequestHubListener;
import domain.notifier.FilterNotifier;
import domain.notifier.ParameterNotifier;
import domain.notifier.RequestNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class ChannelTesting {

    private MockFilterController controller;

    private MockContainer singleSelectContainer;
    private MockSingleSelectFilter singleSelect1;
    private MockSingleSelectFilter singleSelect2;
    private MockSingleSelectFilter singleSelect3;

    private MockContainer checkBoxContainer;
    private MockCheckBoxFilter checkBox1;
    private MockCheckBoxFilter checkBox2;
    private MockCheckBoxFilter checkBox3;

    private MockContainer freeTextContainer;
    private MockFreeTextFilter freeText1;

    private MockContainer complexContainer;
    private MockCompositeFilter compositeFilter;
    private MockFreeTextFilter complexFreeText;
    private MockSingleTextFilter complexSingleText;

    private MockContainer rangeContainer;
    private MockRangeFilter range;

    private MockContainer pageContainer;
    private MockSingleTextFilter pageFilter;

    private MockContainer sortContainer;
    private MockSingleTextFilter sortFilter;

    private MockContainer localeContainer;
    private MockSingleTextFilter locale;

    Hub hub;

    ParameterFilterChannel parameterFilterChannel;
    FilterChannel1 filterChannel1;
    FilterChannel2 filterChannel2;
    CompleteChannel completeChannel;

    @Before
    public void Setup(){
        this.hub = new Hub();
        FilterNotifier filterNotifier = new FilterNotifier(hub);
        ParameterNotifier parameterNotifier = new ParameterNotifier(hub);
        RequestNotifier requestNotifier = new RequestNotifier(hub);

        this.singleSelectContainer = new MockContainer(1,"singleContainer");
        this.singleSelect1 = new MockSingleSelectFilter(this.singleSelectContainer, 1, "f1", filterNotifier);
        this.singleSelect2 = new MockSingleSelectFilter(this.singleSelectContainer, 2, "f2", filterNotifier);
        this.singleSelect3 = new MockSingleSelectFilter(this.singleSelectContainer, 3, "f3", filterNotifier);
        this.singleSelectContainer.AddFilter(singleSelect1);
        this.singleSelectContainer.AddFilter(singleSelect2);
        this.singleSelectContainer.AddFilter(singleSelect3);

        this.checkBox1 = new MockCheckBoxFilter(4, "c1", filterNotifier);
        this.checkBox2 = new MockCheckBoxFilter(5, "c2", filterNotifier);
        this.checkBox3 = new MockCheckBoxFilter(6, "c3", filterNotifier);
        this.checkBoxContainer = new MockContainer(2,"checkContainer");
        this.checkBoxContainer.AddFilter(checkBox1);
        this.checkBoxContainer.AddFilter(checkBox2);
        this.checkBoxContainer.AddFilter(checkBox3);

        this.freeText1 = new MockFreeTextFilter(7, "f1", filterNotifier);
        this.freeTextContainer = new MockContainer(3,"freeContainer");
        this.freeTextContainer.AddFilter(freeText1);

        this.compositeFilter = new MockCompositeFilter(10, "c1", filterNotifier);
        this.complexFreeText = new MockFreeTextFilter(8, "cf1", this.compositeFilter);
        List<String> complexSingleTextValues = new ArrayList<>();
        complexSingleTextValues.add("x");
        complexSingleTextValues.add("y");
        complexSingleTextValues.add("z");
        this.complexSingleText = new MockSingleTextFilter(9, "cs1", this.compositeFilter, complexSingleTextValues);
        this.complexSingleText.SetDefaultValue("y");

        this.compositeFilter.AddFilter(complexFreeText);
        this.compositeFilter.AddFilter(complexSingleText);
        this.complexContainer = new MockContainer(4,"complex");
        this.complexContainer.AddFilter(this.compositeFilter);

        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        List<String> toValues = new ArrayList<>();
        toValues.add("200");
        toValues.add("300");
        toValues.add("400");
        toValues.add("500");
        this.range = new MockRangeFilter(11, "r", filterNotifier, fromValues, toValues);

//        this.range.UpdateFromValues(fromValues);
//        this.range.UpdateToValues(toValues);
        this.range.SetDefaultFrom("200");
        this.range.SetDefaultTo("300");
        this.rangeContainer = new MockContainer(5,"range");
        this.rangeContainer.AddFilter(range);

        List<String> pageFilterValues = new ArrayList<>();
        pageFilterValues.add("1");
        pageFilterValues.add("2");
        pageFilterValues.add("3");
        pageFilterValues.add("4");
        this.pageFilter = new MockSingleTextFilter(12, "page", requestNotifier,pageFilterValues);
        this.pageFilter.SetDefaultValue("1");
        this.pageContainer = new MockContainer(6,"paging");
        this.pageContainer.AddFilter(pageFilter);

        List<String> sortFilterValues = new ArrayList<>();
        sortFilterValues.add("asc");
        sortFilterValues.add("desc");
        this.sortFilter = new MockSingleTextFilter(13, "s", requestNotifier,sortFilterValues);
        this.sortFilter.SetDefaultValue("asc");
        this.sortContainer = new MockContainer(7,"sorting");
        this.sortContainer.AddFilter(sortFilter);

        List<String> localeFilterValues = new ArrayList<>();
        localeFilterValues.add("el");
        localeFilterValues.add("en-us");
        localeFilterValues.add("en-au");
        this.locale = new MockSingleTextFilter(14, "locale", parameterNotifier, localeFilterValues);
        this.locale.SetDefaultValue("en-au");
        this.localeContainer = new MockContainer(8,"locale");
        this.localeContainer.AddFilter(locale);

        this.parameterFilterChannel = new ParameterFilterChannel();
        this.filterChannel1 = new FilterChannel1();
        this.filterChannel2 = new FilterChannel2();
        this.completeChannel = new CompleteChannel();

        this.hub.AddFilterListener(this.parameterFilterChannel);
        this.hub.AddParameterListener(this.parameterFilterChannel);

        this.hub.AddFilterListener(this.filterChannel1);
        this.hub.AddFilterListener(this.filterChannel2);

        this.hub.AddFilterListener(this.completeChannel);
        this.hub.AddParameterListener(this.completeChannel);
        this.hub.AddRequestListener(this.completeChannel);

    }

    @Test
    public void testMultipleChannelCommandsFromController(){
        List<FilterContainer> containers = new ArrayList<>();
        containers.add(this.singleSelectContainer);
        containers.add(this.checkBoxContainer);
        containers.add(this.freeTextContainer);
        containers.add(this.complexContainer);
        containers.add(this.rangeContainer);
        containers.add(this.pageContainer);
        containers.add(this.sortContainer);
        containers.add(this.localeContainer);
        MockRequestHandler handler = new MockRequestHandler();
        MockController controller = new MockController(containers, hub, handler, new UrlQueryConverter(new UrlBuilder(",", "&")));

        controller.ChangeState(this.singleSelectContainer.GetName(), "f2", "1"); // should all filter listeners notified
        Assert.assertEquals(this.singleSelectContainer.GetName() + "=" + "f2", handler.Request);
        Assert.assertEquals(1, this.parameterFilterChannel.Filter);
        Assert.assertEquals(1, this.filterChannel1.Filter);
        Assert.assertEquals(1, this.filterChannel2.Filter);
        Assert.assertEquals(1, this.completeChannel.Filter);

        controller.ChangeState("checkContainer", "c1", "1");
        controller.ChangeState("checkContainer", "c2", "0"); // already false, do nothing
        controller.ChangeState("checkContainer", "c2", "1");

        Assert.assertEquals(3, this.parameterFilterChannel.Filter);
        Assert.assertEquals(3, this.filterChannel1.Filter);
        Assert.assertEquals(3, this.filterChannel2.Filter);
        Assert.assertEquals(3, this.completeChannel.Filter);
        Assert.assertTrue(handler.Request.contains("singleContainer=f2"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c1,c2") || handler.Request.contains("checkContainer=c2,c1"));

        // change a request notifier
        controller.ChangeState("sorting", "s", "asc"); // do nothing already set as default value
        controller.ChangeState("sorting", "s", "desc");

        //change a parameter notifier
        controller.ChangeState("locale", "locale", "en-au"); // en-au is set as selected do nothing
        controller.ChangeState("locale", "locale", "abc"); // do nothing because is not in list
        controller.ChangeState("locale", "locale", "el"); // +1 for parameter

        Assert.assertEquals(1, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(3, this.filterChannel1.Filter);
        Assert.assertEquals(3, this.filterChannel2.Filter);
        Assert.assertEquals(1, this.completeChannel.Parameter);
        Assert.assertEquals(1, this.completeChannel.Request);
        Assert.assertEquals(3, this.completeChannel.Filter);
        Assert.assertTrue(handler.Request.contains("singleContainer=f2"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c1,c2") || handler.Request.contains("checkContainer=c2,c1"));
        Assert.assertTrue(handler.Request.contains("locale=el"));
        Assert.assertTrue(handler.Request.contains("s=desc"));


        controller.ChangeState("freeContainer", "f1", "text");
        controller.ChangeState("complex", "c1", "cs1:x"); // to set state of a filter inside a composite filter,
        // for filter name you enter composite_filter_name and for state you have the filters name first following by : and then the state
        // is already x from the begining
        controller.ChangeState("complex", "c1", "cf1:free_text");
        Assert.assertEquals(1, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(6, this.parameterFilterChannel.Filter);
        Assert.assertEquals(6, this.filterChannel1.Filter);
        Assert.assertEquals(6, this.filterChannel2.Filter);
        Assert.assertEquals(1, this.completeChannel.Parameter);
        Assert.assertEquals(1, this.completeChannel.Request);
        Assert.assertEquals(6, this.completeChannel.Filter);
        Assert.assertTrue(handler.Request.contains("singleContainer=f2"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c1,c2") || handler.Request.contains("checkContainer=c2,c1"));
        Assert.assertTrue(handler.Request.contains("locale=el"));
        Assert.assertTrue(handler.Request.contains("f1=text"));
        Assert.assertTrue(handler.Request.contains("cs1=x")); // for complex the inside fiter is shown
        Assert.assertTrue(handler.Request.contains("cf1=free_text"));

        controller.ChangeState("range", "r", "from:200"); // already set, do nothing
        controller.ChangeState("range", "r", "to:400");
        controller.ChangeState("range", "r", "from:100");
        controller.ChangeState("range", "r", "from:200-to:200");
        Assert.assertEquals(1, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(9, this.parameterFilterChannel.Filter);
        Assert.assertEquals(9, this.filterChannel1.Filter);
        Assert.assertEquals(9, this.filterChannel2.Filter);
        Assert.assertEquals(1, this.completeChannel.Parameter);
        Assert.assertEquals(1, this.completeChannel.Request);
        Assert.assertEquals(9, this.completeChannel.Filter);
        Assert.assertTrue(handler.Request.contains("singleContainer=f2"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c1,c2") || handler.Request.contains("checkContainer=c2,c1"));
        Assert.assertTrue(handler.Request.contains("locale=el"));
        Assert.assertTrue(handler.Request.contains("f1=text"));
        Assert.assertTrue(handler.Request.contains("cs1=x")); // for complex the inside fiter is shown
        Assert.assertTrue(handler.Request.contains("cf1=free_text"));
        Assert.assertTrue(handler.Request.contains("r=from:200-to:200"));

        controller.ChangeState("checkContainer", "c3", "1"); // filterNotifiers +1
        controller.ChangeState("checkContainer", "c2", ReservedState.reset); // filterNotifiers -1
        controller.ChangeState("complex", "c1", "cf1:free_text2"); // filterNotifiers +1
        controller.ChangeState("freeContainer", "f1", "text22"); // filterNotifiers +1
        controller.ChangeState("locale", "locale", ReservedState.reset); // parameterNotifiers -1
        controller.ChangeState("sorting", "s", "asc"); // requestNotifiers -1
        controller.ChangeState("singleContainer", "f1", "1"); // filterNotifiers +1, also reset f2, so filterNotifiers -1
        Assert.assertEquals(0, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(11, this.parameterFilterChannel.Filter);
        Assert.assertEquals(11, this.filterChannel1.Filter);
        Assert.assertEquals(11, this.filterChannel2.Filter);
        Assert.assertEquals(0, this.completeChannel.Parameter);
        Assert.assertEquals(0, this.completeChannel.Request);
        Assert.assertEquals(11, this.completeChannel.Filter);
        Assert.assertTrue(handler.Request.contains("singleContainer=f1"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c1,c3") || handler.Request.contains("checkContainer=c3,c1"));
        Assert.assertFalse(handler.Request.contains("locale=en-au")); // is reset and dont apear in request
        Assert.assertTrue(handler.Request.contains("s=asc"));
        Assert.assertTrue(handler.Request.contains("f1=text22"));
        Assert.assertTrue(handler.Request.contains("cs1=x")); // for complex the inside fiter is shown
        Assert.assertTrue(handler.Request.contains("cf1=free_text2"));
        Assert.assertTrue(handler.Request.contains("r=from:200-to:200"));

    }

    private class ParameterFilterChannel implements IParameterHubListener, IFilterHubListener{

        public int Parameter;
        public int Filter;

        public ParameterFilterChannel() {
            Parameter = 0;
            Filter = 0;
        }

        @Override
        public void ParameterChanged(Filter filter) {
            System.out.println("ParameterFilterChannel->ParameterChanged:"+filter);
            this.Parameter++;
        }

        @Override
        public void ParameterReset(Filter filter) {
            System.out.println("ParameterFilterChannel->ParameterReset:"+filter);
            this.Parameter--;
        }

        @Override
        public void ParameterPropertyChanged(domain.filters.Filter filter, String old, String aNew, FilterPropertyType propType) {

        }

        @Override
        public void ParameterUpdated(domain.filters.Filter filter) {

        }

        @Override
        public void FilterChanged(Filter filter) {
            System.out.println("ParameterFilterChannel->RequestChanged:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterReset(Filter filter) {
            System.out.println("ParameterFilterChannel->RequestReset:"+filter);
            this.Filter--;
        }

        @Override
        public void FilterPropertyChanged(domain.filters.Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void FilterUpdated(domain.filters.Filter filter) {

        }
    }

    private class FilterChannel1 implements IFilterHubListener{

        public int Filter;

        @Override
        public void FilterChanged(Filter filter) {
            System.out.println("FilterChannel->FilterChanged:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterReset(Filter filter) {
            System.out.println("FilterChannel->FilterReset:"+filter);
            Filter--;
        }

        @Override
        public void FilterPropertyChanged(domain.filters.Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void FilterUpdated(domain.filters.Filter filter) {

        }
    }

    private class FilterChannel2 implements IFilterHubListener{

        public int Filter;

        @Override
        public void FilterChanged(Filter filter) {
            System.out.println("FilterChannel->FilterChanged:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterReset(Filter filter) {
            System.out.println("FilterChannel->FilterReset:"+filter);
            Filter--;
        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void FilterUpdated(domain.filters.Filter filter) {

        }
    }

    private class CompleteChannel implements IFilterHubListener, IParameterHubListener, IRequestHubListener{

        public int Parameter;
        public int Request;
        public int Filter;

        public CompleteChannel() {
            this.Parameter = 0;
            this.Request = 0;
            this.Filter = 0;
        }

        @Override
        public void FilterChanged(Filter filter) {
            System.out.println("CompleteChannel->FilterChanged:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterReset(Filter filter) {
            System.out.println("CompleteChannel->FilterReset:"+filter);
            this.Filter--;
        }

        @Override
        public void FilterPropertyChanged(domain.filters.Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void FilterUpdated(domain.filters.Filter filter) {

        }

        @Override
        public void ParameterChanged(Filter filter) {
            System.out.println("CompleteChannel->ParameterChanged:"+filter);
            this.Parameter++;
        }

        @Override
        public void ParameterReset(Filter filter) {
            System.out.println("CompleteChannel->ParameterReset:"+filter);
            this.Parameter--;
        }

        @Override
        public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

        }

        @Override
        public void ParameterUpdated(domain.filters.Filter filter) {

        }

        @Override
        public void RequestChanged(Filter filter) {
            System.out.println("CompleteChannel->RequestChanged:"+filter);
            this.Request++;
        }

        @Override
        public void RequestReset(Filter filter) {
            System.out.println("CompleteChannel->RequestReset:"+filter);
            this.Request--;
        }

        @Override
        public void RequestPropertyChanged(domain.filters.Filter filter, String old, String aNew, FilterPropertyType propType) {

        }

        @Override
        public void RequestUpdated(domain.filters.Filter filter) {

        }
    }

    private class MockFilterController extends FilterController{

        protected MockFilterController(List<FilterContainer> containers, Hub hub, IRequestHandler receiver, IRequestConverter requestConverter) {
            super(containers, hub, receiver, requestConverter);
        }
    }

    private class MockContainer extends FilterContainer{

        protected MockContainer(Object id, String name) {
            super(id, name);
        }
    }

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

    private class MockController extends FilterController{

        protected MockController(List<FilterContainer> containers, Hub hub, IRequestHandler handler, IRequestConverter requestConverter) {
            super(containers, hub, handler, requestConverter);
        }
    }

}
