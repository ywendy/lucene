package com.wendy.lucene01;

import org.junit.Test;

public class HelloLuceneTest {
	
	
	@Test
	public void testIndex(){
		HelloLucene hello = new HelloLucene();
		hello.index();
	}
	@Test
	public void testSearcher(){
		HelloLucene hello = new HelloLucene();
		hello.searcher();
	}

}
