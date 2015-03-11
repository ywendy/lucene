package com.wendy.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 * <strong>time:</strong> 2015-02-01
 * @author admin
 * 
 *
 */
/*8
 * 
 */
/**
 * 
*<b>time:</b>2015年2月1日
*@author admin
*
 */
public class IndexUtils {
	private String[] ids = { "1", "2", "3", "4", "5", "6" };
	private String[] emails = { "aa@alijk.com", "bb@alijk.com", "cc@alijk.com",
			"dd@alijk.com", "ee@163.com", "ff@sina.com" };
	private String[] content = { "Welcome to visited the space",
			"my name is hello boy", "my name is cc,I like game",
			"I like football", "I like football and I like basketball too",
			"I like movie and swim" };
	private int[] attachs = { 2, 3, 1, 4, 5, 5 };
	private String[] name = { "zhangsan", "lisi", "john", "jetty", "mike",
			"jake" };
	private Map<String, Float> scores = new HashMap<String, Float>();
	private Directory directory = null;
	private Date[] dates = {};

	private static IndexReader reader = null;

	public IndexUtils() {
		try {
			directory = FSDirectory.open(new File(
					"E:/mystudy/lucene/35/index02"));
			scores.put("sina.com", 2.0f);
			scores.put("163.com", 1.5f);
			setDates();
			reader = IndexReader.open(directory);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IndexSearcher getSearcher() {
		//只有一个reader ,writer 为一个，commit提交
		try {
			if (reader == null) {
				reader = IndexReader.open(directory);
			} else {
				IndexReader ir = IndexReader.openIfChanged(reader);
				if (ir != null){
					reader.close();
					reader = ir;
				}
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new IndexSearcher(reader);
	}

	private void setDates() {
		SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getInstance();
		sdf.applyPattern("yyyy-MM-dd");
		dates = new Date[ids.length];
		try {
			dates[0] = sdf.parse("2012-11-12");
			dates[1] = sdf.parse("2012-11-13");
			dates[2] = sdf.parse("2012-11-14");
			dates[3] = sdf.parse("2012-11-15");
			dates[4] = sdf.parse("2012-11-16");
			dates[5] = sdf.parse("2012-11-17");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			/*
			 * lucene没有提供更新，更新分为两步：1删除，2添加
			 */
			Document doc = new Document();
			doc = new Document();
			doc.add(new Field("id", "11", Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email", emails[1], Field.Store.YES,
					Field.Index.NOT_ANALYZED));
			doc.add(new Field("content", content[1], Field.Store.NO,
					Field.Index.ANALYZED));
			doc.add(new Field("name", name[1], Field.Store.YES,
					Field.Index.NOT_ANALYZED_NO_NORMS));
			writer.updateDocument(new Term("id", "1"), doc);

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void merge() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			// 不建议使用了35以后
			writer.forceMerge(2);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void forceDelete() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.forceMergeDeletes();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/***
	 * 恢复.
	 */
	public void undelete() {
		try {
			reader = IndexReader.open(directory, false);
			reader.undeleteAll();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public void delete() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));

			// 参数是一个选项，可以是一个query，也可以是一个term（精确查找出的值）
			// 删除的文档不会完全删除，而是存储在一个类似回收站的文件中（.del）可以恢复回来
			writer.deleteDocuments(new Term("id", "1"));
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void query() {
		try {
			reader = IndexReader.open(directory);
			System.out.println("numDocs:" + reader.numDocs());
			System.out.println("maxDocs:" + reader.maxDoc());
			System.out.println("deleteDocs:" + reader.numDeletedDocs());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}

	public void index() {
		IndexWriter writer = null;

		try {
			writer = new IndexWriter(directory, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.deleteAll();
			Document doc = null;
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id", ids[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email", emails[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", content[i], Field.Store.NO,
						Field.Index.ANALYZED));
				doc.add(new Field("name", name[i], Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));
				// 整形
				doc.add(new NumericField("attache", Field.Store.YES, true)
						.setIntValue(attachs[i]));
				// 日期
				doc.add(new NumericField("date", Field.Store.YES, true)
						.setLongValue(dates[i].getTime()));
				String et = emails[i].substring(emails[i].lastIndexOf("@") + 1);
				if (scores.containsKey(et)) {
					doc.setBoost(scores.get(et));
				}
				writer.addDocument(doc);
			}

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void search() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			IndexSearcher searcher = new IndexSearcher(reader);
			TermQuery query = new TermQuery(new Term("content", "like"));
			TopDocs tds = searcher.search(query, 10);
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(Long.parseLong(doc.get("date")));
				System.out.println(doc.get("name") + "[" + doc.get("email")
						+ "," + doc.get("id") + "," + doc.get("attache") + ","
						+ sdf.format(c.getTime()) + "]" + "===>docnum:"
						+ sd.doc);
			}
			searcher.close();

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

}
