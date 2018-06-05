package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.*;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.hub.Hub;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;

import java.util.ArrayList;
import java.util.List;

public class BuilderActionsTest {

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
        Assert.assertEquals(0, builderObserver.FilterUpdatedCounter);

        MockItemsOneContainerAfter itemsAfter = new MockItemsOneContainerAfter(context.GetHub());

        context.GetBuilder().Build(itemsAfter);
        Assert.assertEquals(1, builderObserver.ContainerAddedCounter);
        Assert.assertEquals(0, builderObserver.ContainerRemovedCounter);
        Assert.assertEquals(4, builderObserver.FilterAddedCounter);
        Assert.assertEquals(1, builderObserver.FilterRemovedCounter);
        Assert.assertEquals(0, builderObserver.FilterUpdatedCounter);

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

    private class MockBuilderObserver implements IBuilderObserver {

        public int ContainerAddedCounter = 0;
        public int FilterAddedCounter = 0;
        public int FilterUpdatedCounter = 0;
        public int ContainerRemovedCounter = 0;
        public int FilterRemovedCounter = 0;

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
        public void FilterRemoved(ActionType actionType, Filter f) {
            FilterRemovedCounter++;
            System.out.println("MockBuilderObserver->FilterRemoved:" + f);
        }

        @Override
        public void FilterUpdated(ActionType actionType, Filter f) {
            FilterUpdatedCounter++;
            System.out.println("MockBuilderObserver->FilterUpdated:" + f);
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
