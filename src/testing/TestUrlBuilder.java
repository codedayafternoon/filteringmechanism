package testing;

import org.junit.Assert;
import org.junit.Test;

import application.infrastructure.UrlBuilder;

public class TestUrlBuilder {

	@Test
	public void testAddParameter() {
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");

		urlBuilder.AddParameter("name1", "x1");

		Assert.assertEquals(urlBuilder.Peek(), "name1=x1");
		Assert.assertFalse(urlBuilder.Peek().contains("&"));

		urlBuilder.AddParameter("name2", "x2");

		Assert.assertTrue(urlBuilder.Peek().contains("name1=x1"));
		Assert.assertTrue(urlBuilder.Peek().contains("name2=x2"));

	}

	@Test
	public void testAddThenRemoveParameter() {
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");

		urlBuilder.AddParameter("name1", "x1");

		Assert.assertEquals(urlBuilder.Peek(), "name1=x1");

		urlBuilder.RemoveParameter("name2");

		Assert.assertTrue(urlBuilder.Peek().contains(""));
		Assert.assertFalse(urlBuilder.Peek().contains("&"));
	}

	@Test
	public void testAddTwoThenRemoveOneParameter() {
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");

		urlBuilder.AddParameter("name1", "x1");
		urlBuilder.AddParameter("name2", "x2");

		urlBuilder.RemoveParameter("name2");

		Assert.assertTrue(urlBuilder.Peek().contains("name1=x1"));
		Assert.assertFalse(urlBuilder.Peek().contains("&"));

	}
	
	@Test
	public void testAddSameValue() {
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");
		urlBuilder.AddParameter("param1", "x1");
		urlBuilder.AddParameter("param1", "x2");
		
		Assert.assertEquals("param1=x1,x2", urlBuilder.Peek());
		urlBuilder.AddParameter("param1", "x1");
		Assert.assertEquals("param1=x1,x2", urlBuilder.Peek());
		Assert.assertFalse(urlBuilder.Peek().contains("&"));
		
	}

	@Test
	public void testAddTwoValuesToSameParameterRemoveOne() {
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");

		urlBuilder.AddParameter("param1", "x1");
		urlBuilder.AddParameter("param1", "x2");
		Assert.assertEquals("param1=x1,x2", urlBuilder.Peek());

		urlBuilder.RemoveParameter("param1", "x1");
		Assert.assertEquals("param1=x2", urlBuilder.Peek());
		Assert.assertFalse(urlBuilder.Peek().contains("&"));

		urlBuilder.AddParameter("param1", "x3");
		urlBuilder.AddParameter("param1", "x4");
		Assert.assertEquals("param1=x2,x3,x4", urlBuilder.Peek());

		urlBuilder.RemoveParameter("param1", "x3");
		Assert.assertEquals("param1=x2,x4", urlBuilder.Peek());

		urlBuilder.RemoveParameter("param1", "x2");
		Assert.assertEquals("param1=x4", urlBuilder.Peek());
		Assert.assertFalse(urlBuilder.Peek().contains("&"));
	}

	@Test
	public void testAddRemoveDifferentParameters(){
		UrlBuilder urlBuilder = new UrlBuilder(",", "&");

		urlBuilder.AddParameter("param1", "x");
		urlBuilder.AddParameter("param1", "y");
		Assert.assertEquals("param1=x,y", urlBuilder.Peek());

		urlBuilder.AddParameter("param2", "x");
		Assert.assertTrue( urlBuilder.Peek().contains("param2=x"));
		Assert.assertTrue( urlBuilder.Peek().contains("param1=x,y") || urlBuilder.Peek().contains("param1=y,x"));

		urlBuilder.RemoveParameter("param1");
		Assert.assertFalse(urlBuilder.Peek().contains("&"));

	}

}
