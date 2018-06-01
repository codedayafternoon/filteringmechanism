package testing;

import domain.FilterContext;
import domain.configuration.Configuration;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.Hub;
import domain.hub.IFilterHubListener;
import domain.hub.IParameterHubListener;
import domain.hub.IRequestHubListener;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockFreeTextFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FilterContextUsage {

    @Test
    public void testFilterContextUsage(){
        FilterContext context = new FilterContext();
        MockHandler handler = new MockHandler();
        MockRequestConverter converter = new MockRequestConverter();
        context.Initialize(handler, converter);

        context.GetBuilder().Build(new MockConfiguration(context.GetHub()));

        FilterController controller = context.GetController();
        MockParameterComponent component1 = new MockParameterComponent(controller);
        MockCompleteComponent component2 = new MockCompleteComponent(controller );

        Assert.assertEquals(1, context.GetController().GetContainers().size());
        Assert.assertEquals(1, context.GetController().GetContainers().get(0).GetFilters().size());
        Assert.assertEquals("f1", context.GetController().GetContainers().get(0).GetFilters().get(0).getName());

    }


    private class MockConfiguration extends Configuration{

        Hub hub;
        public MockConfiguration(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            List<FilterContainer> res = new ArrayList<>();
            FilterContainer c1 = new FilterContainer("c1");
            MockFreeTextFilter f1 = new MockFreeTextFilter(1, "f1", new FilterNotifier(this.hub));
            c1.AddFilter(f1);
            res.add(c1);
            return res;
        }
    }

    private class MockCompleteComponent implements IParameterHubListener, IFilterHubListener, IRequestHubListener{
        public MockCompleteComponent(FilterController controller) {
            controller.GetHub().AddParameterListener(this);
            controller.GetHub().AddFilterListener(this);
            controller.GetHub().AddRequestListener(this);
        }

        @Override
        public void FilterChanged(Filter filter) {

        }

        @Override
        public void FilterReset(Filter filter) {

        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void ParameterChanged(Filter filter) {

        }

        @Override
        public void ParameterReset(Filter filter) {

        }

        @Override
        public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

        }

        @Override
        public void RequestChanged(Filter filter) {

        }

        @Override
        public void RequestReset(Filter filter) {

        }

        @Override
        public void RequestPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

        }
    }

    private class MockParameterComponent implements IParameterHubListener{

        public MockParameterComponent(FilterController controller) {
            controller.GetHub().AddParameterListener(this);
        }

        @Override
        public void ParameterChanged(Filter filter) {

        }

        @Override
        public void ParameterReset(Filter filter) {

        }

        @Override
        public void ParameterPropertyChanged(Filter filter, String old, String aNew, FilterPropertyType propType) {

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

    private class MockHandler implements IRequestHandler{

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
}
