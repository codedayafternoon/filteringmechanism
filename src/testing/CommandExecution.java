package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.ReservedState;
import domain.hub.*;
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
 * Created by Jimfi on 5/28/2018.
 */
public class CommandExecution {
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

        this.singleSelectContainer = new MockContainer("singleContainer");
        this.singleSelect1 = new MockSingleSelectFilter(this.singleSelectContainer, 1, "f1", filterNotifier);
        this.singleSelect2 = new MockSingleSelectFilter(this.singleSelectContainer, 2, "f2", filterNotifier);
        this.singleSelect3 = new MockSingleSelectFilter(this.singleSelectContainer, 3, "f3", filterNotifier);
        this.singleSelectContainer.AddFilter(singleSelect1);
        this.singleSelectContainer.AddFilter(singleSelect2);
        this.singleSelectContainer.AddFilter(singleSelect3);

        this.checkBox1 = new MockCheckBoxFilter(4, "c1", filterNotifier);
        this.checkBox2 = new MockCheckBoxFilter(5, "c2", filterNotifier);
        this.checkBox3 = new MockCheckBoxFilter(6, "c3", filterNotifier);
        this.checkBoxContainer = new MockContainer("checkContainer");
        this.checkBoxContainer.AddFilter(checkBox1);
        this.checkBoxContainer.AddFilter(checkBox2);
        this.checkBoxContainer.AddFilter(checkBox3);

        this.freeText1 = new MockFreeTextFilter(7, "f1", filterNotifier);
        this.freeTextContainer = new MockContainer("freeContainer");
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
        this.complexContainer = new MockContainer("complex");
        this.complexContainer.AddFilter(this.compositeFilter);

        this.range = new MockRangeFilter(11, "r", filterNotifier);
        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        List<String> toValues = new ArrayList<>();
        toValues.add("200");
        toValues.add("300");
        toValues.add("400");
        toValues.add("500");
        this.range.AddFromValues(fromValues);
        this.range.AddToValues(toValues);
        this.range.SetDefaultFrom("200");
        this.range.SetDefaultTo("300");
        this.rangeContainer = new MockContainer("range");
        this.rangeContainer.AddFilter(range);

        List<String> pageFilterValues = new ArrayList<>();
        pageFilterValues.add("1");
        pageFilterValues.add("2");
        pageFilterValues.add("3");
        pageFilterValues.add("4");
        this.pageFilter = new MockSingleTextFilter(12, "page", requestNotifier,pageFilterValues);
        this.pageFilter.SetDefaultValue("1");
        this.pageContainer = new MockContainer("paging");
        this.pageContainer.AddFilter(pageFilter);

        List<String> sortFilterValues = new ArrayList<>();
        sortFilterValues.add("asc");
        sortFilterValues.add("desc");
        this.sortFilter = new MockSingleTextFilter(13, "s", requestNotifier,sortFilterValues);
        this.sortFilter.SetDefaultValue("asc");
        this.sortContainer = new MockContainer("sorting");
        this.sortContainer.AddFilter(sortFilter);

