package testing;

import application.infrastructure.UrlBuilder;
import application.infrastructure.UrlQueryConverter;
import com.sun.nio.sctp.IllegalReceiveException;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filtercontroller.IRequestConverter;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.filters.INotifier;
import domain.filters.policies.SelectedValuePolicyType;
import domain.filters.types.RangeFilter;
import domain.hub.Hub;
import domain.notifier.FilterNotifier;
import domain.notifier.NotifierChannelType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.*;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimfi on 5/27/2018.
 */
public class FilterTest {

    private MockFilterNotifier notifier;
    private FilterContainer filterContainer;
    private FilterContainer filterContainer1;
    private FilterController filterController;
    private MockRequesthandler handler;
    Hub hub;

    @Before
    public void Setup(){
        this.notifier = new MockFilterNotifier(0);
        this.filterContainer = new MockContainer(1,"c");
        this.hub = new Hub();
        this.handler = new MockRequesthandler();
        this.filterController = new FilterController(this.hub, this.handler, new UrlQueryConverter(new UrlBuilder(",", "&")));
        this.filterController.AddContainer(this.filterContainer);

    }

    @Test
    public void testSameFilterNameInContainerNotPermitted(){

        MockSingleSelectFilter f1 = new MockSingleSelectFilter(this.filterContainer, 1, "f1", this.notifier);
        MockFreeTextFilter f2 = new MockFreeTextFilter(20, "f2", this.notifier);
        MockCompositeFilter f3 = new MockCompositeFilter(3, "f1", this.notifier);
        MockCheckBoxFilter f4 = new MockCheckBoxFilter(2, "f4", this.notifier);

        this.filterContainer.AddFilter(f1);
        this.filterContainer.AddFilter(f4);
        this.filterContainer.AddFilter(f2);
        try{
            this.filterContainer.AddFilter(f3);
            Assert.fail();
        }catch (IllegalArgumentException e){
        }
    }

    @Test
    public void testChangeAndResetRangeFilterPartially(){
        List<String> from = new ArrayList<>();
        from.add("x_from");
        from.add("y_from");
        from.add("z_from");
        List<String> to = new ArrayList<>();
        to.add("x_to");
        to.add("y_to");
        to.add("z_to");
        MockRangeFilterCustom f1 = new MockRangeFilterCustom( 1,"f1", this.notifier, from, to);
        f1.SetDefaultTo("y_to");
        f1.SetDefaultFrom("x_from");

        this.filterContainer.AddFilter(f1);
        this.filterController.ChangeState(1,1, "from:z_from-to:z_to");
        Assert.assertEquals("from:z_from-to:z_to", f1.GetState());
        Assert.assertFalse(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);
        this.filterController.ChangeState(1, 1, "from:reset");
        Assert.assertTrue(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);
        this.filterController.ChangeState(1,1, "from:z_from");
        Assert.assertFalse(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);
        this.filterController.ChangeState(1,1, "from:x_from");
        Assert.assertTrue(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);


        this.filterController.ChangeState(1,1, "from:z_from-to:z_to");
        Assert.assertEquals("from:z_from-to:z_to", f1.GetState());
        Assert.assertFalse(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);
        this.filterController.ChangeState(1, 1, "to:reset");
        Assert.assertTrue(this.notifier.ToReset);
        Assert.assertFalse(this.notifier.FromReset);
        this.filterController.ChangeState(1,1, "to:z_to");
        Assert.assertFalse(this.notifier.FromReset);
        Assert.assertFalse(this.notifier.ToReset);
        this.filterController.ChangeState(1,1, "to:y_to");
        Assert.assertTrue(this.notifier.ToReset);
        Assert.assertFalse(this.notifier.FromReset);

        this.filterContainer.Clear();
    }

