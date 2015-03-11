package com.wendy.util;

import org.junit.Test;

public class IndexUtilsTest {
	
	
	@Test
	public void testIndex(){
		IndexUtils utils = new IndexUtils();
		utils.index();
	}
	@Test
	public void testQuery(){
		IndexUtils utils = new IndexUtils();
		utils.query();
	}
	@Test
	public void testDelete(){
		IndexUtils utils = new IndexUtils();
		utils.delete();
	}
	@Test
	public void testUndelete(){
		IndexUtils utils = new IndexUtils();
		utils.undelete();
	}
	@Test
	public void testForceMerge(){
		IndexUtils utils = new IndexUtils();
		utils.merge();
	}
	@Test
	public void testForceMergeDelete(){
		IndexUtils utils = new IndexUtils();
		utils.forceDelete();
	}
	@Test
	public void testUpdate(){
		IndexUtils utils = new IndexUtils();
		utils.update();
	}
	
	@Test
	public void testSearcher(){
		IndexUtils utils = new IndexUtils();
		utils.search();
	}

}
