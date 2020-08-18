package com.sundy.lingbao.file.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TestLucene {

	public static void main(String[] args) {
		IndexWriter indexWriter = null;
		try {
			Directory directory = FSDirectory.open(new File("doc\\lucene").toPath());
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			
			indexWriter = new IndexWriter(directory, indexWriterConfig);
			
			
			
			File dataDirectory = new File("doc\\datafiles");
			if (dataDirectory.isDirectory()) {
				File[] files = dataDirectory.listFiles();
				for (File file : files) {
					Document document = new Document();
					indexWriter.addDocument(document);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (indexWriter != null) {
				try {
					indexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		
	}
	
}
