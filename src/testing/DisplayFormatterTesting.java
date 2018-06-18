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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        MockNotifier notifier = new MockNotifier();
        FilterContainer container = new FilterContainer(1, "c1");

        MockCheckBoxFilter f1 = new MockCheckBoxFilter(1, "f1", notifier);
        MockCheckBoxFilterFormatter formatter = new MockCheckBoxFilterFormatter(1,"$fv $local");
        f1.AddFormatter(formatter);

        String formatted = f1.GetFormattedText(1);
        Assert.assertEquals("NO $local", formatted); // $local should not be deserialized

        Map<String, String> _params = new HashMap<>();
        _params.put("local", "el");
        formatted = f1.GetFormattedText(1, _params);
        Assert.assertEquals("NO el", formatted); // $local should not be deserialized


        MockCompositeFilter composite = new MockCompositeFilter(1, "f1", notifier);
        MockFreeTextFilter freeTextFilter = new MockFreeTextFilter(1, "f1", composite);

        List<String> singleTextValues = new ArrayList<>();
        singleTextValues.add("x");
        singleTextValues.add("y");
        singleTextValues.add("z");
        MockSingleTextFilter singleText = new MockSingleTextFilter(2, "f2", composite, singleTextValues);

        List<String> rangeFilterFromValues = new ArrayList<>();
        rangeFilterFromValues.add("10");
        rangeFilterFromValues.add("30");
        rangeFilterFromValues.add("50");
        List<String> rangeFilterToValues = new ArrayList<>();
        rangeFilterToValues.add("30");
        rangeFilterToValues.add("60");
        rangeFilterToValues.add("100");
        MockRangeFilter rangeFilter = new MockRangeFilter(3, "f3", composite, rangeFilterFromValues, rangeFilterToValues);

        composite.AddFilter(freeTextFilter);
        composite.AddFilter(singleText);
        composite.AddFilter(rangeFilter);

        Map<String, String> freeValues = new HashMap<>();
        freeValues.put("local", "aad 4 34r3");
        freeValues.put("tree", "δεντρο");

        MockFreeTextFormatter freeTextFilterFormatter = new MockFreeTextFormatter(1, "$tree $fv $local");
        freeTextFilter.AddFormatter(freeTextFilterFormatter);
        formatted = freeTextFilter.GetFormattedText(1, freeValues);
        Assert.assertEquals("δεντρο  aad 4 34r3", formatted);

        freeTextFilter.SetText("aa text");
        formatted = freeTextFilter.GetFormattedText(1, freeValues);
        Assert.assertEquals("δεντρο AA aa text aad 4 34r3", formatted);

        Map<String, String> compParams = new HashMap<>();
        compParams.put("ext3", "x");
        compParams.put("ext1", "y");
        compParams.put("ext2", "z");
        MockEmptyFormatter formatter1 = new MockEmptyFormatter(1, "$ext1 $f[0].fn=$f[0].fv $f[1].fn $f[2].fn $ext2");
        composite.AddFormatter(formatter1);

        formatted = composite.GetFormattedText(1, compParams);
        // TODO composite does not support formatting of its internal filters
        Assert.assertEquals("y f1=aa text f2 f3 z", formatted);

    }

    private class MockFreeTextFormatter extends FilterFormatter{

        public MockFreeTextFormatter(Object id, String pattern) {
            super(id, pattern);
        }

        @Override
        public String fv(String v, int index){
            if(v == null)
                return "";
            if(v.startsWith("a")){
                return "AA " + v;
            }
            return v;
        }

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

    private class MockCheckBoxFilterFormatter extends FilterFormatter{

        public MockCheckBoxFilterFormatter(Object id, String pattern) {
            super(id, pattern);
        }

        @Override
        public String fv(String filterValue, int valueIndex){
            if(filterValue.equals("false")){
                return "NO";
            }else if(filterValue.equals("true")){
                return "YES";
            }
            return "DONT KNOW";
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
