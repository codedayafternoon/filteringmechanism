package testing;

import domain.filtercontroller.FilterContainer;
import domain.filters.Filter;
import domain.filters.FilterFormatter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;
import domain.filters.types.CompositeFilter;
import domain.notifier.NotifierChannelType;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.*;

import java.util.ArrayList;
import java.util.List;

public class DisplayFormatterTesting {

    @Test
    public void testSimpleDisplayFormattersFunctionality(){
        MockNotifier notifier = new MockNotifier();
        FilterContainer container = new FilterContainer(1, "c1");
        MockCheckBoxFilter checkBoxFilter = new MockCheckBoxFilter(1,"f1", notifier);

        String formatted = checkBoxFilter.GetFormattedText(0);
        Assert.assertEquals(checkBoxFilter.GetState(), formatted);

        MockEmptyFormatter formatter = new MockEmptyFormatter(10,"$cn, $fn, $fv");
        checkBoxFilter.AddFormatter(formatter);

        formatted = checkBoxFilter.GetFormattedText(10);

        Assert.assertEquals(", f1, false", formatted);
        container.AddFilter(checkBoxFilter);

        formatted = checkBoxFilter.GetFormattedText(10);

        Assert.assertEquals("c1, f1, false", formatted);
    }

    @Test
    public void testRangeFilterFormatter(){
        MockNotifier notifier = new MockNotifier();
        FilterContainer container = new FilterContainer(10, "container");

        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        List<String> toValues = new ArrayList<>();
        toValues.add("500");
        toValues.add("600");
        toValues.add("700");
        MockRangeFilter rangeFilter = new MockRangeFilter(1, "price",notifier, fromValues, toValues);
        rangeFilter.SetDefaultFrom("100");
        rangeFilter.SetDefaultTo("500");
        container.AddFilter(rangeFilter);

        MockEmptyFormatter fromFormatter = new MockEmptyFormatter("from", "$fn from $fv[0]€");
        MockEmptyFormatter toFormatter = new MockEmptyFormatter("to", "$fn to $fv[1]€");

        rangeFilter.AddFormatter(fromFormatter);
        rangeFilter.AddFormatter(toFormatter);

        String fromFormatted = rangeFilter.GetFormattedText("from");
        String toFormatted = rangeFilter.GetFormattedText("to");
        Assert.assertEquals("price from 100€", fromFormatted);
        Assert.assertEquals("price to 500€", toFormatted);

        rangeFilter.SetFrom("200");
        rangeFilter.SetTo("700");

        fromFormatted = rangeFilter.GetFormattedText("from");
        toFormatted = rangeFilter.GetFormattedText("to");

        Assert.assertEquals("price from 200€", fromFormatted);
        Assert.assertEquals("price to 700€", toFormatted);

        rangeFilter.ClearFormatters();

        rangeFilter.setName("τιμή");

        MockEmptyFormatter fromFormatter2 = new MockEmptyFormatter("from", "στο $cn η $fn από $fv[0]$");
        MockEmptyFormatter toFormatter2 = new MockEmptyFormatter("to", "στο $cn η $fn μέχρι $fv[1]$");
        rangeFilter.AddFormatter(fromFormatter2);
        rangeFilter.AddFormatter(toFormatter2);
        fromFormatted = rangeFilter.GetFormattedText("from");
        toFormatted = rangeFilter.GetFormattedText("to");
        Assert.assertEquals("στο container η τιμή από 200$", fromFormatted);
        Assert.assertEquals("στο container η τιμή μέχρι 700$", toFormatted);

    }

    @Test
    public void testCompositeFormatter(){
        MockNotifier notifier = new MockNotifier();
        FilterContainer container = new FilterContainer(1, "c1");

        MockCompositeFilter compositeFilter = new MockCompositeFilter(1, "f1", notifier);
        container.AddFilter(compositeFilter);

        MockFreeTextFilter freeTextFilter = new MockFreeTextFilter(1, "area", compositeFilter);

        List<String> values = new ArrayList<>();
        values.add("athens");
        values.add("berlin");
        values.add("alaska");
        MockSingleTextFilter singleTextFilter = new MockSingleTextFilter(2, "km", compositeFilter, values);
        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        List<String> toValues = new ArrayList<>();
        toValues.add("500");
        toValues.add("600");
        toValues.add("700");
        MockRangeFilter rangeFilter=  new MockRangeFilter(3, "price", compositeFilter, fromValues, toValues);
        rangeFilter.SetDefaultFrom("100");
        rangeFilter.SetDefaultTo("500");

        compositeFilter.AddFilter(freeTextFilter);
        compositeFilter.AddFilter(singleTextFilter);
        compositeFilter.AddFilter(rangeFilter);

        String pattern = "$f[0].fv km from $f[1].fv, $f[2].fn from $f[2].fv[0] to $f[2].fv[1]";


        MockEmptyFormatter formatter = new MockEmptyFormatter(1, pattern);
        compositeFilter.AddFormatter(formatter);

        String formatted = compositeFilter.GetFormattedText(1);
        // nothing is set yet
        Assert.assertEquals(" km from athens, price from 100 to 500", formatted);

        freeTextFilter.SetText("1000");
        formatted = compositeFilter.GetFormattedText(1);
        Assert.assertEquals("1000 km from athens, price from 100 to 500", formatted);


        freeTextFilter.SetText("2550");
        singleTextFilter.SetSelectedValue("alaska");
        rangeFilter.SetFrom("300");
        rangeFilter.SetTo("700");
        formatted = compositeFilter.GetFormattedText(1);
        Assert.assertEquals("2550 km from alaska, price from 300 to 700", formatted);

    }

    @Test
    public void testFormatterWithInjectableParameters(){

    }


    private class MockEmptyFormatter extends FilterFormatter {

        public MockEmptyFormatter(Object id, String pattern) {
            super(id, pattern);
        }
    }

    private class MockComplexEmptyFormatter extends FilterFormatter {

        public MockComplexEmptyFormatter(Object id, String pattern) {
            super(id, pattern);
        }

    }

    private class MockNotifier implements INotifier{

        @Override
        public void NotifyFilterStateChanged(Filter filter) {

        }

        @Override
        public void NotifyFilterReset(Filter filter) {

        }

        @Override
        public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void NotifyFilterUpdated(Filter filter) {

        }

        @Override
        public NotifierChannelType GetType() {
            return null;
        }
    }
}
