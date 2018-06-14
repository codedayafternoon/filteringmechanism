package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IFilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filters.INotifier;
import domain.filters.types.RangeFilter;
import domain.hub.Hub;
import domain.hub.IHub;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.MockRangeFilter;

import java.util.ArrayList;
import java.util.List;

public class OveridingParametersKeyValuesInFilters {

    FilterContext context;
    FilterNotifier notifier;

    MockRequestHandler handler;

    @Before
    public void Setup(){
        this.context = new FilterContext();
        this.handler = new MockRequestHandler();
        this.context.Initialize(this.handler, new UrlQueryConverter(new UrlBuilder(",", "&")), new MockConfiguration());
    }

    @Test
    public void testParameterOverriding(){
        MockBuilderItems builderItems = new MockBuilderItems(this.context.GetHub());
        this.context.GetBuilder().Build(builderItems);

        IFilterController controller = this.context.GetController();
        controller.ChangeState(1, 1, "from:300-to:500");

        Assert.assertTrue( this.handler.Request.contains("priceMin=300&priceMax=500"));

        this.context.Dispose();
    }

    private class MockRequestHandler implements IRequestHandler{
        public String Request;

        @Override
        public void makeRequest(String request) {
            this.Request = request;
            System.out.println("MockRequestHandler->makeRequest:" + request);
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

    private class MockConfiguration extends Configuration {

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

    private class MockBuilderItems extends BuilderItems{

        IHub hub;

        public MockBuilderItems(IHub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            List<FilterContainer> containers = new ArrayList<>();

            FilterNotifier notifier = new FilterNotifier(this.hub);

            FilterContainer c = new FilterContainer(1, "c1");
            List<String> rangeFromValues = new ArrayList<>();
            rangeFromValues.add("200");
            rangeFromValues.add("300");
            rangeFromValues.add("400");
            List<String> rangeToValues = new ArrayList<>();
            rangeToValues.add("300");
            rangeToValues.add("400");
            rangeToValues.add("500");

            MockRangeFilterCustom rangeFilter = new MockRangeFilterCustom(1,"price", notifier, rangeFromValues, rangeToValues);
            c.AddFilter(rangeFilter);

            containers.add(c);
            return containers;
        }
    }

    private class MockRangeFilterCustom extends MockRangeFilter {

        public MockRangeFilterCustom(Object id, String name, INotifier notifier, List<String> from, List<String> to) {
            super(id, name, notifier, from, to);
        }

        @Override
        public String GetParameterKeyFrom(){
            return this.Name + "Min";
        }

        @Override
        public String GetParameterKeyTo() {
            return super.Name + "Max";
        }

//        @Override
//        protected String GetIntermediateSymbol() {
//            return "&";
//        }

        @Override
        protected String EncodeParameterValueFrom(String from){
            return from;
        }

        @Override
        protected String EncodeParameterValueTo(String to){
            return to;
        }
    }
}
