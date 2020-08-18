package com.sundy.db.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassUtil {

	private static Logger logger = LoggerFactory.getLogger(ClassUtil.class);
	
	
	public static <T> List<Class<?>> getClassByClassType(Class<T> clazz,String ...packages){
		List<Class<?>> classes = new ArrayList<>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader)classLoader).getURLs();
		for(URL url : urls){
			findAndAddClassesByPath(url, classes,classLoader,clazz,packages);
		}
		return classes;
	}
	
	private static void findAndAddClassesByPath(URL url, List<Class<?>> classes, ClassLoader classLoader, Class<?> clazz, String ...packages) {
		if(url.getProtocol().equals("file")&&url.getPath().toLowerCase().endsWith(".jar")){
			findAndAddClassesByUrlJar(url, classes,classLoader,clazz, packages);
		}else if(url.getProtocol().equals("file")) {
			findAndAddClassesByFile(url, classes,classLoader,clazz, packages);
		}
		
	}
	
	
	private static void findAndAddClassesByUrlJar(URL url, List<Class<?>> classes, ClassLoader classLoader, Class<?> clazz, String ...packages){
		try {
			JarFile jarFile = new JarFile(URLDecoder.decode(url.getFile(), "utf-8"));
			findClassInJarFile(classes, classLoader, jarFile, clazz, packages);
		} catch (Throwable e) {
			e.printStackTrace();
			logger.info("加载所有实体类", e);
		}
	}


	private static void findClassInJarFile(List<Class<?>> classes, ClassLoader classLoader, JarFile jarFile, Class<?> clazz, String ...packages){
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		JarEntry jarEntry = null;
		try {
			while (jarEntries.hasMoreElements()) {
				jarEntry = jarEntries.nextElement();
				String jarEntryName = jarEntry.getName();
				if (jarEntry.getName().endsWith(".class")) {
					int deleteStrIndex = jarEntryName.lastIndexOf(".class");
					String className = jarEntryName.substring(0, deleteStrIndex).replace("/", ".");
					if(packages!=null){
						for(String pack : packages){
							if(className.startsWith(pack)){
								Class<?> tempClass = classLoader.loadClass(className);
								addClass(classes, clazz, tempClass);
							}
						}
					} else {
						Class<?> tempClass = classLoader.loadClass(className);
						addClass(classes, clazz, tempClass);
					}
					
				}
			}
		} catch(Throwable e){
			logger.info("加载所有实体类", e);
		} finally {
			try {
				jarFile.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.info("加载所有实体类", e);
			}
		}
	}

	
	
	
	private static <T> void findAndAddClassesByFile(URL url, List<Class<?>> classes, ClassLoader classLoader,Class<T> clazz, String ...packages){
		File file = new File(url.getFile());
		findAndAddClassesByFile(file, file.getPath(), classes, classLoader,clazz,packages);
	}
	
	private static <T> void findAndAddClassesByFile(File file,String rootName, List<Class<?>> classes, ClassLoader classLoader,Class<T> clazz,String ...packages){
		if(file.isDirectory()){
			File[] childrenFiles = file.listFiles();
			for(File childrenFile : childrenFiles){
				findAndAddClassesByFile(childrenFile, rootName, classes, classLoader,clazz,packages);
			}
		}else if(file.getName().endsWith(".class")){
			try {
				int deleteStartIndex = rootName.length();
				int deleteEndIndex = file.getPath().lastIndexOf(".class");
				String relativePath = file.getPath().substring(deleteStartIndex, deleteEndIndex);
				if(relativePath.startsWith(File.separator)){
					relativePath = relativePath.substring(1);
				}
				String className = relativePath.replace(File.separator, ".");
				if(packages!=null){
					for(String pack : packages){
						if(className.startsWith(pack)){
							Class<?> tempClass = classLoader.loadClass(className);
							addClass(classes, clazz, tempClass);
						}
					}
				} else {
					Class<?> tempClass = classLoader.loadClass(className);
					addClass(classes, clazz, tempClass);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logger.info("加载所有实体类", e);
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	private static void addClass(List<Class<?>> classes, Class<?> clazz, Class<?> tempClass) {
		if(clazz.isAnnotation()){
			if(tempClass.isAnnotationPresent((Class<? extends Annotation>) clazz)){
				classes.add(tempClass);
			}
		} else {
			if(clazz.isAssignableFrom(tempClass)){
				classes.add(tempClass);
			}
		}
	}

	

	public static List<Field> findAllField(Class<?> clazz){
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		Class<?> superClass = clazz.getSuperclass();
		while(superClass!=null){
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		return fields;
	}
	
	public static List<Method> findAllMethod(Class<?> clazz){
		List<Method> methods = new ArrayList<Method>();
		methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		Class<?> superClass = clazz.getSuperclass();
		while(superClass!=null){
			methods.addAll(Arrays.asList(superClass.getDeclaredMethods()));
			superClass = superClass.getSuperclass();
		}
		return methods;
	}
	
	public static Map<String,Field> findAllFieldMap(Class<?> clazz){
		Map<String,Field> fieldMaps = new LinkedHashMap<String,Field>();
		for(Field field : clazz.getDeclaredFields()){
			fieldMaps.put(field.getName(), field);
		}
		Class<?> superClass = clazz.getSuperclass();
		if(superClass!=null){
			fieldMaps.putAll(findAllFieldMap(superClass));
		}
		return fieldMaps;
	}
	
	
}
