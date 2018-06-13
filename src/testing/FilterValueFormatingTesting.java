package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import domain.FilterContext;
import domain.configuration.Configuration;
import domain.configuration.ExistingContainerActionType;
import domain.configuration.MissingContainerActionType;
import domain.configuration.NewContainerActionType;
import domain.filtercontroller.IRequestHandler;
import domain.filters.INotifier;
import domain.filters.valueformatters.NumberValueFormatPolicy;
import domain.filters.valueformatters.NumberValuePostFormatter;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Test;
import testing.mocks.MockFreeTextFilter;
import testing.mocks.MockRangeFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterValueFormatingTesting {

    @Test
    public void testNumberExtractionLogic(){
        NumberValuePostFormatter formatter = new NumberValuePostFormatter(NumberValueFormatPolicy.CONVERT_COMMA_TO_DOT);
        List<String> numbers = formatter.Extract("jdj7jhb2.33sjd23,33dd0-09=-=234");
        // 7 2.33 23,33 0 09 234

        Assert.assertEquals(6, numbers.size());
        Assert.assertEquals("7", numbers.get(0));
        Assert.assertEquals("2.33", numbers.get(1));
        Assert.assertEquals("23,33", numbers.get(2));
        Assert.assertEquals("0", numbers.get(3));
        Assert.assertEquals("09", numbers.get(4));
        Assert.assertEquals("234", numbers.get(5));

        numbers = formatter.Extract("77s7s,dw2,3wdq,dd");
        Assert.assertEquals(3, numbers.size());
        Assert.assertEquals("77", numbers.get(0));
        Assert.assertEquals("7", numbers.get(1));
        Assert.assertEquals("2,3", numbers.get(2));

        numbers = formatter.Extract("2,8 - 3,5");


    }

    @Test
    public void testNumberFormattingLogic(){
        List<String> numbers = new ArrayList<>();
        numbers.add("1#$2,22");
        numbers.add("1332.22");
        numbers.add("1#F2,2.2");
        numbers.add("1FF#2,2,2");
        numbers.add("1.,22");
        numbers.add("1#,F#22,2");

        NumberValuePostFormatter formatter = new NumberValuePostFormatter(NumberValueFormatPolicy.CONVERT_COMMA_TO_DOT);

        for(int i = 0; i < numbers.size(); i++){
            String formatted = formatter.Format(numbers.get(i));
            numbers.set(i, formatted);
        }

        Assert.assertEquals(6, numbers.size());
        Assert.assertEquals("1#$2.22", numbers.get(0));
        Assert.assertEquals("1332.22", numbers.get(1));
        Assert.assertEquals("1#F2.2.2", numbers.get(2));
        Assert.assertEquals("1FF#2.2.2", numbers.get(3));
        Assert.assertEquals("1..22", numbers.get(4));
        Assert.assertEquals("1#.F#22.2", numbers.get(5));

    }

    @Test
    public void testFilterWithCustomPostFormatter(){
        FilterContext context = new FilterContext();
        MockRequestHandler handler = new MockRequestHandler();
        context.Initialize(handler, new UrlQueryConverter(new UrlBuilder(",", "&")), new MockConfiguration());
        FilterNotifier notifier = new FilterNotifier(context.GetHub());
        MockFreeTextFilter f1 = new MockFreeTextFilter(1, "f1", notifier);

        f1.ChangeState("77s7s,dw2,3wdq,dd");
        String res = f1.GetParameterValue();
        Assert.assertEquals("77s7s,dw2,3wdq,dd", res);

        NumberValuePostFormatter formatter = new NumberValuePostFormatter(NumberValueFormatPolicy.CONVERT_COMMA_TO_DOT);
        f1.SetValuePostFormatter(formatter);
        res = f1.GetParameterValue();
        Assert.assertEquals("77s7s,dw2.3wdq,dd", res);

        List<String> rangeFromValues = new ArrayList<>();
        rangeFromValues.add("jj323j#J#j32,3j32,d");
        rangeFromValues.add("ad33f43f3fF#F#Ff324f");
        rangeFromValues.add("*77..3,33f3f.3f3,3f3.f33,");
        List<String> rangeToValues = new ArrayList<>();
        rangeToValues.add("33uj3,3kj3d,3d3.3d,3.3,,dcs.3f3f3,");
        rangeToValues.add("3,2e2,3.e23e,23e.3r,5.665,42t.5,4");
        rangeToValues.add("35,56yu7.u35,3423.r24r,.t");
        MockRangeFilterCustom f2 = new MockRangeFilterCustom(2, "f2", notifier, rangeFromValues, rangeToValues);
        f2.ChangeState("from:ad33f43f3fF#F#Ff324f-to:35,56yu7.u35,3423.r24r,.t");
        f2.SetValuePostFormatter(formatter);
        res = f2.GetParameterKey() + "=" + f2.GetParameterValue();
        Assert.assertEquals("ad33f43f3fF#F#Ff324f", f2.GetParameterValueFrom());
        Assert.assertEquals("35.56yu7.u35.3423.r24r,.t", f2.GetParameterValueTo());
        //Assert.assertEquals("f2Min=ad33f43f3fF#F#Ff324f&f2Max=35.56yu7.u35.3423.r24r,.t", res);
        f2.ChangeState("from:jj323j#J#j32,3j32,d-to:33uj3,3kj3d,3d3.3d,3.3,,dcs.3f3f3,");
        //res = f2.GetParameterKey() + "=" + f2.GetParameterValue();
        Assert.assertEquals("jj323j#J#j32.3j32.d", f2.GetParameterValueFrom());
        Assert.assertEquals("33uj3.3kj3d,3d3.3d,3.3..dcs.3f3f3.", f2.GetParameterValueTo());
        //Assert.assertEquals("f2Min=jj323j#J#j32.3j32.d&f2Max=33uj3.3kj3d,3d3.3d,3.3..dcs.3f3f3.", res);
        f2.ChangeState("from:jj323j#J#j32,3j32,d-to:3,2e2,3.e23e,23e.3r,5.665,42t.5,4");
        //res = f2.GetParameterKey() + "=" + f2.GetParameterValue();
        Assert.assertEquals("jj323j#J#j32.3j32.d", f2.GetParameterValueFrom());
        Assert.assertEquals("3.2e2.3.e23e,23e.3r,5.665.42t.5.4", f2.GetParameterValueTo());
        context.Dispose();
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

    private class MockConfiguration extends Configuration{

        @Override
        public MissingContainerActionType getMissingContainerActionType() {
            return MissingContainerActionType.Nothing;
        }

        @Override
        public NewContainerActionType getNewContainerActionType() {
            return NewContainerActionType.Nothing;
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
