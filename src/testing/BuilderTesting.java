package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.configuration.Builder;
import domain.configuration.Configuration;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.types.CheckBoxFilter;
import domain.filters.types.RangeFilter;
import domain.hub.Hub;
import domain.notifier.FilterNotifier;
import domain.notifier.ParameterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;
import testing.mocks.MockRangeFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimfi on 5/28/2018.
 */
public class BuilderTesting {

    Hub hub = new Hub();

    @Test
    public void testBuilder(){

        FilterController controller = new MockController( hub, new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")));
        Builder builder = controller.GetBuilder();
        builder.Build(new MockConfiguration(controller));

        Assert.assertEquals(2, controller.GetContainers().size());
        Assert.assertEquals("c1", controller.GetContainers().get(0).GetName());
        Assert.assertEquals("c2", controller.GetContainers().get(1).GetName());

        Assert.assertEquals(2, controller.GetContainers().get(0).GetFilters().size());
        Assert.assertEquals(1, controller.GetContainers().get(1).GetFilters().size());

        Assert.assertEquals("c1", controller.GetContainers().get(0).GetFilters().get(0).Name);
        Assert.assertEquals("c2", controller.GetContainers().get(0).GetFilters().get(1).Name);

        Assert.assertEquals("r1", controller.GetContainers().get(1).GetFilters().get(0).Name);
    }

    private class MockConfiguration extends Configuration
    {
        List<FilterContainer> containers ;

        public MockConfiguration(FilterController controller) {
            this.containers = new ArrayList<>();
            this.containers.add(new FilterContainer("c1"));
            this.containers.add(new FilterContainer("c2"));

            CheckBoxFilter checkBoxFilter1 = new MockCheckBoxFilter(1, "c1", new FilterNotifier(controller.GetHub()) );
            CheckBoxFilter checkBoxFilter2 = new MockCheckBoxFilter(2 ,"c2", new FilterNotifier(controller.GetHub()) );
            this.containers.get(0).AddFilter(checkBoxFilter1);
            this.containers.get(0).AddFilter(checkBoxFilter2);

            RangeFilter range = new MockRangeFilter(3, "r1", new ParameterNotifier(controller.GetHub()));
            this.containers.get(1).AddFilter(range);
        }

        @Override
        public List<FilterContainer> GetContainers() {
            return this.containers;
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

    private class MockController extends FilterController{

        protected MockController( Hub hub, IRequestHandler handler, IRequestConverter requestConverter) {
            super( hub, handler, requestConverter);
        }
    }
}
