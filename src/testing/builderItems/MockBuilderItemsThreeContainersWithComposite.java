package testing.builderItems;

import domain.configuration.BuilderItems;
import domain.filtercontroller.FilterContainer;
import domain.hub.Hub;
import domain.hub.IHub;
import domain.notifier.FilterNotifier;
import domain.notifier.ParameterNotifier;
import domain.notifier.RequestNotifier;
import testing.mocks.*;

import java.util.ArrayList;
import java.util.List;

public class MockBuilderItemsThreeContainersWithComposite extends BuilderItems {

    IHub hub;

    public MockBuilderItemsThreeContainersWithComposite(IHub hub) {
        this.hub = hub;
    }

    @Override
    public List<FilterContainer> GetContainers() {
        FilterNotifier filterNotifier = new FilterNotifier(this.hub);
        ParameterNotifier parameterNotifier = new ParameterNotifier(this.hub);
        RequestNotifier requestNotifier = new RequestNotifier(this.hub);

        List<FilterContainer> containers = new ArrayList<>();
        FilterContainer c1 = new FilterContainer(1, "c1");
        MockCheckBoxFilter f1 = new MockCheckBoxFilter(1,"f1", filterNotifier);
        MockCheckBoxFilter f2 = new MockCheckBoxFilter(2, "f2", parameterNotifier);
        MockFreeTextFilter f3 = new MockFreeTextFilter(3, "f3", requestNotifier);
        c1.AddFilter(f1);
        c1.AddFilter(f2);
        c1.AddFilter(f3);

        FilterContainer c2 = new FilterContainer(2, "c2");
        MockCompositeFilter c2_f1 = new MockCompositeFilter(1, "f1", filterNotifier);
        MockFreeTextFilter c2_f1_f1 = new MockFreeTextFilter(1, "f1", c2_f1);
        List<String> c2_f1_f2From = new ArrayList<>();
        c2_f1_f2From.add("10");
        c2_f1_f2From.add("20");
        c2_f1_f2From.add("30");
        MockSingleTextFilter c2_f1_f2 = new MockSingleTextFilter(2, "f2", c2_f1, c2_f1_f2From);
        c2_f1.AddFilter(c2_f1_f1);
        c2_f1.AddFilter(c2_f1_f2);

        MockSingleSelectFilter c2_f2 = new MockSingleSelectFilter(c2,2, "f2",parameterNotifier);
        c2.AddFilter(c2_f1);
        c2.AddFilter(c2_f2);

        FilterContainer c3 = new FilterContainer(3, "c3");
        List<String> c3_f1From = new ArrayList<>();
        c3_f1From.add("10");
        c3_f1From.add("20");
        c3_f1From.add("30");
        List<String> c3_f1To = new ArrayList<>();
        c3_f1To.add("40");
        c3_f1To.add("50");
        c3_f1To.add("60");
        MockRangeFilter c3_f1 = new MockRangeFilter(1, "f1", requestNotifier,c3_f1From,c3_f1To);
//        c3_f1.SetDefaultFrom("10");
//        c3_f1.SetDefaultTo("40");
        MockFreeTextFilter c3_f2 = new MockFreeTextFilter(2, "f2", parameterNotifier);
        c3.AddFilter(c3_f1);
        c3.AddFilter(c3_f2);

        containers.add(c1);
        containers.add(c2);
        containers.add(c3);

        return containers;
    }
}