    @Test
    public void testRangeFilterWithNullSelectedValuePolicyFunctionality(){
        MockFilterNotifier notifier = new MockFilterNotifier(0);

        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        fromValues.add("400");
        List<String> toValues = new ArrayList<>();
        toValues.add("400");
        toValues.add("500");
        toValues.add("600");
        toValues.add("700");
        MockRangeFilter f1 = new MockRangeFilter(1, "f1", notifier, fromValues, toValues);

        f1.SetSelectedValuePolicy(SelectedValuePolicyType.Null);

        String state = f1.GetState();
        Assert.assertEquals("from:null-to:null", state);

        f1.ChangeState("from:200");
        state = f1.GetState();
        Assert.assertEquals("from:200-to:null", state);

        f1.ChangeState("to:600");
        state = f1.GetState();
        Assert.assertEquals("from:200-to:600", state);

        f1.ChangeState("to:reset");
        state = f1.GetState();
        Assert.assertEquals("from:200-to:null", state);

        f1.ChangeState("from:reset");
        state = f1.GetState();
        Assert.assertEquals("from:null-to:null", state);

        f1.ChangeState("to:700");
        state = f1.GetState();
        Assert.assertEquals("from:null-to:700", state);

        f1.ChangeState("from:200");
        state = f1.GetState();
        Assert.assertEquals("from:200-to:700", state);

    }

    @Test
    public void testChangeAndResetRangeWithRequestUrlCheck(){
        FilterNotifier filterNotifier = new FilterNotifier(this.hub);
        List<String> from = new ArrayList<>();
        from.add("x_from");
        from.add("y_from");
        from.add("z_from");
        List<String> to = new ArrayList<>();
        to.add("x_to");
        to.add("y_to");
        to.add("z_to");
        MockRangeFilterCustom f1 = new MockRangeFilterCustom( 1,"f1", filterNotifier, from, to);
        f1.SetSelectedValuePolicy(SelectedValuePolicyType.Null);

        this.filterContainer.AddFilter(f1);

        this.filterController.ChangeState(1,1, "from:y_from");

        Assert.assertEquals("f1Min=y_from", this.handler.Request);


        this.filterController.ChangeState(1,1, "from:reset");

        Assert.assertEquals("", this.handler.Request);
        this.filterController.ChangeState(1,1, "to:z_to");
        Assert.assertEquals("f1Max=z_to", this.handler.Request);
        this.filterController.ChangeState(1,1, "from:z_from");
        Assert.assertEquals("f1Min=z_from&f1Max=z_to", this.handler.Request);
        this.filterController.ChangeState(1,1, "to:reset");
        Assert.assertEquals("f1Min=z_from", this.handler.Request);
        this.filterController.ChangeState(1,1, "from:reset");
        Assert.assertEquals("", this.handler.Request);
    }

    @Test
    public void testSameIdInContainerIsNotPermitted(){
        MockSingleSelectFilter f1 = new MockSingleSelectFilter(this.filterContainer, 1, "f1", this.notifier);
        MockFreeTextFilter f2 = new MockFreeTextFilter(2, "f2", this.notifier);
        MockCompositeFilter f3 = new MockCompositeFilter(3, "f3", this.notifier);
        MockCheckBoxFilter f4 = new MockCheckBoxFilter(2, "f4", this.notifier);

        this.filterContainer.AddFilter(f1);
        this.filterContainer.AddFilter(f4);
        this.filterContainer.AddFilter(f3);
        try{
            this.filterContainer.AddFilter(f2);
            Assert.fail();
        }catch (KeyAlreadyExistsException e){
        }

        this.filterContainer.Clear();
    }

