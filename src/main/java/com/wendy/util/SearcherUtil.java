/** Copyright&copy;2015-2016 alijk.com*/
package com.wendy.util;

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
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * <b>time:</b>2015年2月1日
 *
 * @author admin
 *
 */
public class SearcherUtil {
	private Directory directory;
	private IndexReader reader;
	private String[] ids = { "1", "2", "3", "4", "5", "6" };
	private String[] emails = { "aa@alijk.com", "bb@alijk.com", "cc@alijk.com",
			"dd@alijk.com", "ee@163.com", "ff@sina.com" };
	private String[] content = { "Welcome to visited the space",
			"my name is hello boy", "my name is cc,I like game",
			"I like football", "I like jettymike football and I like basketball too",
			"I like movie and swim" };
	private int[] attachs = { 2, 3, 1, 4, 5, 5 };
	private String[] name = { "zhangsan", "lisi", "john", "jetty", "mike",
			"jake" };
	private Map<String, Float> scores = new HashMap<String, Float>();
	private Date[] dates = {};

	public SearcherUtil() {
		directory = new RAMDirectory();
		scores.put("sina.com", 2.0f);
		scores.put("163.com", 1.5f);
		setDates();
		index();
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

	public IndexSearcher getSearcher() {
		try {
			if (reader == null) {
				reader = IndexReader.open(directory);
			} else {
				IndexReader tr = IndexReader.openIfChanged(reader);
				if (tr != null) {
					reader.close();
					reader = tr;
				}
			}
			return new IndexSearcher(reader);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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

	public void searchByTerm(String field, String name, int num) {
		IndexSearcher searcher = getSearcher();
		Query query = new TermQuery(new Term(field, name));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchByTermRange(String field, String start, String end,
			int num) {
		IndexSearcher searcher = getSearcher();
		Query query = new TermRangeQuery(field, start, end, true, true);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchByNumber(String field, int start, int end, int num) {
		IndexSearcher searcher = getSearcher();
		Query query = NumericRangeQuery.newIntRange(field, start, end, true,
				true);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchByPrex(String field, String value, int num) {
		IndexSearcher searcher = getSearcher();
		Query query = new PrefixQuery(new Term(field, value));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通配符搜索
	 * 
	 * @param field
	 * @param value
	 * @param num
	 */
	public void searchByWildcard(String field, String value, int num) {
		IndexSearcher searcher = getSearcher();
		Query query = new WildcardQuery(new Term(field, value));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchByPhrase(int num) {
		IndexSearcher searcher = getSearcher();
		PhraseQuery query = new PhraseQuery();
		query.setSlop(1);// 设置跳数
		query.add(new Term("content", "i"));
		query.add(new Term("content", "football"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchByBooleanQuery(int num) {
		IndexSearcher searcher = getSearcher();
		BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term("name", "zhangsan")), Occur.MUST);
		query.add(new TermQuery(new Term("content", "like")), Occur.MUST_NOT);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
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
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void queryParse(String keyword, String field, int num) {
		IndexSearcher searcher = getSearcher();
		QueryParser parser = new QueryParser(Version.LUCENE_35, field,
				new StandardAnalyzer(Version.LUCENE_35));
		try {

			Query query = parser.parse(keyword);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			TopDocs tds = searcher.search(query, num);
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(Long.parseLong(doc.get("date")));
				System.out.println(doc.get("name") + "[" + doc.get("email")
						+ "," + doc.get("id") + "," + doc.get("attache") + ","
						+ sdf.format(c.getTime()) + "," + doc.get("content")
						+ "]" + "===>docnum:" + sd.doc);

			}
			searcher.close();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void multifiledQueryParse(String keyword, int num, String... fileds) {
		IndexSearcher searcher = getSearcher();
		MultiFieldQueryParser parser = new MultiFieldQueryParser(
				Version.LUCENE_35, fileds, new StandardAnalyzer(
						Version.LUCENE_35));
		try {

			Query query = parser.parse(keyword);
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			TopDocs tds = searcher.search(query, num);
			System.out.println("搜索语句:"+query.toString());
			System.out.println("共查询出 [" + tds.totalHits + "] 条");
			for (ScoreDoc sd : tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(Long.parseLong(doc.get("date")));
				System.out.println(doc.get("name") + "[" + doc.get("email")
						+ "," + doc.get("id") + "," + doc.get("attache") + ","
						+ sdf.format(c.getTime()) + "," + doc.get("content")
						+ "]" + "===>docnum:" + sd.doc);

			}
			searcher.close();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
