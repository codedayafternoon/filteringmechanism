package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IFilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.Hub;
import domain.hub.IFilterHubListener;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.*;

import java.util.ArrayList;
import java.util.List;

public class BuilderActionsTest {

    @Test
    public void ResetAllFiltersWithoutPropagateRequest(){
        FilterContext context = new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        context.Initialize(handler, new UrlQueryConverter(new UrlBuilder(",", "&")), new FullConfiguration());
        MockBuilderObserver builderObserver = new MockBuilderObserver();

        MockBuilderItems3 items = new MockBuilderItems3(context.GetHub());
        context.GetBuilder().AddObserver(builderObserver);
        context.GetBuilder().Build(items);

        MockFilterHubListener listener = new MockFilterHubListener();
        context.GetHub().AddFilterListener(listener);

        // change states in various filters
        IFilterController controller = context.GetController();
        controller.ChangeState(1, 1, "1");
        controller.ChangeState(1, 2, "from:z_from-to:z_to");
        controller.ChangeState(1, 3, "asd");

        controller.ChangeState(2, 1, "eee");
        controller.ChangeState(2, 2, "y_from");

        controller.ChangeState(3, 1, "1");
        controller.ChangeState(3, 2, "1");
        controller.ChangeState(3, 3, "1");

        String req = handler.Request;
        Assert.assertTrue(req.contains("c3=f3,f2") || req.contains("c3=f2,f3"));
        Assert.assertTrue(req.contains("f1=eee"));
        Assert.assertTrue(req.contains("f2range=from:z_from-to:z_to"));
        Assert.assertTrue(req.contains("f2=y_from"));
        Assert.assertTrue(req.contains("f3=asd"));
        Assert.assertTrue(req.contains("c1=f1"));

        Assert.assertEquals(8, listener.ChangedCount);
        Assert.assertEquals(1, listener.ResetCount);

        Assert.assertEquals(8, handler.RequestCount);
        controller.ResetAllWithoutRequestPropagation();



        // check if all filters are reset

        // if events are fired in listener
        Assert.assertEquals(8, listener.ChangedCount);
        Assert.assertEquals(8, listener.ResetCount);
        // check if request didnt happen
        Assert.assertEquals(8, handler.RequestCount);

        controller.MakeRequestWithCurrentState();
        Assert.assertEquals("", handler.Request);
        Assert.assertEquals(9, handler.RequestCount);

        context.Dispose();
    }

    private class MockBuilderItems3 extends BuilderItems{

        Hub hub;

        public MockBuilderItems3(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();

            FilterContainer c1 = new FilterContainer(1, "c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
            List<String> f2FromValues = new ArrayList<>();
            f2FromValues.add("x_from");
            f2FromValues.add("y_from");
            f2FromValues.add("z_from");
            List<String> f2ToValues = new ArrayList<>();
            f2ToValues.add("x_to");
            f2ToValues.add("y_to");
            f2ToValues.add("z_to");
            MockRangeFilter f2 = new MockRangeFilter(2, "f2range", notifier, f2FromValues,f2ToValues );
            f2.SetDefaultTo("y_to");
            f2.SetDefaultFrom("z_from");
            MockFreeTextFilter f3 = new MockFreeTextFilter(3, "f3", notifier);
            f3.SetDefaultValue("");
            c1.AddFilter(f1);
            c1.AddFilter(f2);
            c1.AddFilter(f3);

            FilterContainer c2 = new FilterContainer(2, "c2");
            MockFreeTextFilter c2f1 = new MockFreeTextFilter(1, "f1", notifier);
            c2f1.SetDefaultValue("");
            List<String> c2f2Values = new ArrayList<>();
            c2f2Values.add("x_from");
            c2f2Values.add("y_from");
            c2f2Values.add("z_from");
            MockSingleTextFilter c2f2 = new MockSingleTextFilter(2, "f2", notifier, c2f2Values);
            c2f2.SetDefaultValue("x_from");
            c2.AddFilter(c2f1);
            c2.AddFilter(c2f2);

            FilterContainer c3 = new FilterContainer(3, "c3");
            MockSingleSelectFilter c3f1 = new MockSingleSelectFilter(c3,1, "f1", notifier);
            MockSingleSelectFilter c3f2 = new MockSingleSelectFilter(c3,2, "f2", notifier);
            MockCheckBoxFilter c3f3 = new MockCheckBoxFilter(3, "f3", notifier);
            c3.AddFilter(c3f1);
            c3.AddFilter(c3f2);
            c3.AddFilter(c3f3);

            containers.add(c1);
            containers.add(c2);
            containers.add(c3);
            return containers;
        }
    }

