package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IRequestHandler;
import domain.filters.types.CompositeFilter;
import domain.hub.IHub;
import domain.notifier.NotifierChannelType;
import domain.filters.Filter;
import domain.hub.Hub;
import domain.notifier.FilterNotifier;
import domain.notifier.ParameterNotifier;
import domain.notifier.RequestNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;
import testing.mocks.MockCompositeFilter;

import java.util.ArrayList;
import java.util.List;

public class QueringController {

    FilterContext context;

    @Before
    public void Setup(){
        context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new MockConfiguration());

        context.GetBuilder().Build(new MockBuilderItems(this.context.GetHub()));

    }

    @Test
    public void testGetFiltersByChannel(){
        List<Filter> filterChanneledFilters = this.context.GetController().GetFiltersByChannel(NotifierChannelType.FilterChannel);
        Assert.assertEquals(4, filterChanneledFilters.size());

        List<Filter> parameterChanneledFilters = this.context.GetController().GetFiltersByChannel(NotifierChannelType.ParameterChannel);
        Assert.assertEquals(3, parameterChanneledFilters.size());

        List<Filter> requestChanneledFilters = this.context.GetController().GetFiltersByChannel(NotifierChannelType.RequestChannel);
        Assert.assertEquals(2, requestChanneledFilters.size());

        context.Dispose();

    }

    private class MockBuilderItems extends BuilderItems{

        IHub hub;

        public MockBuilderItems(IHub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier filterChannel = new FilterNotifier(this.hub);
            ParameterNotifier parameterChannel = new ParameterNotifier(this.hub);
            RequestNotifier requestChannel = new RequestNotifier(this.hub);

            List<FilterContainer> containers  = new ArrayList<>();
            FilterContainer c1 = new FilterContainer(1, "c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", requestChannel);
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", filterChannel);
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3", parameterChannel);
            c1.AddFilter(f1);
            c1.AddFilter(f2);
            c1.AddFilter(f3);

            FilterContainer c2 = new FilterContainer(2, "c2");
            MockCheckBoxFilter f4 = new MockCheckBoxFilter(1, "f4", filterChannel);
            MockCheckBoxFilter f5 = new MockCheckBoxFilter(2, "f5", parameterChannel);
            MockCheckBoxFilter f6 = new MockCheckBoxFilter(3, "f6", filterChannel);
            c2.AddFilter(f4);
            c2.AddFilter(f5);
            c2.AddFilter(f6);

            FilterContainer c3 = new FilterContainer(3, "c3");
            MockCheckBoxFilter f7 = new MockCheckBoxFilter(1, "f7", filterChannel);

            MockCheckBoxFilter f8 = new MockCheckBoxFilter(2, "f8", parameterChannel);
            MockCheckBoxFilter f9 = new MockCheckBoxFilter(3, "f9", requestChannel);
            MockCompositeFilter compositeFilter = new MockCompositeFilter(2, "f10", filterChannel);
            compositeFilter.AddFilter(f8);
            compositeFilter.AddFilter(f9);
            c3.AddFilter(f7);
            c3.AddFilter(compositeFilter);

            containers.add(c1);
            containers.add(c2);
            containers.add(c3);
            return containers;
        }
    }

    private class MockConfiguration extends Configuration{

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
