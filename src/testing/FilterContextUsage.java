package testing;

import domain.FilterContext;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.hub.IFilterHubListener;
import domain.hub.IParameterHubListener;
import domain.hub.IRequestHubListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

public class FilterContextUsage {

    @Test
    public void testFilterContextUsage(){
        FilterContext context = new FilterContext();
        MockHandler handler = new MockHandler();
        MockRequestConverter converter = new MockRequestConverter();
        context.Initialize(handler, converter);

        FilterController controller = context.GetController();
        MockParameterComponent component1 = new MockParameterComponent(controller);
        MockCompleteComponent component2 = new MockCompleteComponent(controller );

    }

    private class MockCompleteComponent implements IParameterHubListener, IFilterHubListener, IRequestHubListener{
        public MockCompleteComponent(FilterController controller) {
            controller.GetHub().AddParameterListener(this);
            controller.GetHub().AddFilterListener(this);
            controller.GetHub().AddRequestListener(this);
        }

        @Override
        public void FilterAdded(Filter filter) {

        }

        @Override
        public void FilterRemoved(Filter filter) {

        }

        @Override
        public void ParameterAdded(Filter filter) {

        }

        @Override
        public void ParameterRemoved(Filter filter) {

        }

        @Override
        public void RequestAdded(Filter filter) {

        }

        @Override
        public void RequestRemoved(Filter filter) {

        }
    }

    private class MockParameterComponent implements IParameterHubListener{

        public MockParameterComponent(FilterController controller) {
            controller.GetHub().AddParameterListener(this);
        }

        @Override
        public void ParameterAdded(Filter filter) {

        }

        @Override
        public void ParameterRemoved(Filter filter) {

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
