package testing;

import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filtercontroller.IRequestConverter;
import domain.filters.Filter;
import domain.filters.INotifier;
import domain.hub.Hub;
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

    @Before
    public void Setup(){
        this.notifier = new MockFilterNotifier(0);
        this.filterContainer = new MockContainer("c");

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
        Assert.assertEquals(null, singleText1.GetSelectedValue());
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

        protected MockContainer(String name) {
            super(name);
        }
    }

    private class MockFilterNotifier implements INotifier{
        public int Notified;

        public MockFilterNotifier(int notified) {
            Notified = notified;
        }

        @Override
        public void NotifyFilterReset(Filter filter) {
            System.out.println("MockFilterNotifier->NotifyFilterReset:" + filter);
            this.Notified++;
        }

        @Override
        public void NotifyFilterStateChanged(Filter filter) {
            System.out.println("MockFilterNotifier->NotifyFilterStateChanged:" + filter);
            this.Notified++;
        }
    }

    private class MockFilterController extends FilterController{

        protected MockFilterController(List<FilterContainer> containers, Hub hub, IRequestHandler receiver, IRequestConverter requestConverter) {
            super(containers, hub, receiver, requestConverter);
        }
    }

}
