package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.*;
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
        FilterContext filterContext = new FilterContext();
        filterContext.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new MockConfiguration());
        IFilterController controller = filterContext.GetController();// new MockController( hub, new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")));
        Builder builder = filterContext.GetBuilder();
        builder.Build(new MockBuilderItems(filterContext.GetHub()));

        Assert.assertEquals(2, controller.GetContainers().size());
        Assert.assertEquals("c1", controller.GetContainers().get(0).GetName());
        Assert.assertEquals("c2", controller.GetContainers().get(1).GetName());

        Assert.assertEquals(2, controller.GetContainers().get(0).GetFilters().size());
        Assert.assertEquals(1, controller.GetContainers().get(1).GetFilters().size());

        Assert.assertEquals("c1", controller.GetContainers().get(0).GetFilters().get(0).getName());
        Assert.assertEquals("c2", controller.GetContainers().get(0).GetFilters().get(1).getName());

        Assert.assertEquals("r1", controller.GetContainers().get(1).GetFilters().get(0).getName());
        filterContext.Dispose();
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

    private class MockBuilderItems extends BuilderItems
    {
        List<FilterContainer> containers ;
        Hub hub;

        public MockBuilderItems(Hub hub) {
            this.hub = hub;
            this.containers = new ArrayList<>();
            this.containers.add(new FilterContainer(1,"c1"));
            this.containers.add(new FilterContainer(2,"c2"));

            CheckBoxFilter checkBoxFilter1 = new MockCheckBoxFilter(1, "c1", new FilterNotifier(this.hub) );
            CheckBoxFilter checkBoxFilter2 = new MockCheckBoxFilter(2 ,"c2", new FilterNotifier(this.hub) );
            this.containers.get(0).AddFilter(checkBoxFilter1);
            this.containers.get(0).AddFilter(checkBoxFilter2);

            MockCheckBoxFilter range = new MockCheckBoxFilter(3, "r1", new ParameterNotifier(this.hub));
            this.containers.get(1).AddFilter(range);
        }

        @Override
        public List<FilterContainer> GetContainers() {
            return this.containers;
        }
    }

    private class MockBuilderItems2 extends BuilderItems {

        @Override
        public List<FilterContainer> GetContainers() {
            return null;
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
