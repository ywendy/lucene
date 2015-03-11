package com.wendy.lucene01;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class HelloLucene {

	/**
	 * 创建索引.
	 */
	public void index() {

		IndexWriter writer = null;
		Directory dir = null;
		try {
			// 1、创建Directory(在内存中创建索引使用new RAMDirectory())
			dir = FSDirectory.open(new File("E:/mystudy/lucene/35/index"));

			// 2、创建IndexWriter
			writer = new IndexWriter(dir, new IndexWriterConfig(
					Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));

			File files = new File("E:/mystudy/lucene/35/file01");

			// 3、创建Document对象
			Document doc = null;
			for (File file : files.listFiles()) {
				doc = new Document();

				// 4、为document对象添加Field
				doc.add(new Field("name", file.getName(), Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("content", new FileReader(file)));
				doc.add(new Field("path", file.getAbsolutePath(),
						Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

				// 5、通过IndexWriter 添加文档到索引文件中
				writer.addDocument(doc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (dir != null) {
				try {
					dir.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 搜索.
	 */
	public void searcher() {
		// 1、创建Directory
		Directory dir = null;
		IndexReader reader = null;
		IndexSearcher searcher = null;
		try {
			// 1、创建Directory
			dir = FSDirectory.open(new File("E:/mystudy/lucene/35/index"));
			// 2、创建IndexReader
			reader = IndexReader.open(dir);
			// 3、根据IndexReader创建IndexSearch
			searcher = new IndexSearcher(reader);

			// 4、创建搜索的Query
			QueryParser parser = new QueryParser(Version.LUCENE_35, "content",
					new StandardAnalyzer(Version.LUCENE_35));
			
			// 搜索内容包含ClientDetails的文档
			Query query = parser.parse("ClientDetails");
			
			// 5、根据searcher搜索返回TopDocs
			TopDocs tds = searcher.search(query, 10);
			
			// 6、根据TopDocs获取ScoreDocs
			ScoreDoc[] sds = tds.scoreDocs;
			for (ScoreDoc scoreDoc : sds) {
				// 7、根据searcher对象获取document对象
				Document doc = searcher.doc(scoreDoc.doc);
				System.out.println(doc.get("name") + "---" + doc.get("path"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if(searcher!=null){
				try {
					searcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dir != null) {
				try {
					dir.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
