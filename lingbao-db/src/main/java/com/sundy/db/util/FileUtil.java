package com.sundy.db.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class FileUtil {

	private final static String  CLASS_PATH_PREFIX = "classpath:";
	private final static String  CLASS_PATH_SUFFIX_PREFIX = "classpath*:";
	
	
	public static void getAllFile(List<URL> fileUrls) throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader)classLoader).getURLs();
		for(URL url : urls){
			if(url.getProtocol().equals("file")&&url.getPath().toLowerCase().endsWith(".jar")){
				findFileInJar(url,fileUrls);
			}else if(url.getProtocol().equals("file")) {
				findFile(url.getPath(), fileUrls);
			}
		}
	}
	
	private static void findFileInJar(URL jarUrl, Collection<URL> fileUrls) throws Exception{
		JarFile jarFile = new JarFile(URLDecoder.decode(jarUrl.getFile(), "utf-8"));
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		System.out.println(jarFile.getName());
		JarEntry jarEntry = null;
		try {
			while (jarEntries.hasMoreElements()) {
				jarEntry = jarEntries.nextElement();
				if(!jarEntry.isDirectory()){
					String jarEntryName = jarEntry.getName();
					URL url = new URL("jar:file:" + jarUrl.getPath() + "!/" + jarEntryName);
					fileUrls.add(url);
				}
			}
		} finally {
			try {
				jarFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void findFile(String path, Collection<URL> fileUrls) throws Exception{
		File file = new File(path);
		if(file.isDirectory()){
			File[] listFiles = file.listFiles();
			for(File temp : listFiles){
				findFile(temp.getPath(),fileUrls);
			}
		} else {
			fileUrls.add(file.toURI().toURL());
		}
	}
	
	public static void main(String[] args) {
		try {
			URL[] urls = getURLs("classpath*:META-INF/db/mapper/*.xml");
			System.out.println(urls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static URL[] getURLs(String path) throws Exception{
		if(path.indexOf(CLASS_PATH_SUFFIX_PREFIX)!=-1){
			String locationPattenr = path.substring(CLASS_PATH_SUFFIX_PREFIX.length());
			if(locationPattenr.indexOf("*")!=-1){
				return findURLsInJarAndLocalWithPatternPath(locationPattenr);
			} else {
				return findURLsInJarAndLocalWithPath(locationPattenr);
			}
		} else if(path.indexOf(CLASS_PATH_PREFIX)!=-1) {
			String locationPattenr = path.substring(CLASS_PATH_PREFIX.length());
			if(locationPattenr.indexOf("*")!=-1){
				return findURLsInLocalWithPatternPath(locationPattenr);
			} else {
				return findURLsInLocalWithPath(locationPattenr);
			}
		} else {
			return new URL[]{new File(path).toURI().toURL()};
		}
	}



	private static URL[] findURLsInLocalWithPath(String locationPattenr) throws Exception {
		List<URL> urlList = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource("");
		String pattern = url.getPath()+locationPattenr+"[\\S]*";
		findURLsInLocale(url.getPath(), pattern, urlList);
		return urlList.toArray(new URL[]{});
	}
	
	private static URL[] findURLsInLocalWithPatternPath(String locationPattenr) throws Exception {
		List<URL> urlList = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource("");
		String[] patterns = locationPattenr.split("\\*");
		StringBuilder patternBuilder = new StringBuilder();
		patternBuilder.append(url.getPath());
		for(String pattern : patterns){
			patternBuilder.append(pattern+"[\\S]*");
		}
		String pattern = patternBuilder.toString();
		findURLsInLocale(url.getPath(), pattern, urlList);
		return urlList.toArray(new URL[]{});
	}

	private static void findURLsInLocale(String path, String pattern, List<URL> urlList) throws Exception {
		File file = new File(path);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File temp : files){
				findURLsInLocale(temp.getPath(), pattern, urlList);
			}
		} else {
			String urlPath = file.toURI().toURL().getPath();
			if(urlPath.matches(pattern)){
				urlList.add(file.toURI().toURL());
			}
		}
	}
	
	private static void findURLsInJar(URL jarUrl, String pattern, List<URL> urlList) throws Exception {
		JarFile jarFile = new JarFile(URLDecoder.decode(jarUrl.getFile(), "utf-8"));
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		JarEntry jarEntry = null;
		try {
			while (jarEntries.hasMoreElements()) {
				jarEntry = jarEntries.nextElement();
				if(!jarEntry.isDirectory()&&jarEntry.getName().matches(pattern)){
					String jarEntryName = jarEntry.getName();
					URL url = new URL("jar:file:" + jarUrl.getPath() + "!/" + jarEntryName);
					urlList.add(url);
				}
			}
		} finally {
			try {
				jarFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	private static URL[] findURLsInJarAndLocalWithPath(String locationPattenr) throws Exception {
		List<URL> urlList = new ArrayList<>();
		urlList.addAll(Arrays.asList(findURLsInLocalWithPath(locationPattenr)));
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader)classLoader).getURLs();
		for(URL url : urls){
			if(url.getProtocol().equals("file")&&url.getPath().toLowerCase().endsWith(".jar")){
				findURLsInJar(url,  locationPattenr, urlList);
			}
		}
		return urlList.toArray(new URL[]{});
	}

	private static URL[] findURLsInJarAndLocalWithPatternPath(String locationPattenr) throws Exception {
		List<URL> urlList = new ArrayList<>();
		urlList.addAll(Arrays.asList(findURLsInLocalWithPatternPath(locationPattenr)));
		
		String[] patterns = locationPattenr.split("\\*");
		StringBuilder patternBuilder = new StringBuilder();
		for(String pattern : patterns){
			patternBuilder.append(pattern+"[\\S]*");
		}
		String pattern = patternBuilder.toString();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader)classLoader).getURLs();
		for(URL url : urls){
			if(url.getProtocol().equals("file")&&url.getPath().toLowerCase().endsWith(".jar")){
				findURLsInJar(url,  pattern, urlList);
			}
		}
		return urlList.toArray(new URL[]{});
	}
	
}
