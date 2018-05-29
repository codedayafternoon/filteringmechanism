package testing;

import domain.FilterContext;
import domain.configuration.Configuration;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.ICountable;
import domain.hub.Hub;
import domain.hub.HubCommand;
import domain.hub.IResultHubListener;
import domain.hub.results.IResult;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ResultTesting {

    private FilterContext context;

    private FilterContainer container1;
    private MockCheckBoxFilter checkBox1;
    private MockCheckBoxFilter checkBox2;

    private FilterContainer container2;
    private MockCheckBoxFilter checkBox3;
    private MockCheckBoxFilter checkBox4;

    FilterNotifier notifier;

    MockRequestHandler requestHandler;
    MockRequestConverter requestConverter;

    MockResultListenerModule resultModule;

    public void Setup(){
        this.context = new FilterContext();
        this.requestHandler = new MockRequestHandler(this.context.GetHub());
        this.requestConverter = new MockRequestConverter();
        this.context.Initialize(this.requestHandler, this.requestConverter);

        this.resultModule = new MockResultListenerModule(this.context.GetController());
        this.requestHandler = new MockRequestHandler(this.context.GetHub());

        this.notifier = new FilterNotifier(this.context.GetHub());
        this.container1 = new FilterContainer("c1");
        this.checkBox1 = new MockCheckBoxFilter(1, "f1", this.notifier );
        this.checkBox2 = new MockCheckBoxFilter(2, "f2", this.notifier );
        this.container1.AddFilter(checkBox1);
        this.container1.AddFilter(checkBox2);

        this.container2 = new FilterContainer("c2");
        this.checkBox3 = new MockCheckBoxFilter(3, "f3", this.notifier);
        this.checkBox4 = new MockCheckBoxFilter(4, "f4", this.notifier);
        this.container2.AddFilter(this.checkBox3);
        this.container2.AddFilter(this.checkBox4);


    }

    @Test
    public void testPassResultToHub(){
        this.Setup();
        context = new FilterContext();

        MockConfiguration configuration = new MockConfiguration();
        context.GetBuilder().Build(configuration);

        Assert.assertEquals(2, this.context.GetController().GetContainers().size());
        Assert.assertEquals(2, this.context.GetController().GetContainers().get(0).GetFilters().size());
        Assert.assertEquals(2, this.context.GetController().GetContainers().get(1).GetFilters().size());

        this.requestHandler.TriggerResultReceived(1);
        Assert.assertEquals(1, this.resultModule.Result);
        // nothing cahnged in filters
        Assert.assertEquals(false, this.checkBox1.IsChecked());
        Assert.assertEquals(false, this.checkBox2.IsChecked());
        Assert.assertEquals(false, this.checkBox3.IsChecked());
        Assert.assertEquals(false, this.checkBox4.IsChecked());

        Assert.assertEquals(0, this.checkBox1.GetCount());
        Assert.assertEquals(0, this.checkBox2.GetCount());
        Assert.assertEquals(0, this.checkBox3.GetCount());
        Assert.assertEquals(0, this.checkBox4.GetCount());


       // HubCommand command1 = new HubCommand("c1", "f1", 10, "1" );
    }



    private class MockResultListenerModule implements IResultHubListener{

        public Object Result;

        public MockResultListenerModule(FilterController controller) {
            controller.GetHub().AddResultListener(this);
        }

        @Override
        public void ResultReceived(Object result) {
            System.out.println("MockResultListenerModule->ResultReceived:" + result);
            this.Result = result;
        }
    }

    private class MockRequestConverter implements IRequestConverter{

        @Override
        public String Convert(Map<Filter, Date> items) {
            return null;
        }

        @Override
        public void AddCustomParameter(String paramName, String value) {

        }

        @Override
        public void RemoveCustomParameter(String paramName, String value) {

        }

        @Override
        public void RemoveCustomParameter(String paramName) {

        }
    }

    private class MockRequestHandler implements IRequestHandler{

        Hub hub;

        public MockRequestHandler(Hub hub) {
            this.hub = hub;
        }

        public void TriggerResultReceived(Object obj){

            MockResult result = new MockResult(null,obj,null);
            this.hub.ResultReceived(result);
        }

        public void TriggerResultReceivedWithHubCommands(Object obj, List<HubCommand> commands){
            MockResult result = new MockResult(null,obj,commands);
            this.hub.ResultReceived(result);
        }

        @Override
        public void makeRequest(String request) {

        }

        @Override
        public void Initialize(String request) {

        }

        @Override
        public boolean IsRetrieveFromRequest() {
            return false;
        }

        @Override
        public boolean IsRetrieveFromParameters() {
            return false;
        }

        @Override
        public boolean IsRetrieveFromFilters() {
            return false;
        }
    }

    private class MockResult implements IResult{

        List<Filter> filters;
        Object result;
        List<HubCommand> commands;

        public MockResult(List<Filter> filters, Object result, List<HubCommand> commands) {
            this.filters = filters;
            this.result = result;
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

    private class MockConfiguration extends Configuration{

        @Override
        public List<FilterContainer> GetContainers() {
            List<FilterContainer> containers = new ArrayList<>();
            containers.add(container1);
            containers.add(container2);
            return containers;
        }
    }
}
