package com.wendy.util;

import org.junit.Before;
import org.junit.Test;

/**
 * <b>time:</b>2015年2月1日
 *
 * @author admin
 *
 */
public class SearcherUtilTest {
	private SearcherUtil util;

	@Before
	public void init() {
		util = new SearcherUtil();
	}

	@Test
	public void testSearchByTerm() {
		util.searchByTerm("name", "jetty", 10);
	}
	@Test
	public void testSearchByRangeTerm(){
		util.searchByTermRange("id", "1", "3", 10);
		System.out.println("-------------------------------------");
		util.searchByTermRange("name", "a", "m", 10);
	}
	@Test
	public void testSearchByNumber(){
		util.searchByNumber("attache", 2, 3, 10);
	}
	@Test
	public void testSearchByPrex(){
		util.searchByPrex("name", "j", 10);
		System.out.println("-----------------------------------");
		util.searchByPrex("content", "s", 10);
		
	}
	@Test
	public void testSearchByWildcard(){
		util.searchByWildcard("name", "j????", 10);
		System.out.println("-----------------------------------");
		util.searchByWildcard("email", "*sina*", 10);
		System.out.println("-----------------------------------");
		util.searchByWildcard("content", "*name*", 10);
		
	}
	@Test
	public void testSearchBybooleanQuery(){
		util.searchByBooleanQuery(10);
	}
	
	@Test
	public void testSearchByPhrase(){
		util.searchByPhrase(10);
	}
	
	@Test
	public void testQueryParse(){
		util.queryParse("jetty","name", 10);
	}
	
	@Test 
	public void testMultifiledQueryParse(){
		util.multifiledQueryParse("jetty*", 10, new String[]{"name","content"});
	}

}
