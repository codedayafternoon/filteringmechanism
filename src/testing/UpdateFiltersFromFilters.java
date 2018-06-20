package testing;

import domain.buildins.UrlBuilder;
import domain.buildins.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.Configuration;
import domain.configuration.ExistingContainerActionType;
import domain.configuration.MissingContainerActionType;
import domain.configuration.NewContainerActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockCheckBoxFilter;
import testing.mocks.MockFreeTextFilter;
import testing.mocks.MockRangeFilter;
import testing.mocks.MockSingleTextFilter;

import java.util.ArrayList;
import java.util.List;

public class UpdateFiltersFromFilters {

    //FilterContext context;
    FilterNotifier notifier;

//    @After
//    public void TearDown(){
//        this.context.GetController().Clear();
//        this.context.GetHub().ClearFilterListeners();
//    }

    @Test
    public void testUpdateCheckBoxFilter(){
        FilterContext context =  new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        MockConfiguration conf = new MockConfiguration();
        context.Initialize(handler,new UrlQueryConverter(new UrlBuilder(",", "&")), conf);
        this.notifier = new FilterNotifier(context.GetHub());

        MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
        MockFilterListener listener = new MockFilterListener();
        context.GetHub().AddFilterListener(listener);
        MockCheckBoxFilter new_f1 = new MockCheckBoxFilter(1, "f1_new", notifier);
        Assert.assertEquals("f1", f1.getName());
        f1.UpdateFrom(new_f1);
        Assert.assertEquals("f1_new", f1.getName());
        Assert.assertEquals(1, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(0, listener.FilterUpdatedCounter);

        context.Dispose();
    }

    @Test
    public void testUpdateFreeTextFilter(){
        FilterContext context =  new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        MockConfiguration conf = new MockConfiguration();
        context.Initialize(handler,new UrlQueryConverter(new UrlBuilder(",", "&")), conf);
        this.notifier = new FilterNotifier(context.GetHub());

        MockFreeTextFilter f1 = new MockFreeTextFilter(1, "f1", this.notifier);
        MockFilterListener listener = new MockFilterListener();
        context.GetHub().AddFilterListener(listener);
        MockFreeTextFilter new_f1 = new MockFreeTextFilter(2, "f1_new", notifier); // id doesn't matter on this level
        Assert.assertEquals("f1", f1.getName());
        f1.UpdateFrom(new_f1);
        Assert.assertEquals("f1_new", f1.getName());
        Assert.assertEquals(1, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(0, listener.FilterUpdatedCounter);

        context.Dispose();
    }

    @Test
    public void testUpdateSingleTextFilter(){
        FilterContext context =  new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        MockConfiguration conf = new MockConfiguration();
        context.Initialize(handler,new UrlQueryConverter(new UrlBuilder(",", "&")), conf);
        this.notifier = new FilterNotifier(context.GetHub());

        List<String> values = new ArrayList<>();
        values.add("x_en");
        values.add("y_en");
        values.add("z_en");
        FilterContainer c1 = new FilterContainer(1, "c1");
        MockSingleTextFilter f1 = new MockSingleTextFilter(1, "f1", this.notifier,values);
        c1.AddFilter(f1);
        f1.SetDefaultValue("z_en");
        f1.ChangeState("y_en");

        MockFilterListener listener = new MockFilterListener();
        context.GetHub().AddFilterListener(listener);

        List<String> values_gr = new ArrayList<>();
        values_gr.add("x_gr");
        values_gr.add("y_gr");
        values_gr.add("z_gr");
        MockSingleTextFilter new_f1 = new MockSingleTextFilter(2, "f1_new", notifier, values_gr);
        new_f1.SetDefaultValue("z_gr");
        c1.AddFilter(new_f1);

        Assert.assertEquals("f1", f1.getName());
        Assert.assertEquals("x_en", f1.GetValues().get(0));
        Assert.assertEquals("y_en", f1.GetValues().get(1));
        Assert.assertEquals("z_en", f1.GetValues().get(2));
        f1.UpdateFrom(new_f1);
        Assert.assertEquals("f1_new", f1.getName());
        Assert.assertEquals("x_gr", f1.GetValues().get(0));
        Assert.assertEquals("y_gr", f1.GetValues().get(1));
        Assert.assertEquals("z_gr", f1.GetValues().get(2));
        Assert.assertEquals("y_gr", f1.GetSelectedValue());
        Assert.assertEquals("y_gr", f1.GetState());
        Assert.assertEquals("z_gr", f1.GetDefaultValue());
        Assert.assertEquals(1, listener.FilterPropertyChangedCounter);
        Assert.assertEquals(1, listener.FilterUpdatedCounter);

        context.Dispose();
    }

    @Test
    public void testRangeFilterUpdate(){
        FilterContext context =  new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        MockConfiguration conf = new MockConfiguration();
        context.Initialize(handler,new UrlQueryConverter(new UrlBuilder(",", "&")), conf);
        this.notifier = new FilterNotifier(context.GetHub());

        MockFilterListener listener = new MockFilterListener();
        context.GetHub().AddFilterListener(listener);

        List<String> from_values_en = new ArrayList<>();
        from_values_en.add("from_en1");
        from_values_en.add("from_en2");
        from_values_en.add("from_en3");
        List<String> to_values_en = new ArrayList<>();
        to_values_en.add("to_en1");
        to_values_en.add("to_en2");
        to_values_en.add("to_en3");
        MockRangeFilter f1 = new MockRangeFilter(1, "f1", this.notifier,from_values_en,to_values_en);

        f1.SetDefaultFrom("from_en2");
        f1.SetDefaultTo("to_en2");
        f1.SetFrom("from_en3");
        f1.SetTo("to_en2");


        List<String> from_values_gr = new ArrayList<>();
        from_values_gr.add("from_gr1");
        from_values_gr.add("from_gr2");
        from_values_gr.add("from_gr3");
        List<String> to_values_gr = new ArrayList<>();
        to_values_gr.add("to_gr1");
        to_values_gr.add("to_gr2");
        to_values_gr.add("to_gr3");
        MockRangeFilter f2 = new MockRangeFilter(2, "f2", this.notifier,from_values_gr,to_values_gr); // id doesn't matter on that level

        f2.SetDefaultFrom("from_gr1");
        f2.SetDefaultTo("to_gr2");

        Assert.assertEquals("from_en1", f1.GetFromValues().get(0));
        Assert.assertEquals("from_en2", f1.GetFromValues().get(1));
        Assert.assertEquals("from_en3", f1.GetFromValues().get(2));
        Assert.assertEquals("to_en1", f1.GetToValues().get(0));
        Assert.assertEquals("to_en2", f1.GetToValues().get(1));
        Assert.assertEquals("to_en3", f1.GetToValues().get(2));
        Assert.assertEquals("from_en2", f1.GetDefaultFrom());
        Assert.assertEquals("to_en2", f1.GetDefaultTo());
        Assert.assertEquals("from_en3", f1.GetSelectedFrom());
        Assert.assertEquals("to_en2", f1.GetSelectedTo());

        f1.UpdateFrom(f2);

        Assert.assertEquals("from_gr1", f1.GetFromValues().get(0));
        Assert.assertEquals("from_gr2", f1.GetFromValues().get(1));
        Assert.assertEquals("from_gr3", f1.GetFromValues().get(2));
        Assert.assertEquals("to_gr1", f1.GetToValues().get(0));
        Assert.assertEquals("to_gr2", f1.GetToValues().get(1));
        Assert.assertEquals("to_gr3", f1.GetToValues().get(2));
        Assert.assertEquals("from_gr1", f1.GetDefaultFrom());
        Assert.assertEquals("to_gr2", f1.GetDefaultTo());
        Assert.assertEquals("from_gr3", f1.GetSelectedFrom());
        Assert.assertEquals("to_gr2", f1.GetSelectedTo());

        context.Dispose();
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

    private class MockRequestHandler implements IRequestHandler {

        @Override
        public void makeRequest(String request) {

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

    private class MockFilterListener implements IFilterHubListener{

        public int FilterChangedCounter = 0;
        public int FilterResetCounter = 0;
        public int FilterPropertyChangedCounter = 0;
        public int FilterUpdatedCounter = 0;

        @Override
        public void FilterChanged(Filter filter) {
            this.FilterChangedCounter++;
            System.out.println("MockFilterListener->FilterChanged:" + filter);
        }

        @Override
        public void FilterReset(Filter filter) {
            this.FilterResetCounter++;
            System.out.println("MockFilterListener->FilterReset:" + filter);
        }

        @Override
        public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {
            this.FilterPropertyChangedCounter++;
            System.out.println("MockFilterListener->FilterPropertyChanged(" + propType + "):" + filter);
        }

        @Override
        public void FilterUpdated(Filter filter) {
            this.FilterUpdatedCounter++;
            System.out.println("MockFilterListener->FilterUpdated:" + filter);
        }
    }
}
