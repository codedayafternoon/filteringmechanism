package testing;

import domain.filtercontroller.FilterContainer;
import domain.hub.Hub;
import domain.hub.interconnections.EventSubjectPair;
import domain.hub.interconnections.FilterEvent;
import domain.hub.interconnections.FilterInterconnection;
import domain.notifier.FilterNotifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testing.mocks.*;

import java.util.ArrayList;
import java.util.List;

public class InterconnectionTesting {

    Hub hub;

    private FilterContainer container1;
    private MockCheckBoxFilter checkBox1;
    private MockSingleTextFilter singleTextBox2;
    private MockRangeFilter range1;

    private FilterContainer container2;
    private MockFreeTextFilter freeFilter;
    private MockRangeFilter range2;
    private MockSingleTextFilter singleText3;

    private FilterContainer container3;
    private MockSingleSelectFilter single1;
    private MockSingleSelectFilter single2;

    private FilterNotifier filterNotifier;


    @Before
    public void Setup(){
        this.hub = new Hub();
        this.filterNotifier = new FilterNotifier(hub);
        this.container1 = new FilterContainer("c1");
        this.checkBox1 = new MockCheckBoxFilter(1, "f1", this.filterNotifier);
        List<String> singleTextBox2Values = new ArrayList<>();
        singleTextBox2Values.add("x");
        singleTextBox2Values.add("y");
        singleTextBox2Values.add("z");
        this.singleTextBox2 = new MockSingleTextFilter(2, "f2",this.filterNotifier, singleTextBox2Values);
        this.singleTextBox2.SetDefaultValue("y");
        List<String> fromValues = new ArrayList<>();
        fromValues.add("100");
        fromValues.add("200");
        fromValues.add("300");
        List<String> toValues = new ArrayList<>();
        toValues.add("200");
        toValues.add("300");
        toValues.add("400");
        this.range1 = new MockRangeFilter(3, "f3", this.filterNotifier);
        this.range1.AddFromValues(fromValues);
        this.range1.AddToValues(toValues);
        this.range1.SetDefaultFrom("100");
        this.range1.SetDefaultTo("400");
        this.container1.AddFilter(checkBox1);
        this.container1.AddFilter(singleTextBox2);
        this.container1.AddFilter(range1);

        this.container2 = new FilterContainer("c2");
        this.freeFilter = new MockFreeTextFilter(4, "f1", filterNotifier);
        this.freeFilter.SetDefaultValue("");
        List<String> fromValues2 = new ArrayList<>();
        fromValues2.add("1000");
        fromValues2.add("2000");
        fromValues2.add("3000");
        List<String> toValues2 = new ArrayList<>();
        toValues2.add("2000");
        toValues2.add("3000");
        toValues2.add("4000");
        this.range2 = new MockRangeFilter(5, "f2", this.filterNotifier);
        this.range2.AddFromValues(fromValues2);
        this.range2.AddToValues(toValues2);
        this.range2.SetDefaultFrom("1000");
        this.range2.SetDefaultTo("4000");
        List<String> singleText3Values = new ArrayList<>();
        singleText3Values.add("a");
        singleText3Values.add("b");
        singleText3Values.add("c");
        this.singleText3 = new MockSingleTextFilter(6, "f3",this.filterNotifier, singleText3Values);
        this.singleText3.SetDefaultValue("a");
        this.container2.AddFilter(this.freeFilter);
        this.container2.AddFilter(range2);
        this.container2.AddFilter(this.singleText3);

        this.container3 = new FilterContainer("c3");
        this.single1 = new MockSingleSelectFilter(this.container3,  7, "f1", this.filterNotifier);
        this.single2 = new MockSingleSelectFilter(this.container3, 8, "f2", this.filterNotifier);
        this.container3.AddFilter(single1);
        this.container3.AddFilter(single2);
    }