    @Test
    public void DetectAndAddNewContainersAndFilters(){
        FilterContext context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new OnlyAddNewContainerFiltersConfiguration());

        MockBuilderObserver builderObserver = new MockBuilderObserver();

        MockBuilderItems items = new MockBuilderItems(context.GetHub());
        context.GetBuilder().AddObserver(builderObserver);
        context.GetBuilder().Build(items);

        Assert.assertEquals(2, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(6, builderObserver.FilterAddedCounter);
        Assert.assertEquals(0, builderObserver.FilterRemovedCounter);

        context.Dispose();

    }

    @Test
    public void DetectAndRemoveContainerAndFilters(){
        FilterContext context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new AddNewContainerFiltersAndRemoveMissingContainerFiltersConfiguration());

        MockBuilderObserver builderObserver = new MockBuilderObserver();

        MockBuilderItems items = new MockBuilderItems(context.GetHub());
        context.GetBuilder().AddObserver(builderObserver);
        context.GetBuilder().Build(items);

        Assert.assertEquals(2, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(6, builderObserver.FilterAddedCounter);
        Assert.assertEquals(0, builderObserver.FilterRemovedCounter);

        Assert.assertTrue(context.GetController().GetContainers().get(0).GetId().equals(1) || context.GetController().GetContainers().get(0).GetId().equals(2));
        Assert.assertTrue(context.GetController().GetContainers().get(1).GetId().equals(1) || context.GetController().GetContainers().get(1).GetId().equals(2));

        MockBuilderItems2 items2 = new MockBuilderItems2(context.GetHub());
        context.GetBuilder().Build(items2);
        // container with id 1 must be removed, and container with id 10 must be added
        Assert.assertEquals(3, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(1, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(9, builderObserver.FilterAddedCounter);
        Assert.assertEquals(3, builderObserver.FilterRemovedCounter);

        Assert.assertEquals(2, context.GetController().GetContainers().size());
        Assert.assertEquals(3, context.GetController().GetContainers().get(0).GetFilters().size());
        Assert.assertEquals(3, context.GetController().GetContainers().get(1).GetFilters().size());
        Assert.assertTrue(context.GetController().GetContainers().get(0).GetId().equals(10) || context.GetController().GetContainers().get(0).GetId().equals(2));
        Assert.assertTrue(context.GetController().GetContainers().get(1).GetId().equals(10) || context.GetController().GetContainers().get(1).GetId().equals(2));

        context.Dispose();
    }

    @Test
    public void testAddRemoveFiltersFromContainer(){
        FilterContext context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new FullConfiguration());

        MockBuilderObserver builderObserver = new MockBuilderObserver();
        context.GetBuilder().AddObserver(builderObserver);

        MockItemsOneContainerBefore items = new MockItemsOneContainerBefore(context.GetHub());

        context.GetBuilder().Build(items);
        Assert.assertEquals(1, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(3, builderObserver.FilterAddedCounter);
        Assert.assertEquals(0, builderObserver.FilterRemovedCounter);

        MockItemsOneContainerAfter itemsAfter = new MockItemsOneContainerAfter(context.GetHub());

        context.GetBuilder().Build(itemsAfter);
        Assert.assertEquals(1, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(4, builderObserver.FilterAddedCounter);
        Assert.assertEquals(1, builderObserver.FilterRemovedCounter);

        context.Dispose();

    }

    private class MockItemsOneContainerBefore extends BuilderItems{

        Hub hub;

        public MockItemsOneContainerBefore(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c = new FilterContainer(1, "c");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", notifier);
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3", notifier);
            c.AddFilter(f1);
            c.AddFilter(f2);
            c.AddFilter(f3);
            containers.add(c);
            return containers;
        }
    }

    private class MockItemsOneContainerAfter extends BuilderItems{

        Hub hub;

        public MockItemsOneContainerAfter(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c = new FilterContainer(1, "c");
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", notifier);
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3", notifier);
            MockCheckBoxFilter f4 = new MockCheckBoxFilter(4, "f4", notifier);
            c.AddFilter(f2);
            c.AddFilter(f3);
            c.AddFilter(f4);
            containers.add(c);
            return containers;
        }
    }

    @Test
    public void testAddRemoveFiltersFromExistingContainers(){
        FilterContext context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new FullConfiguration());

        MockBuilderObserver builderObserver = new MockBuilderObserver();
        context.GetBuilder().AddObserver(builderObserver);

        MockBuilderItemsBefore items = new MockBuilderItemsBefore(context.GetHub());

        context.GetBuilder().Build(items);
        Assert.assertEquals(3, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(7, builderObserver.FilterAddedCounter);
        Assert.assertEquals(0, builderObserver.FilterRemovedCounter);

        MockBuilderItemsAfter itemsAfter = new MockBuilderItemsAfter(context.GetHub());

        context.GetBuilder().Build(itemsAfter);
        Assert.assertEquals(4, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(10, builderObserver.FilterAddedCounter);
        Assert.assertEquals(3, builderObserver.FilterRemovedCounter);

        context.Dispose();
    }

    private class MockBuilderItemsBefore extends BuilderItems{

        Hub hub;

        public MockBuilderItemsBefore(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);

            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c1 = new FilterContainer(1, "c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1",notifier );
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2",notifier );
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3",notifier );
            c1.AddFilter(f1);
            c1.AddFilter(f2);
            c1.AddFilter(f3);
            FilterContainer c2 = new FilterContainer(2, "c2");
            MockCheckBoxFilter f1_2 = new MockCheckBoxFilter(1, "f1",notifier );
            MockCheckBoxFilter f2_2 = new MockCheckBoxFilter(2, "f2",notifier );
            c2.AddFilter(f1_2);
            c2.AddFilter(f2_2);
            FilterContainer c3 = new FilterContainer(3, "c3");
            MockCheckBoxFilter f1_3 = new MockCheckBoxFilter(1, "f1",notifier );
            MockCheckBoxFilter f2_3 = new MockCheckBoxFilter(2, "f2",notifier );
            c3.AddFilter(f1_3);
            c3.AddFilter(f2_3);
            containers.add(c1);
            containers.add(c2);
            containers.add(c3);
            return containers;
        }
    }

    private class MockBuilderItemsAfter extends BuilderItems{

        Hub hub;

        public MockBuilderItemsAfter(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);

            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c1 = new FilterContainer(1, "c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1",notifier );
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3",notifier );
            MockCheckBoxFilter f4 = new MockCheckBoxFilter(4, "f4",notifier );
            c1.AddFilter(f1);
            c1.AddFilter(f3);
            c1.AddFilter(f4);
            FilterContainer c2 = new FilterContainer(2, "c2");
            MockCheckBoxFilter f1_2 = new MockCheckBoxFilter(1, "f1",notifier );
            MockCheckBoxFilter f2_2 = new MockCheckBoxFilter(2, "f2",notifier );
            c2.AddFilter(f1_2);
            c2.AddFilter(f2_2);
            FilterContainer c3 = new FilterContainer(3, "c3");
            MockCheckBoxFilter f3_3 = new MockCheckBoxFilter(3, "f3",notifier );
            c3.AddFilter(f3_3);
            FilterContainer c4 = new FilterContainer(4, "c4");
            MockCheckBoxFilter f5_4 = new MockCheckBoxFilter(5, "f5",notifier );
            c4.AddFilter(f5_4);
            containers.add(c1);
            containers.add(c2);
            containers.add(c3);
            containers.add(c4);
            return containers;
        }
    }

    private class MockBuilderItems2 extends BuilderItems{

        Hub hub;

        public MockBuilderItems2(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c1 = new FilterContainer(10,"c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", notifier);
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3", notifier);
            c1.AddFilter(f1);
            c1.AddFilter(f2);
            c1.AddFilter(f3);
            containers.add(c1);

            FilterContainer c2 = new FilterContainer(2,"c2");
            MockCheckBoxFilter f4 = new MockCheckBoxFilter(4, "f4", notifier);
            MockCheckBoxFilter f5 = new MockCheckBoxFilter(5, "f5", notifier);
            MockCheckBoxFilter f6 = new MockCheckBoxFilter(6, "f6", notifier);
            c2.AddFilter(f4);
            c2.AddFilter(f5);
            c2.AddFilter(f6);
            containers.add(c2);

            return containers;
        }
    }

    private class MockBuilderItems extends BuilderItems{

        Hub hub;

        public MockBuilderItems(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();
            FilterContainer c1 = new FilterContainer(1,"c1");
            MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
            MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", notifier);
            MockCheckBoxFilter f3 = new MockCheckBoxFilter(3, "f3", notifier);
            c1.AddFilter(f1);
            c1.AddFilter(f2);
            c1.AddFilter(f3);
            containers.add(c1);

            FilterContainer c2 = new FilterContainer(2,"c2");
            MockCheckBoxFilter f4 = new MockCheckBoxFilter(4, "f4", notifier);
            MockCheckBoxFilter f5 = new MockCheckBoxFilter(5, "f5", notifier);
            MockCheckBoxFilter f6 = new MockCheckBoxFilter(6, "f6", notifier);
            c2.AddFilter(f4);
            c2.AddFilter(f5);
            c2.AddFilter(f6);
            containers.add(c2);

            return containers;
        }
    }

    @Test
    public void testComplexUseCase(){
        FilterContext context = new FilterContext();
        context.Initialize(new MockRequestHandler(), new UrlQueryConverter(new UrlBuilder(",", "&")), new FullConfiguration());

        MockFilterHubListener filterHubListener = new MockFilterHubListener();
        context.GetHub().AddFilterListener(filterHubListener);

        MockBuilderObserver builderObserver = new MockBuilderObserver();
        context.GetBuilder().AddObserver(builderObserver);

        MockBuilderItemsUseCase1Before items = new MockBuilderItemsUseCase1Before(context.GetHub());
        context.GetBuilder().Build(items);

        Assert.assertEquals(3, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(8, builderObserver.FilterAddedCounter);
        Assert.assertEquals(0, builderObserver.FilterRemovedCounter);
        Assert.assertEquals(0, filterHubListener.Update);

        Assert.assertEquals(3, context.GetController().GetContainers().size());
        Assert.assertEquals("c1", context.GetController().GetContainers().get(0).GetName());
        Assert.assertEquals("c2", context.GetController().GetContainers().get(1).GetName());
        Assert.assertEquals("c3", context.GetController().GetContainers().get(2).GetName());
        Assert.assertEquals("f1", context.GetController().GetContainers().get(0).GetFilters().get(0).getName());
        Assert.assertEquals("f2", context.GetController().GetContainers().get(0).GetFilters().get(1).getName());
        Assert.assertEquals("f3", context.GetController().GetContainers().get(0).GetFilters().get(2).getName());
        Assert.assertEquals("f1", context.GetController().GetContainers().get(1).GetFilters().get(0).getName());
        Assert.assertEquals("f2", context.GetController().GetContainers().get(1).GetFilters().get(1).getName());
        Assert.assertEquals("f3", context.GetController().GetContainers().get(1).GetFilters().get(2).getName());
        Assert.assertEquals("comp1", context.GetController().GetContainers().get(2).GetFilters().get(0).getName());
        Assert.assertEquals("f3", context.GetController().GetContainers().get(2).GetFilters().get(1).getName());

        context.GetController().ChangeState("c1", "f3", "y_gr");
        System.out.println("----------------------------------------------------");
        System.out.println("");
        builderObserver.ResetAllCounters();
        MockBuilderItemsUseCase1After itemsAfter = new MockBuilderItemsUseCase1After(context.GetHub());
        context.GetBuilder().Build(itemsAfter);

        Assert.assertEquals(1, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(4, builderObserver.FilterAddedCounter);
        Assert.assertEquals(1, builderObserver.FilterRemovedCounter);
        Assert.assertEquals(2, builderObserver.ContainerUpdated);
        Assert.assertEquals(2, filterHubListener.Update);
        Assert.assertEquals(9, filterHubListener.PropertyChanged);


        Assert.assertEquals(4, context.GetController().GetContainers().size());
        Assert.assertEquals("c1_b", context.GetController().GetContainers().get(0).GetName());
        Assert.assertEquals("c2", context.GetController().GetContainers().get(1).GetName());
        Assert.assertEquals("c3_b", context.GetController().GetContainers().get(2).GetName());
        Assert.assertEquals("c4", context.GetController().GetContainers().get(3).GetName());

        Assert.assertEquals("f1_b", context.GetController().GetContainers().get(0).GetFilters().get(0).getName());
        Assert.assertEquals("f2", context.GetController().GetContainers().get(0).GetFilters().get(1).getName());
        Assert.assertEquals("f3", context.GetController().GetContainers().get(0).GetFilters().get(2).getName());
        Assert.assertEquals("x_gr", ((MockSingleTextFilter)context.GetController().GetContainers().get(0).GetFilters().get(2)).GetValues().get(0));
        Assert.assertEquals("y_gr", ((MockSingleTextFilter)context.GetController().GetContainers().get(0).GetFilters().get(2)).GetValues().get(1));
        Assert.assertEquals("z_gr", ((MockSingleTextFilter)context.GetController().GetContainers().get(0).GetFilters().get(2)).GetValues().get(2));
        Assert.assertEquals("x_gr", ((MockSingleTextFilter)context.GetController().GetContainers().get(0).GetFilters().get(2)).GetDefaultValue());
        Assert.assertEquals("f5", context.GetController().GetContainers().get(0).GetFilters().get(3).getName());

        Assert.assertEquals("f2", context.GetController().GetContainers().get(1).GetFilters().get(0).getName());
        Assert.assertEquals("f3_b", context.GetController().GetContainers().get(1).GetFilters().get(1).getName());
        Assert.assertEquals("f4", context.GetController().GetContainers().get(1).GetFilters().get(2).getName());

        context.Dispose();
    }

    private class MockFilterHubListener implements IFilterHubListener {

        public int Update = 0;
        public int PropertyChanged = 0;
        public int ChangedCount = 0;
        public int ResetCount = 0;

        @Override
        public void FilterChanged(Filter filter) {
            this.ChangedCount++;
        }

        @Override
        public void FilterReset(Filter filter) {
            this.ResetCount++;
        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
            this.PropertyChanged++;
            System.out.println("MockFilterHubListener->FilterPropertyChanged(" + propType + "):" + filter);
        }

        @Override
        public void FilterUpdated(Filter filter) {
            Update++;
            System.out.println("MockFilterHubListener->FilterUpdated:" + filter);
        }

        public void ResetAllCounters(){
            Update = 0;
            PropertyChanged = 0;
            this.ResetCount = 0;
            this.ChangedCount = 0;
        }
    }

    private class MockBuilderItemsUseCase1Before extends BuilderItems{

        Hub hub;

        public MockBuilderItemsUseCase1Before(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();

            FilterContainer c1 = new FilterContainer(1, "c1");
            MockCheckBoxFilter c1f1 = new MockCheckBoxFilter(1, "f1", notifier);
            MockFreeTextFilter c1f2 = new MockFreeTextFilter(2, "f2", notifier);
            List<String> c1f3Values = new ArrayList<>();
            c1f3Values.add("x_en");
            c1f3Values.add("y_en");
            c1f3Values.add("z_en");
            MockSingleTextFilter c1f3 = new MockSingleTextFilter(3, "f3", notifier, c1f3Values);
            c1.AddFilter(c1f1);
            c1.AddFilter(c1f2);
            c1.AddFilter(c1f3);

            FilterContainer c2 = new FilterContainer(2, "c2");
            MockSingleSelectFilter c2f1 = new MockSingleSelectFilter(c2, 1, "f1", notifier);
            MockSingleSelectFilter c2f2 = new MockSingleSelectFilter(c2, 2, "f2", notifier);
            MockCheckBoxFilter c2f3 = new MockCheckBoxFilter(3, "f3", notifier);
            c2.AddFilter(c2f1);
            c2.AddFilter(c2f2);
            c2.AddFilter(c2f3);

            FilterContainer c3 = new FilterContainer(3, "c3");
            MockCheckBoxFilter c3f1 = new MockCheckBoxFilter(1, "f1", notifier);
            MockFreeTextFilter c3f2 = new MockFreeTextFilter(2, "f2", notifier);
            MockCompositeFilter c3comp = new MockCompositeFilter(1, "comp1", notifier);
            c3comp.AddFilter(c3f1);
            c3comp.AddFilter(c3f2);

            List<String> c3f3FromValues = new ArrayList<>();
            c3f3FromValues.add("x_from_en");
            c3f3FromValues.add("y_from_en");
            c3f3FromValues.add("z_from_en");
            List<String> c3f3ToValues = new ArrayList<>();
            c3f3ToValues.add("x_to_en");
            c3f3ToValues.add("y_to_en");
            c3f3ToValues.add("z_to_en");
            MockRangeFilter c3f3 = new MockRangeFilter(3, "f3", notifier,c3f3FromValues,c3f3ToValues);
            c3.AddFilter(c3comp);
            c3.AddFilter(c3f3);

            containers.add(c1);
            containers.add(c2);
            containers.add(c3);

            return containers;
        }
    }

    private class MockBuilderItemsUseCase1After extends BuilderItems{

        Hub hub;

        public MockBuilderItemsUseCase1After(Hub hub) {
            this.hub = hub;
        }

        @Override
        public List<FilterContainer> GetContainers() {
            FilterNotifier notifier = new FilterNotifier(this.hub);
            List<FilterContainer> containers = new ArrayList<>();

            FilterContainer c1 = new FilterContainer(1, "c1_b");
            MockCheckBoxFilter c1f5 = new MockCheckBoxFilter(5, "f5", notifier);
            MockCheckBoxFilter c1f1 = new MockCheckBoxFilter(1, "f1_b", notifier);
            c1f1.SetCount(3);
            MockFreeTextFilter c1f2 = new MockFreeTextFilter(2, "f2", notifier);

            List<String> c1f3Values = new ArrayList<>();
            c1f3Values.add("x_gr");
            c1f3Values.add("y_gr");
            c1f3Values.add("z_gr");
            MockSingleTextFilter c1f3 = new MockSingleTextFilter(3, "f3", notifier, c1f3Values);
            c1f3.SetCount(5);
            c1.AddFilter(c1f1);
            c1.AddFilter(c1f2);
            c1.AddFilter(c1f3);
            c1.AddFilter(c1f5);

            FilterContainer c2 = new FilterContainer(2, "c2");
            MockSingleSelectFilter c2f2 = new MockSingleSelectFilter(c2, 2, "f2", notifier);
            MockCheckBoxFilter c2f3 = new MockCheckBoxFilter(3, "f3_b", notifier);
            MockSingleSelectFilter c2f4 = new MockSingleSelectFilter(c2,4, "f4", notifier);
            c2.AddFilter(c2f2);
            c2.AddFilter(c2f3);
            c2.AddFilter(c2f4);

            FilterContainer c4 = new FilterContainer(4, "c4");
            MockCheckBoxFilter c4f1 = new MockCheckBoxFilter(1, "f1", notifier);
            c4f1.SetCount(5);
            MockCheckBoxFilter c4f2 = new MockCheckBoxFilter(2, "f2", notifier);
            c4f2.SetCount(6);
            c4.AddFilter(c4f1);
            c4.AddFilter(c4f2);

            FilterContainer c3 = new FilterContainer(3, "c3_b");
            MockCheckBoxFilter c3f1 = new MockCheckBoxFilter(1, "f1_b", notifier);
            MockFreeTextFilter c3f2 = new MockFreeTextFilter(2, "f2", notifier);
            MockCompositeFilter c3comp = new MockCompositeFilter(1, "composite", notifier);
            c3comp.AddFilter(c3f1);
            c3comp.AddFilter(c3f2);
            List<String> c3f3FromValues = new ArrayList<>();
            c3f3FromValues.add("x_from_gr");
            c3f3FromValues.add("y_from_gr");
            c3f3FromValues.add("z_from_gr");
            List<String> c3f3ToValues = new ArrayList<>();
            c3f3ToValues.add("x_to_gr");
            c3f3ToValues.add("y_to_gr");
            c3f3ToValues.add("z_to_gr");
            MockRangeFilter c3f3 = new MockRangeFilter(3, "f3", notifier,c3f3FromValues,c3f3ToValues);
            c3f3.SetCount(10);
            c3.AddFilter(c3comp);
            c3.AddFilter(c3f3);

            containers.add(c1);
            containers.add(c2);
            containers.add(c3);
            containers.add(c4);

            return containers;
        }
    }

    private class MockBuilderObserver implements IBuilderObserver {

        public int ContainerAddedCounter = 0;
        public int FilterAddedCounter = 0;
        public int ContainerRemovedCounter = 0;
        public int FilterRemovedCounter = 0;
        public int ContainerUpdated = 0;

        @Override
        public void ContainerAdded(ActionType actionType, FilterContainer container) {
            ContainerAddedCounter++;
            System.out.println("MockBuilderObserver->ContainerAdded:" + container);
        }

        @Override
        public void FilterAdded(ActionType actionType, Filter f) {
            FilterAddedCounter++;
            System.out.println("MockBuilderObserver->FilterAdded:" + f);
        }

        @Override
        public void ContainerRemoved(ActionType actionType, FilterContainer container) {
            ContainerRemovedCounter++;
            System.out.println("MockBuilderObserver->ContainerRemoved:" + container);
        }

        @Override
        public void ContainerUpdated(ActionType actionType, FilterContainer container) {
            ContainerUpdated++;
            System.out.println("MockBuilderObserver->ContainerUpdated:" + container);
        }

        @Override
        public void FilterRemoved(ActionType actionType, Filter f) {
            FilterRemovedCounter++;
            System.out.println("MockBuilderObserver->FilterRemoved:" + f);
        }


        public void ResetAllCounters() {
            ContainerAddedCounter = 0;
            FilterAddedCounter = 0;
            ContainerRemovedCounter = 0;
            FilterRemovedCounter = 0;
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

    private class AddNewContainerFiltersAndRemoveMissingContainerFiltersConfiguration extends Configuration{

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
            return ExistingContainerActionType.Nothing;
        }
    }

    private class OnlyAddNewContainerFiltersConfiguration extends Configuration{

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

    private class MockRequestHandler implements IRequestHandler{

        public int RequestCount = 0;
        public String Request = "";
        @Override
        public void makeRequest(String request) {
            this.Request = request;
            this.RequestCount++;
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
