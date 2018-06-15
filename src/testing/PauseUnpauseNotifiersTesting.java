package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.channelmanipulation.ChannelManipulator;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IFilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.*;
import domain.notifier.NotifierChannelType;
import org.junit.Assert;
import org.junit.Test;
import testing.builderItems.MockBuilderItemsThreeContainersWithComposite;

import java.util.ArrayList;
import java.util.List;

public class PauseUnpauseNotifiersTesting {

    @Test
    public void testHubFilterChannelsPauseUnpause(){
        FilterContext context = new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        context.Initialize(handler, new UrlQueryConverter(new UrlBuilder(",", "&")), new FullConfiguration());
        IFilterController controller = context.GetController();

        IHub hub = context.GetHub();

        ChannelManipulator channelManipulator = context.GetChannelmanipulator();
        channelManipulator.PauseRequestHandlerChannel();

        RequestChannel requestChannel = new RequestChannel();
        ParameterChannel parameterChannel = new ParameterChannel();
        FilterChannel filterChannel = new FilterChannel();
        ResultChannel resultChannel = new ResultChannel();

        hub.AddFilterListener(filterChannel);
        hub.AddParameterListener(parameterChannel);
        hub.AddRequestListener(requestChannel);
        hub.AddResultListener(resultChannel);

        MockBuilderItemsThreeContainersWithComposite items = new MockBuilderItemsThreeContainersWithComposite(context.GetHub());
        context.GetBuilder().Build(items);

        controller.ChangeState(1, 1, "1");
        Assert.assertTrue(filterChannel.Fired);
        filterChannel.Fired = false;

        channelManipulator.PauseChannel(NotifierChannelType.FilterChannel);
        channelManipulator.PauseChannel(NotifierChannelType.ParameterChannel);
        controller.ChangeState(1, 1, "0");
        Assert.assertFalse(filterChannel.Fired);
        Assert.assertFalse(handler.Fired);

        channelManipulator.UnPauseChannel(NotifierChannelType.FilterChannel);
        controller.ChangeState(1, 1, "1");
        Assert.assertTrue(filterChannel.Fired);

        filterChannel.Fired = false;
        parameterChannel.Fired = false;
        requestChannel.Fired = false;

        channelManipulator.PauseAllFilterChannels();

        controller.ChangeState(2, 1, "1:asd");
        controller.ChangeState(2, 1, "2:40");
        controller.ChangeState(2, 2, "1");
        controller.ChangeState(3, 1, "from:10-to:40");
        controller.ChangeState(3, 2, "free_text");
        Assert.assertFalse(filterChannel.Fired);
        Assert.assertFalse(parameterChannel.Fired);
        Assert.assertFalse(requestChannel.Fired);

        channelManipulator.UnPauseAllFilterChannels();
        controller.ChangeState(2, 1, "1:asdd");
        controller.ChangeState(2, 1, "2:20");
        controller.ChangeState(2, 2, "0");
        controller.ChangeState(3, 1, "from:20-to:50");
        controller.ChangeState(3, 2, "free_text2");
        Assert.assertTrue(filterChannel.Fired);
        Assert.assertTrue(parameterChannel.Fired);
        Assert.assertTrue(requestChannel.Fired);

        String req = controller.GetCurrentConvertedRequest();
        // the current state is in sync with the filter changes
        Assert.assertTrue( req.contains("f1From=from:20&f1To=to:50&f1=asdd&f2=20,free_text2&c1=f1") || req.contains("f1From=from:20&f1To=to:50&f1=asdd&f2=free_text2,20&c1=f1"));

        Assert.assertFalse(handler.Fired);

        channelManipulator.UnPauseRequestHandlerChannel();

        controller.ChangeState(1,3, "free_ttt");
        Assert.assertTrue(handler.Fired);
        req = controller.GetCurrentConvertedRequest();
        // the current state is in sync with the filter changes
        Assert.assertTrue(req.contains("f1From=from:20&f1To=to:50&f1=asdd&f2=20,free_text2&f3=free_ttt&c1=f1") || req.contains("f1From=from:20&f1To=to:50&f1=asdd&f2=free_text2,20&f3=free_ttt&c1=f1"));

        context.Dispose();
    }

    private class RequestChannel implements IRequestHubListener {
        public boolean Fired = false;
        @Override
        public void RequestChanged(Filter filter) {
            Fired = true;
        }

        @Override
        public void RequestReset(Filter filter) {
            Fired = true;
        }

        @Override
        public void RequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {
            Fired = true;
        }

        @Override
        public void RequestUpdated(Filter filter) {
            Fired = true;
        }
    }

    private class ParameterChannel implements IParameterHubListener{
        public boolean Fired = true;
        @Override
        public void ParameterChanged(Filter filter) {
            Fired = true;
        }

        @Override
        public void ParameterReset(Filter filter) {
            Fired = true;
        }

        @Override
        public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {
            Fired = true;
        }

        @Override
        public void ParameterUpdated(Filter filter) {
            Fired = true;
        }
    }

    private class FilterChannel implements IFilterHubListener{
        public boolean Fired = false;

        @Override
        public void FilterChanged(Filter filter) {
            Fired = true;
        }

        @Override
        public void FilterReset(Filter filter) {
            Fired = true;
        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
            Fired = true;
        }

        @Override
        public void FilterUpdated(Filter filter) {
            Fired = true;
        }
    }

    private class ResultChannel implements IResultHubListener{
        public boolean Fired = false;
        @Override
        public void ResultReceived(Object result) {
            Fired = true;
        }
    }

    private class MockBuilderItems extends BuilderItems{

        @Override
        public List<FilterContainer> GetContainers() {
            List<FilterContainer> containers = new ArrayList<>();

            FilterContainer c1 = new FilterContainer(1, "c1");

            return containers;
        }
    }

    private class FullConfiguration extends Configuration{

        @Override
        public MissingContainerActionType getMissingContainerActionType() {
            return MissingContainerActionType.RemoveFilters;
        }

        @Override
        public NewContainerActionType getNewContainerActionType() {
            return NewContainerActionType.AddFilters;
        }

        @Override
        public ExistingContainerActionType getExistingContainerActionType() {
            return ExistingContainerActionType.AddRemoveAndUpdate;
        }
    }

    private class MockRequestHandler implements IRequestHandler{

        public boolean Fired = false;

        @Override
        public void makeRequest(String request) {
            Fired = true;
        }

        @Override
        public void Initialize(String request) {

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
