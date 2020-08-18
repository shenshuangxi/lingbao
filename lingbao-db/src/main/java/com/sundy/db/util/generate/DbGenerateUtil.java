package com.sundy.db.util.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.entity.RevisionEntity;
import com.sundy.db.util.ClassUtil;
import com.sundy.lingbao.core.exception.LingbaoException;

public class DbGenerateUtil {

	private static Logger logger = LoggerFactory.getLogger(DbGenerateUtil.class);
	private static List<Class<?>> classes = new ArrayList<>();
	
	static{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL[] urls = ((URLClassLoader)classLoader).getURLs();
		for(URL url : urls){
			findAndAddClassesByUrl(url, classes,classLoader);
		}
	}
	
	private static void findAndAddClassesByUrl(URL url, List<Class<?>> classes, ClassLoader classLoader) {
		if(url.getProtocol().equals("file")) {
			findAndAddClassesByFile(url, classes,classLoader);
		}
	}

	private static void findAndAddClassesByFile(URL url, List<Class<?>> classes, ClassLoader classLoader) {
		File file = new File(url.getFile());
		findAndAddClassesByFile(file, file.getPath(), classes, classLoader);
	}

	private static void findAndAddClassesByFile(File file, String path, List<Class<?>> classes, ClassLoader classLoader) {
		if(file.isDirectory()){
			File[] childrenFiles = file.listFiles();
			for(File childrenFile : childrenFiles){
				findAndAddClassesByFile(childrenFile, path, classes, classLoader);
			}
		}else if(file.getName().endsWith(".class")){
			try {
				int deleteStartIndex = path.length();
				int deleteEndIndex = file.getPath().lastIndexOf(".class");
				String relativePath = file.getPath().substring(deleteStartIndex, deleteEndIndex);
				if(relativePath.startsWith(File.separator)){
					relativePath = relativePath.substring(1);
				}
				String className = relativePath.replace(File.separator, ".");
				Class<?> tempClass = classLoader.loadClass(className);
				if(PersistEntity.class.isAssignableFrom(tempClass) && PersistEntity.class!=tempClass && RevisionEntity.class!=tempClass && !tempClass.isInterface() && !Modifier.isAbstract(tempClass.getModifiers())) {
					classes.add(tempClass);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logger.info("加载所有实体类", e);
			}
		}
	}
	
	private static String changeUpcase(String str){  
	    StringBuffer sb = new StringBuffer();  
	    if(str!=null){  
	        for(int i=0;i<str.length();i++){  
	            char c = str.charAt(i);  
	            if(Character.isUpperCase(c)){  
	                sb.append("_"+c);  
	            } else {
	            	sb.append(c);
	            }
	        }  
	    }  
	    return sb.toString();  
	}
	
	public static File createFile(String path) throws IOException{
		if(path==null){
			return null;
		}
		File file = new File(path);
		if(!file.exists()){
			createDir(file.getParent());
			file.createNewFile();
		}
		return file;
	}
	
	public static void createDir(String path) throws IOException{
		if(path==null){
			return;
		}
		File file = new File(path);
		if(!file.exists()){
			createDir(file.getParent());
			file.mkdir();
		}
	}
	
	
	private static void generateSelectXml(String prefix, Class<?> clazz, String columnId, Map<String, Field> fieldMaps, StringBuilder sb) {
		//selectById
		sb.append("\t"+"<select id=\"select"+clazz.getSimpleName()+"\" parameterType=\"String\" resultMap=\"result"+clazz.getSimpleName()+"\">");
		sb.append("\n");
		sb.append("\t\t"+"select <include refid=\""+columnId+"\"/> from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " where id_ = #{id} ");
		sb.append("\n");
		sb.append("\t"+"</select>");
		sb.append("\n");
		sb.append("\n");
		//select Selective
		sb.append("\t"+"<select id=\"select"+clazz.getSimpleName()+"Selective\" parameterType=\""+clazz.getName()+"\" resultMap=\"result"+clazz.getSimpleName()+"\">");
		sb.append("\n");
		sb.append("\t\t"+"select <include refid=\""+columnId+"\"/> from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " \n");
		sb.append("\t\t<where> \n");
		if(fieldMaps.get("id")!=null){
			sb.append("\t\t\t"+"<if test=\"id != null\" >\n");
			sb.append("\t\t\t\t and id_=#{id} \n");
			sb.append("\t\t\t"+"</if>\n");
		}
		for (String key : fieldMaps.keySet()) {
			if(!key.equals("id")){
				sb.append("\t\t\t"+"<if test=\""+key+" != null\" >\n");
				sb.append("\t\t\t\t and "+changeUpcase(key).toLowerCase()+"_ = #{"+key+"}\n");
				sb.append("\t\t\t"+"</if>\n");
			}
		}
		sb.append("\t\t</where> \n");
		sb.append("\t"+"</select>");
		sb.append("\n");
		sb.append("\n");
		
		//select SelectiveCount
		sb.append("\t"+"<select id=\"select"+clazz.getSimpleName()+"SelectiveCount\" parameterType=\""+clazz.getName()+"\" resultType=\"long\">");
		sb.append("\n");
		sb.append("\t\t"+"select count(1) from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " \n");
		sb.append("\t\t<where> \n");
		if(fieldMaps.get("id")!=null){
			sb.append("\t\t\t"+"<if test=\"id != null\" >\n");
			sb.append("\t\t\t\t and id_=#{id} \n");
			sb.append("\t\t\t"+"</if>\n");
		}
		for (String key : fieldMaps.keySet()) {
			if(!key.equals("id")){
				sb.append("\t\t\t"+"<if test=\""+key+" != null\" >\n");
				sb.append("\t\t\t\t and "+changeUpcase(key).toLowerCase()+"_ = #{"+key+"}\n");
				sb.append("\t\t\t"+"</if>\n");
			}
		}
		sb.append("\t\t</where> \n");
		sb.append("\t"+"</select>");
		sb.append("\n");
		sb.append("\n");
		
		
		//select SelectiveQo
		sb.append("\t"+"<select id=\"select"+clazz.getSimpleName()+"SelectiveQo\" parameterType=\"map\" resultMap=\"result"+clazz.getSimpleName()+"\">");
		sb.append("\n");
		sb.append("\t\t"+"select <include refid=\""+columnId+"\"/> from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " \n");
		sb.append("\t\t<where> \n");
		if(fieldMaps.get("id")!=null){
			sb.append("\t\t\t"+"<if test=\"id != null\" >\n");
			sb.append("\t\t\t\t and id_=#{id} \n");
			sb.append("\t\t\t"+"</if>\n");
		}
		for (String key : fieldMaps.keySet()) {
			if(!key.equals("id")){
				sb.append("\t\t\t"+"<if test=\""+key+" != null\" >\n");
				sb.append("\t\t\t\t and "+changeUpcase(key).toLowerCase()+"_ = #{"+key+"}\n");
				sb.append("\t\t\t"+"</if>\n");
			}
		}
		sb.append("\t\t</where> \n");
		sb.append("\t\t\t"+"<if test=\"pageRequest != null\" >\n");
		sb.append("\t\t\t\t ${pageRequest} \n");
		sb.append("\t\t\t"+"</if>\n");
		sb.append("\t"+"</select>");
		sb.append("\n");
		sb.append("\n");
		
	}
	
	private static void generateDeleteSqlXml(String prefix, Class<?> clazz, Map<String, Field> fieldMaps, StringBuilder sb) {
		sb.append("\t"+"<delete id=\"delete"+clazz.getSimpleName()+"\" parameterType=\""+clazz.getName()+"\">");
		sb.append("\n");
		sb.append("\t\t"+"delete from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " where id_ = #{id} ");
		sb.append("\n");
		sb.append("\t"+"</delete>");
		sb.append("\n");
		sb.append("\n");
		
		
//		sb.append("\t"+"<delete id=\"delete"+clazz.getSimpleName()+"Selective\" parameterType=\""+clazz.getName()+"\">");
//		sb.append("\n");
//		sb.append("\t\t"+"delete from "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " \n");
//		sb.append("\t\t<where> \n");
//		if(fieldMaps.get("id")!=null){
//			sb.append("\t\t\t"+"<if test=\"id != null\" >\n");
//			sb.append("\t\t\t\t and id_=#{id} \n");
//			sb.append("\t\t\t"+"</if>\n");
//		}
//		for (String key : fieldMaps.keySet()) {
//			if(!key.equals("id")){
//				sb.append("\t\t\t"+"<if test=\""+key+" != null\" >\n");
//				sb.append("\t\t\t\t and "+changeUpcase(key).toLowerCase()+"_ = #{"+key+"}\n");
//				sb.append("\t\t\t"+"</if>\n");
//			}
//		}
//		sb.append("\t\t</where> \n");
//		sb.append("\t"+"</delete>");
//		sb.append("\n");
//		sb.append("\n");
	}
	
	
	private static StringBuilder generateUpdateSqlXml(String prefix, Class<?> clazz, Map<String, Field> fieldMaps, StringBuilder sb) {
		if(RevisionEntity.class.isAssignableFrom(clazz)){
			sb.append("\t"+"<update id=\"update"+clazz.getSimpleName()+"\" parameterType=\""+clazz.getName()+"\">");
			sb.append("\n");
			sb.append("\t\t"+"update "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase());
			sb.append("\n");
			sb.append("\t\t"+"set ");
			sb.append("\n");
			for (String key : fieldMaps.keySet()) {
				if(!key.equals("id")) {
					if(key.equals("revision")) {
						sb.append("\t\t\t"+changeUpcase(key).toLowerCase()+"_ = #{nextRevision},\n");
					} else if(key.equals("nextRevision")) {
						sb.append("\t\t\t"+changeUpcase(key).toLowerCase()+"_ = #{nextRevision}+1,\n");
					} else {
						sb.append("\t\t\t"+changeUpcase(key).toLowerCase()+"_ = #{"+key+"},\n");
					}
				}
			}
			sb= sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("\t\t"+"where revision_=#{revision} and id_ = #{id} ");
			sb.append("\n");
			sb.append("\t"+"</update>");
		} else {
			sb.append("\t"+"<update id=\"update"+clazz.getSimpleName()+"\" parameterType=\""+clazz.getName()+"\">");
			sb.append("\n");
			sb.append("\t\t"+"update "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase());
			sb.append("\n");
			sb.append("\t\t"+"set ");
			sb.append("\n");
			for (String key : fieldMaps.keySet()) {
				if(!key.equals("id")) {
					sb.append("\t\t\t"+changeUpcase(key).toLowerCase()+"_ = #{"+key+"},\n");
				}
			}
			sb= sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("\t\t"+"where id_ = #{id} ");
			sb.append("\n");
			sb.append("\t"+"</update>");
		}
		sb.append("\n");
		sb.append("\n");
		return sb;
	}

	
	private static void generateXml(String prefix) throws IOException{
		for (Class<?> clazz : classes) {
			Map<String,Field> fieldMaps = ClassUtil.findAllFieldMap(clazz);
			String columnId = "column"+clazz.getSimpleName().replace(".", "_").toLowerCase();
			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			sb.append("\n");
			sb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
			sb.append("\n");
			sb.append("<mapper namespace=\""+clazz.getName()+"\">");
			sb.append("\n");
			sb.append("\n");
			sb.append("\t"+"<sql id=\""+columnId+"\">");
			sb.append("\n");
			if(fieldMaps.get("id")!=null){
				sb.append("\t\t"+"id_"+",\n");
			}
			for (String key : fieldMaps.keySet()) {
				if(!key.equals("id")){
					sb.append("\t\t"+changeUpcase(key).toLowerCase()+"_"+",\n");
				}
			}
			sb= sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("\t"+"</sql>");
			sb.append("\n");
			sb.append("\n");
			
			sb.append("\t"+"<insert id=\"insert"+clazz.getSimpleName()+"\" parameterType=\""+clazz.getName()+"\">");
			sb.append("\n");
			sb.append("\t\t"+"insert into "+prefix+changeUpcase(clazz.getSimpleName()).toLowerCase()+ " ( <include refid=\""+columnId+"\"/> )");
			sb.append("\n");
			sb.append("\t\t"+"values (");
			sb.append("\n");
			if(fieldMaps.get("id")!=null){
				sb.append("\t\t\t"+"#{id},\n");
			}
			for (String key : fieldMaps.keySet()) {
				if(!key.equals("id")){
					sb.append("\t\t\t"+"#{"+key+"},\n");
				}
			}
			sb= sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append("\t\t"+")");
			sb.append("\n");
			sb.append("\t"+"</insert>");
			sb.append("\n");
			sb.append("\n");
			
			sb = generateUpdateSqlXml(prefix, clazz, fieldMaps, sb);
			generateDeleteSqlXml(prefix, clazz, fieldMaps, sb);
			generateSelectXml(prefix, clazz, columnId, fieldMaps, sb);
			
			sb.append("\t"+"<resultMap type=\""+clazz.getName()+"\" id=\"result"+clazz.getSimpleName()+"\">");
			sb.append("\n");
			if(fieldMaps.get("id")!=null){
				sb.append("\t\t"+"<result property=\"id\" column=\"id_\"/>\n");
			}
			for (String key : fieldMaps.keySet()) {
				if(!key.equals("id")) {
					sb.append("\t\t"+"<result property=\""+key+"\" column=\""+changeUpcase(key).toLowerCase()+"_"+"\"/>\n");
				}
			}
			sb.append("\t"+"</resultMap>");
			sb.append("\n");
			sb.append("\n");
			sb.append("</mapper>");
			
			File file = createFile("META-INF/db/mapper/"+clazz.getSimpleName()+".xml");
			FileWriter writer = null;
			try {
				writer = new FileWriter(file);
				writer.write(sb.toString());
			} finally {
				if(writer!=null){
					writer.close();
				}
			}
		}
	}

	

	

	private static void generateSql(String prefix) throws IOException{
		StringBuilder sb = new StringBuilder();
		for (Class<?> clazz : classes) {
			try {
				Map<String,Field> fieldMaps = ClassUtil.findAllFieldMap(clazz);
				sb.append("drop table if exists "+prefix+ changeUpcase(clazz.getSimpleName()).toLowerCase()+";");
				sb.append("\n");
				sb.append("create table "+prefix+ changeUpcase(clazz.getSimpleName()).toLowerCase()+" (");
				sb.append("\n");
				
				if(fieldMaps.get("id")!=null){
					sb.append("\tid_ "+DbConst.DbType.getDbTypeByClass(fieldMaps.get("id").getType()).getDbType()+",\n");
				}
				for (String key : fieldMaps.keySet()) {
					if(!key.equals("id")) {
						sb.append("\t"+changeUpcase(key).toLowerCase()+"_ "+DbConst.DbType.getDbTypeByClass(fieldMaps.get(key).getType()).getDbType()+",\n");
					}
				}
				sb.append("\t"+"primary key (id_)");
				sb.append("\n");
				sb.append(")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
				sb.append("\n");
				sb.append("\n");
			} catch (Throwable e) {
				e.printStackTrace();
				throw new LingbaoException(-1, clazz.getName());
			}
		}
		File file = createFile("META-INF/db/create.sql");
		if(!file.exists()){
			file.createNewFile();
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(sb.toString());
		} finally {
			if(writer!=null){
				writer.close();
			}
		}
	}
	
	public static void generateXmlSql(String prefix) throws IOException{
		generateXml(prefix);
		generateSql(prefix);
	}
	
}
