package testing;

import org.junit.Assert;
import org.junit.Test;

import application.Container;
import domain.filters.Filter;
import domain.filters.IInvalidator;
import domain.filters.INotifier;
import domain.filters.types.CheckBoxFilter;
import domain.filters.types.FreeTextFilter;
import domain.filters.types.SingleSelectFilter;
import domain.hub.Hub;
import domain.hub.IFilterHubListener;
import domain.notifier.FilterNotifier;

public class FilterNotifierTest {

	/*
	 * test if hub listener is notified by state change in filters 
	 */
	@Test
	public void filterContainerCreation() {
		Container filterContainer = new Container("c");
		Hub filterHub = new Hub();
		FilterNotifier notifier = new FilterNotifier(filterHub);
		MockFilterHubListener listener = new MockFilterHubListener();
		filterHub.AddFilterListener(listener);
		
		MockSingleSelectFilter singleSelect1 = new MockSingleSelectFilter(filterContainer, 1, "f1", notifier);
		MockSingleSelectFilter singleSelect2 = new MockSingleSelectFilter(filterContainer, 2, "f2", notifier);
		MockSingleSelectFilter singleSelect3 = new MockSingleSelectFilter(filterContainer, 3, "f3", notifier);
		filterContainer.AddFilter(singleSelect1);
		filterContainer.AddFilter(singleSelect2);
		filterContainer.AddFilter(singleSelect3);
		
		MockCheckBokFilter checkBox1 = new MockCheckBokFilter(4, "c1", notifier);
		checkBox1.Check(); // equivalent with ChangeState("1"); // !!! notify listener !!!
		MockCheckBokFilter checkBox2 = new MockCheckBokFilter(5, "c2", notifier);
		filterContainer.AddFilter(checkBox1);
		filterContainer.AddFilter(checkBox2);
		
		singleSelect1.UnCheck(); // is already zero/false so nothing is notified
		Assert.assertEquals(1, listener.Notified); // we have the notification from checkBox1
		
		singleSelect1.ChangeState("1"); // should notify
		Assert.assertEquals(2, listener.Notified);
		listener.Notified = 0;
		
		// all other single select filter must be false
		Assert.assertEquals("false", singleSelect2.GetState());
		Assert.assertEquals("false", singleSelect3.GetState());
		Assert.assertEquals("true", checkBox1.GetState()); // this should not be affected
		
		singleSelect2.Check(); // other single select filters should be reset
		// the listener should fire one for the singleSelect2 state change and one for reseting singleSelect1
		Assert.assertEquals(2, listener.Notified);
		Assert.assertEquals("false", singleSelect1.GetState());
		Assert.assertEquals("false", singleSelect3.GetState());
		Assert.assertEquals("true", checkBox1.GetState()); // this should not be affected
		Assert.assertEquals("false", checkBox2.GetState()); // this should not be affected
		
		
	}
	
	private class MockFilterHubListener implements IFilterHubListener{

		public int Notified = 0;
		
		@Override
		public void FilterAdded(Filter filter) {
			System.out.println("MockFilterHubListener->FilterAdded:" + filter);
			Notified++;
		}

		@Override
		public void FilterRemoved(Filter filter) {
			System.out.println("MockFilterHubListener->FilterRemoved:" + filter);
			Notified++;
		}
		
	}
	
	
	private class MockSingleSelectFilter extends SingleSelectFilter{

		public MockSingleSelectFilter(IInvalidator invalidator, Object id, String name, INotifier notifier) {
			super(invalidator, id, name, notifier);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	private class MockCheckBokFilter extends CheckBoxFilter{

		public MockCheckBokFilter(Object id, String name, INotifier notifier) {
			super(id, name, notifier);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	private class MockFreeTextFilter extends FreeTextFilter{

		public MockFreeTextFilter(Object id, String name, INotifier notifier) {
			super(id, name, notifier);
			// TODO Auto-generated constructor stub
		}
		
	}

}