    @Test
    public void testHubInterconnectionComplex(){

        // rule 1
        FilterInterconnection interconnection1 = new FilterInterconnection();
        interconnection1.When.Event = FilterEvent.Reset;
        interconnection1.When.AddFilter(this.checkBox1);
        interconnection1.When.AddFilter(this.range1);
        EventSubjectPair thenClause1_1 = new EventSubjectPair();
        thenClause1_1.Event = FilterEvent.StateChange;
        thenClause1_1.Parameters = "abc";
        thenClause1_1.AddFilter(this.freeFilter);
        EventSubjectPair thenClause1_2 = new EventSubjectPair();
        thenClause1_2.Event = FilterEvent.StateChange;
        thenClause1_2.Parameters = "1";
        thenClause1_2.AddFilter(this.single2);
        interconnection1.Then.add(thenClause1_1);
        interconnection1.Then.add(thenClause1_2);
        this.hub.Interconnections.add(interconnection1);

        this.checkBox1.Check();
        Assert.assertEquals("", this.freeFilter.GetState());
        Assert.assertEquals(false, this.single2.IsChecked());
        this.checkBox1.UnCheck();
        Assert.assertEquals("abc", this.freeFilter.GetState());
        Assert.assertEquals(true, this.single2.IsChecked());
        this.freeFilter.ChangeState("");
        this.single2.Check();
        this.range1.ChangeState("from:200-to:200");
        this.range1.Reset();
        Assert.assertEquals("abc", this.freeFilter.GetState());
        Assert.assertEquals(true, this.single2.IsChecked());

        // rule 2
        FilterInterconnection interconnection2 = new FilterInterconnection();
        interconnection2.When.Event = FilterEvent.Reset;
        interconnection2.When.AddFilter(this.range2);
        interconnection2.When.AddFilter(this.singleTextBox2);
        EventSubjectPair thenClause2_1 = new EventSubjectPair();
        thenClause2_1.Event = FilterEvent.StateChange;
        thenClause2_1.Parameters = "1";
        thenClause2_1.AddFilter(this.single1);
        thenClause2_1.AddFilter(this.checkBox1);
        interconnection2.Then.add(thenClause2_1);
        this.hub.Interconnections.add(interconnection2);

        Assert.assertEquals(false, this.checkBox1.IsChecked());
        Assert.assertEquals(false, this.single1.IsChecked());

        this.range2.ChangeState("from:2000-to:2000");
        Assert.assertEquals("from:2000-to:2000", this.range2.GetState());

        // same as range2.Reset()
        this.range2.ChangeState("from:1000");
        this.range2.ChangeState("to:4000");
        Assert.assertEquals(true, this.checkBox1.IsChecked());
        Assert.assertEquals(true, this.single1.IsChecked());

        this.checkBox1.UnCheck();
        this.single1.UnCheck();
        Assert.assertEquals(false, this.checkBox1.IsChecked());
        Assert.assertEquals(false, this.single1.IsChecked());
        this.singleTextBox2.ChangeState("z");
        this.singleTextBox2.ChangeState("y"); // same as Reset
        Assert.assertEquals(true, this.checkBox1.IsChecked());
        Assert.assertEquals(true, this.single1.IsChecked());

    }

    @Test
    public void testHubInterconnectionMultipleItems(){
        FilterInterconnection interconnection = new FilterInterconnection();
        interconnection.When.Event = FilterEvent.Reset;
        interconnection.When.AddFilter(this.checkBox1);
        interconnection.When.AddFilter(this.range1);
        EventSubjectPair thenClause = new EventSubjectPair();
        thenClause.Event = FilterEvent.StateChange;
        thenClause.AddFilter(this.freeFilter);
        thenClause.Parameters = "new_free_text";
        interconnection.Then.add(thenClause);
        this.hub.Interconnections.add(interconnection);

        this.freeFilter.ChangeState("asd");
        this.checkBox1.Check(); // same as ChangeState("1")
        this.checkBox1.ChangeState("0"); // same as Reset()

        Assert.assertEquals("new_free_text", this.freeFilter.GetState());
        this.freeFilter.ChangeState("asd");

        this.range1.ChangeState("from:300");
        this.range1.ChangeState("to:400");
        Assert.assertEquals("from:300-to:400", this.range1.GetState());
        Assert.assertEquals("asd", this.freeFilter.GetState()); // not affected yet
        this.range1.Reset();
        Assert.assertEquals("new_free_text", this.freeFilter.GetState());

    }

    @Test
    public void testHubInterconnectionsSimple(){
        FilterInterconnection interconnection = new FilterInterconnection();
        interconnection.When.Event = FilterEvent.StateChange;
        interconnection.When.AddFilter(this.checkBox1);
        EventSubjectPair thenClause = new EventSubjectPair();
        thenClause.Event = FilterEvent.Reset;
        thenClause.AddFilter(this.singleText3);
        interconnection.Then.add(thenClause);
        this.hub.Interconnections.add(interconnection);

        this.singleText3.ChangeState("c");
        this.checkBox1.Check();

        Assert.assertEquals("a", this.singleText3.GetState());



    }
}