        List<String> localeFilterValues = new ArrayList<>();
        localeFilterValues.add("el");
        localeFilterValues.add("en-us");
        localeFilterValues.add("en-au");
        this.locale = new MockSingleTextFilter(14, "locale", parameterNotifier, localeFilterValues);
        this.locale.SetDefaultValue("en-au");
        this.localeContainer = new MockContainer("locale");
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
    public void testCommandExecution() {
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

        HubCommand singleContainerStateChangeCommand = new HubCommand("singleContainer", "f1", "1"); // filter notifiers +1
        HubCommand singleContainerStateChangeCommand2 = new HubCommand("singleContainer", "f2", "1"); // filterNotifiers +1 and -1 for reseting f1
        HubCommand singleContainerStateChangeCommand3 = new HubCommand("singleContainer", "f3", "1"); // filterNotifiers +1 and -1 for reseting f2

        HubCommand checkBoxStateChangeCommand = new HubCommand("checkContainer", "c2", "1"); // filter notifiers +1
        HubCommand checkBoxStateChangeCountCommand2 = new HubCommand("checkContainer", "c1", 10, "1"); // filter notifiers +1 for state +1 for count
        HubCommand checkBoxStateChangeCountCommand3 = new HubCommand("checkContainer", "c2",100, "0"); // filter notifiers -1 for resetting +1 for count

        HubCommand freeContainerStateChangeCommand3 = new HubCommand("freeContainer", "f1", "free_text"); // filter notifiers +1

        HubCommand rangeStateChangeCommand1 = new HubCommand("range", "r",63, "from:100"); // filter notifiers +1 count +0 because range is not ICountable
        HubCommand rangeStateChangeCommand2 = new HubCommand("range", "r", "to:400"); // filter notifiers +1

        this.hub.Execute(singleContainerStateChangeCommand);
        this.hub.Execute(singleContainerStateChangeCommand2);
        this.hub.Execute(singleContainerStateChangeCommand3);

        this.hub.Execute(checkBoxStateChangeCommand);
        this.hub.Execute(checkBoxStateChangeCountCommand2);
        this.hub.Execute(checkBoxStateChangeCountCommand3);

        this.hub.Execute(freeContainerStateChangeCommand3);

        this.hub.Execute(rangeStateChangeCommand1);
        this.hub.Execute(rangeStateChangeCommand2);

        Assert.assertEquals(0, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(7, this.parameterFilterChannel.Filter);
        Assert.assertEquals(7, this.filterChannel1.Filter);
        Assert.assertEquals(7, this.filterChannel2.Filter);
        Assert.assertEquals(0, this.completeChannel.Parameter);
        Assert.assertEquals(0, this.completeChannel.Request);
        Assert.assertEquals(7, this.completeChannel.Filter);

        controller.ChangeState("complex", "c1", "cs1:z");
        Assert.assertEquals(0, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(8, this.parameterFilterChannel.Filter);
        Assert.assertEquals(8, this.filterChannel1.Filter);
        Assert.assertEquals(8, this.filterChannel2.Filter);
        Assert.assertEquals(0, this.completeChannel.Parameter);
        Assert.assertEquals(0, this.completeChannel.Request);
        Assert.assertEquals(8, this.completeChannel.Filter);

        // it should have all state changes besides change from controller
        Assert.assertTrue(handler.Request.contains("singleContainer=f3"));
        Assert.assertTrue(handler.Request.contains("checkContainer=c2,c1") || handler.Request.contains("checkContainer=c1,c2"));
        Assert.assertTrue(handler.Request.contains("f1=free_text"));
        Assert.assertTrue(handler.Request.contains("r=from:100-to:400"));
        Assert.assertTrue(handler.Request.contains("cs1=z"));

        HubCommand singleContainerStateChangeCommandreset = new HubCommand("singleContainer", "f3", ReservedState.reset.toString()); // filterNotifiers -1

        HubCommand checkBoxStateChangeCommandreset = new HubCommand("checkContainer", "c2", "0"); // filter notifiers 0 because is already 0
        HubCommand checkBoxStateChangeCountCommand2reset = new HubCommand("checkContainer", "c1", "0"); // filter notifiers -1

        HubCommand freeContainerStateChangeCommand3reset = new HubCommand("freeContainer", "f1", ""); // filter notifiers +1

        HubCommand rangeStateChangeCommand1reset = new HubCommand("range", "r", "from:200"); // filter notifiers +1
        HubCommand rangeStateChangeCommand2reset = new HubCommand("range", "r", "to:300"); // filter notifiers +1

        this.hub.Execute(singleContainerStateChangeCommandreset);

        this.hub.Execute(checkBoxStateChangeCommandreset);
        this.hub.Execute(checkBoxStateChangeCountCommand2reset);

        this.hub.Execute(freeContainerStateChangeCommand3reset);

        this.hub.Execute(rangeStateChangeCommand1reset);
        this.hub.Execute(rangeStateChangeCommand2reset);

        Assert.assertEquals(0, this.parameterFilterChannel.Parameter);
        Assert.assertEquals(9, this.parameterFilterChannel.Filter);
        Assert.assertEquals(9, this.filterChannel1.Filter);
        Assert.assertEquals(9, this.filterChannel2.Filter);
        Assert.assertEquals(0, this.completeChannel.Parameter);
        Assert.assertEquals(0, this.completeChannel.Request);
        Assert.assertEquals(9, this.completeChannel.Filter);

        controller.ChangeState("complex", "c1", "cs1:x");
        Assert.assertFalse(handler.Request.contains("singleContainer=f3"));
        Assert.assertFalse(handler.Request.contains("checkContainer=c2,c1") || handler.Request.contains("checkContainer=c1,c2"));
        Assert.assertTrue(handler.Request.contains("f1="));
        Assert.assertFalse(handler.Request.contains("f1=free_text"));
        Assert.assertTrue(handler.Request.contains("r=from:200-to:300"));
        Assert.assertTrue(handler.Request.contains("cs1=x"));

    }

    private class ParameterFilterChannel implements IParameterHubListener, IFilterHubListener {

        public int Parameter;
        public int Filter;

        public ParameterFilterChannel() {
            Parameter = 0;
            Filter = 0;
        }

        @Override
        public void ParameterAdded(domain.filters.Filter filter) {
            System.out.println("ParameterFilterChannel->ParameterAdded:"+filter);
            this.Parameter++;
        }

        @Override
        public void ParameterRemoved(Filter filter) {
            System.out.println("ParameterFilterChannel->ParameterRemoved:"+filter);
            this.Parameter--;
        }

        @Override
        public void FilterAdded(Filter filter) {
            System.out.println("ParameterFilterChannel->RequestAdded:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterRemoved(Filter filter) {
            System.out.println("ParameterFilterChannel->RequestRemoved:"+filter);
            this.Filter--;
        }
    }

    private class FilterChannel1 implements IFilterHubListener{

        public int Filter;

        @Override
        public void FilterAdded(Filter filter) {
            System.out.println("FilterChannel->FilterAdded:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterRemoved(Filter filter) {
            System.out.println("FilterChannel->FilterRemoved:"+filter);
            Filter--;
        }
    }

    private class FilterChannel2 implements IFilterHubListener{

        public int Filter;

        @Override
        public void FilterAdded(Filter filter) {
            System.out.println("FilterChannel->FilterAdded:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterRemoved(Filter filter) {
            System.out.println("FilterChannel->FilterRemoved:"+filter);
            Filter--;
        }
    }

    private class CompleteChannel implements IFilterHubListener, IParameterHubListener, IRequestHubListener {

        public int Parameter;
        public int Request;
        public int Filter;

        public CompleteChannel() {
            this.Parameter = 0;
            this.Request = 0;
            this.Filter = 0;
        }

        @Override
        public void FilterAdded(Filter filter) {
            System.out.println("CompleteChannel->FilterAdded:"+filter);
            this.Filter++;
        }

        @Override
        public void FilterRemoved(Filter filter) {
            System.out.println("CompleteChannel->FilterRemoved:"+filter);
            this.Filter--;
        }

        @Override
        public void ParameterAdded(Filter filter) {
            System.out.println("CompleteChannel->ParameterAdded:"+filter);
            this.Parameter++;
        }

        @Override
        public void ParameterRemoved(Filter filter) {
            System.out.println("CompleteChannel->ParameterRemoved:"+filter);
            this.Parameter--;
        }

        @Override
        public void RequestAdded(Filter filter) {
            System.out.println("CompleteChannel->RequestAdded:"+filter);
            this.Request++;
        }

        @Override
        public void RequestRemoved(Filter filter) {
            System.out.println("CompleteChannel->RequestRemoved:"+filter);
            this.Request--;
        }
    }

    private class MockFilterController extends FilterController {

        protected MockFilterController(List<FilterContainer> containers, Hub hub, IRequestHandler receiver, IRequestConverter requestConverter) {
            super(containers, hub, receiver, requestConverter);
        }
    }

    private class MockContainer extends FilterContainer{

        protected MockContainer(String name) {
            super(name);
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
