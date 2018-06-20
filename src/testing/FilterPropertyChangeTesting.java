package testing;

import domain.buildins.UrlBuilder;
import domain.buildins.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IFilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.types.CheckBoxFilter;
import domain.hub.HubCommand;
import domain.hub.IFilterHubListener;
import domain.hub.IHub;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterPropertyChangeTesting {

    @Test
    public void testPropertyChange(){

        FilterContext context = new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        context.Initialize(handler, new UrlQueryConverter(new UrlBuilder(",", "&")), new MockConfiguration());
        IFilterController controller = context.GetController();
        IHub hub = context.GetHub();

        MockFilterHubListener listener = new MockFilterHubListener();
        hub.AddFilterListener(listener);

        context.GetBuilder().Build(new MockBuilderItems(context.GetHub()));

        HubCommand command = new HubCommand(1, "c1",1, "cc", -1, "0"); // for property changed Name
        HubCommand command2 = new HubCommand(1, "c1",1, "cc", 10, "0"); // for property changed Count
        HubCommand command3 = new HubCommand(1, "c1", 1,"cc", 10, "1"); // for statechanged
        HubCommand command4 = new HubCommand(1, "c1",1, "cc", 10, "0"); // for Reset

        Assert.assertEquals(0, listener.FilterChangedCounter);
        Assert.assertEquals(0, listener.FilterResetCounter);
        Assert.assertEquals(0, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(false, handler.MakeRequestTriggered);

        hub.Execute(command);
        Assert.assertEquals(0, listener.FilterChangedCounter);
        Assert.assertEquals(0, listener.FilterResetCounter);
        Assert.assertEquals(1, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(false, handler.MakeRequestTriggered);

        hub.Execute(command2);
        Assert.assertEquals(0, listener.FilterChangedCounter);
        Assert.assertEquals(0, listener.FilterResetCounter);
        Assert.assertEquals(2, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(false, handler.MakeRequestTriggered);

        hub.Execute(command3);
        Assert.assertEquals(1, listener.FilterChangedCounter);
        Assert.assertEquals(0, listener.FilterResetCounter);
        Assert.assertEquals(2, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(false, handler.MakeRequestTriggered);

        hub.Execute(command4);
        Assert.assertEquals(1, listener.FilterChangedCounter);
        Assert.assertEquals(1, listener.FilterResetCounter);
        Assert.assertEquals(2, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(false, handler.MakeRequestTriggered);

        context.Dispose();
    }

    private class MockBuilderItems extends BuilderItems {

        IHub hub;

        public MockBuilderItems(IHub hub){
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier filterNotifier = new FilterNotifier(hub);
            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer container = new FilterContainer(1,"c1");

            CheckBoxFilter checkBox = new MockCheckBoxFilter(1, "c1", filterNotifier);
            container.AddFilter(checkBox);

            containers.add(container);

            return containers;
        }
    }

    private class MockConfiguration extends Configuration{

        @Override
        public MissingContainerActionType getMissingContainerActionType() {
            return MissingContainerActionType.Nothing;
        }

        @Override
        public NewContainerActionType getNewContainerActionType() {
            return NewContainerActionType.AddFilters;
        }

        @Override
        public ExistingContainerActionType getExistingContainerActionType() {
            return ExistingContainerActionType.Nothing;
        }
    }

    private class MockFilterHubListener implements IFilterHubListener {

        public int FilterChangedCounter = 0;
        public int FilterResetCounter = 0;
        public int FilterPropertyChangedCounter = 0;

        @Override
        public void FilterChanged(Filter filter) {
            FilterChangedCounter++;
        }

        @Override
        public void FilterReset(Filter filter) {
            FilterResetCounter++;
        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
            FilterPropertyChangedCounter++;
        }

        @Override
        public void FilterUpdated(Filter filter) {

        }
    }

    private class MockRequestHandler implements IRequestHandler {

        public boolean MakeRequestTriggered;

        @Override
        public void makeRequest(String request) {
            this.MakeRequestTriggered = true;
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
}