    @Test
    public void testSingleSelect(){
        MockSingleSelectFilter checkBox1 = new MockSingleSelectFilter(this.filterContainer, 1, "c1", this.notifier);
        checkBox1.Check(); // notifier fired
        MockSingleSelectFilter checkBox2 = new MockSingleSelectFilter(this.filterContainer,2, "c2", this.notifier);
        checkBox2.Check(); // notifier fired
        Assert.assertEquals(2, notifier.Notified );
        MockSingleSelectFilter checkBox3 = new MockSingleSelectFilter(this.filterContainer, 3, "c3", this.notifier);
        this.filterContainer.AddFilter(checkBox1);
        this.filterContainer.AddFilter(checkBox2);
        this.filterContainer.AddFilter(checkBox3);

        checkBox3.Check(); // should reset others to false
        // notifier is should notifier 3 times, one for the checkBox3 and two for reseting the other two
        Assert.assertEquals(false, checkBox1.IsChecked() );
        Assert.assertEquals(false, checkBox2.IsChecked() );
        Assert.assertEquals(true, checkBox3.IsChecked() );
        Assert.assertEquals(5, notifier.Notified );
        this.notifier.Notified = 0;

        checkBox2.Check(); // notifier should fire two times
        checkBox1.Check(); // notifier should fire two times
        Assert.assertEquals(true, checkBox1.IsChecked() );
        Assert.assertEquals(false, checkBox2.IsChecked() );
        Assert.assertEquals(false, checkBox3.IsChecked() );
        Assert.assertEquals(4, notifier.Notified );
    }

    @Test
    public void testCheckBox(){
        MockCheckBoxFilter checkBox1 = new MockCheckBoxFilter(1, "c1", this.notifier);
        MockCheckBoxFilter checkBox2 = new MockCheckBoxFilter(2, "c2", this.notifier);

        checkBox1.Check();
        Assert.assertEquals(true, checkBox1.IsChecked());
        Assert.assertEquals(false, checkBox2.IsChecked());
        Assert.assertEquals(1, this.notifier.Notified);
    }

    @Test
    public void testSingleText(){

        List<String> singleTextValues = new ArrayList<String>();
        singleTextValues.add("x");
        singleTextValues.add("y");
        singleTextValues.add("z");
        MockSingleTextFilter singleText1 = new MockSingleTextFilter(1, "f1", this.notifier, singleTextValues);
        singleText1.SetDefaultValue("q"); // there is no such value in list
        Assert.assertEquals("x", singleText1.GetSelectedValue());
        singleText1.SetDefaultValue("x");
        Assert.assertEquals("x", singleText1.GetSelectedValue());

        singleText1.SetSelectedValue("y");
        Assert.assertEquals("y", singleText1.GetSelectedValue());
        Assert.assertEquals(1, this.notifier.Notified);

        singleText1.Reset();
        Assert.assertEquals("x", singleText1.GetSelectedValue());
        Assert.assertEquals(2, this.notifier.Notified);

        singleText1.ChangeState("z"); // same effect as SetSelectedValue
        Assert.assertEquals("z", singleText1.GetSelectedValue());
        Assert.assertEquals(3, this.notifier.Notified);
    }

    private class MockContainer extends FilterContainer{

        protected MockContainer(Object id, String name) {
            super(id, name);
        }
    }

    private class MockFilterNotifier implements INotifier{
        public int Notified;
        public boolean ToReset = false;
        public boolean FromReset = false;

        public MockFilterNotifier(int notified) {
            Notified = notified;
        }

        @Override
        public void NotifyFilterReset(Filter filter) {
            System.out.println("MockFilterNotifier->NotifyFilterReset:" + filter);
            this.Notified++;
        }

        @Override
        public void NotifyPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

        }

        @Override
        public void NotifyFilterUpdated(Filter filter) {

        }

        @Override
        public NotifierChannelType GetType() {
            return NotifierChannelType.FilterChannel;
        }

        @Override
        public void NotifyFilterStateChanged(Filter filter) {
            System.out.println("MockFilterNotifier->NotifyFilterStateChanged:" + filter);
            this.Notified++;
            if(filter instanceof RangeFilter){
                RangeFilter rf = (RangeFilter)filter;
                this.ToReset = rf.IsToReset();
                this.FromReset = rf.IsFromReset();
            }
        }
    }

    private class MockFilterController extends FilterController{

        protected MockFilterController(List<FilterContainer> containers, Hub hub, IRequestHandler receiver, IRequestConverter requestConverter) {
            super(containers, hub, receiver, requestConverter);
        }
    }

    private class MockRequesthandler implements IRequestHandler{

        public String Request = "";

        @Override
        public void makeRequest(String request) {
            this.Request = request;
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
//
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

}
