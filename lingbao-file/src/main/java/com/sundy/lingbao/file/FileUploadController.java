package com.sundy.lingbao.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sundy.lingbao.file.constant.ContantConfig;
import com.sundy.lingbao.file.dto.Param;
import com.sundy.lingbao.file.message.ResponseMessage;
import com.sundy.lingbao.file.util.FileUtil;
import com.sundy.lingbao.file.util.MD5Util;

@RestController
@RequestMapping("/api/v1/upload")
public class FileUploadController {

	
	@PostMapping("/createDirectory") 
	public ResponseMessage createDirectory(String directory) {
		
		return new ResponseMessage();
	}
	
	@PostMapping("/cacheFileCheck")
	public ResponseMessage cacheFileCheck(Param param){
		//body true 表示不需上传
		ResponseMessage rspMessage = new ResponseMessage(true, "文件已存在");
		
		Map<String, Object> params = param.getParamMap();
		String fileName = (String) params.get("fileName");
		String fileCacheName = (String) (params.get("fileCacheName")==null?null:params.get("fileCacheName"));
		long fileCacheSize = params.get("fileCacheSize")==null?0l:Long.parseLong((String) params.get("fileCacheSize"));
		
		String base = ContantConfig.winDirPath;
		String separator = ContantConfig.fileSeparator;
		if(separator.equals("/")){
			base = ContantConfig.linuxDirPath;
		}
		
		if(fileName==null){
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("上传文件名参数空");
			return rspMessage;
		}
		
		if(fileCacheName==null){
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("上传缓存文件名参数空");
			return rspMessage;
		}
		
		if(fileCacheSize==0l){
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("上传缓存文件长度参数为空");
			return rspMessage;
		}
		
		try {
			String filePath = base+separator+MD5Util.encoderByMd5(fileName);
			if(!FileUtil.exist(filePath)){
				rspMessage.setIsSuccess(false);
				rspMessage.setMessage(fileName+" 文件 "+fileCacheName+"还没开始上传");
				return rspMessage;
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("服务端出错: "+e.getMessage());
			return rspMessage;
		};
		
		try {
			String cacheFilePath = base+separator+MD5Util.encoderByMd5(fileName)+separator+fileCacheName;
			if(!FileUtil.exist(cacheFilePath)){
				rspMessage.setIsSuccess(false);
				rspMessage.setMessage(fileName+" 文件 "+fileCacheName+"还没开始上传");
				return rspMessage;
			}else{
				File file = new File(cacheFilePath);
				long currentLength = file.length();
				if(currentLength==fileCacheSize){
					rspMessage.setIsSuccess(true);
					rspMessage.setMessage("文件已存在，不需再次上传");
					return rspMessage;
				}else{
					file.delete();
					rspMessage.setIsSuccess(false);
					rspMessage.setMessage("文件已存在，但长度不一致，需重新上传");
					return rspMessage;
				}
			}
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("服务端出错: "+e.getMessage());
			return rspMessage;
		}
	}
	
	@PostMapping("/fileCheck")
	public ResponseMessage fileCheck(Param param){
		//body true 表示不需上传
		ResponseMessage rspMessage = new ResponseMessage(true, "文件已存在");
		
		Map<String, Object> params = param.getParamMap();
		String fileName = (String) params.get("fileName");
		String base = ContantConfig.winDirPath;
		String separator = ContantConfig.fileSeparator;
		if(separator.equals("/")){
			base = ContantConfig.linuxDirPath;
		}
		
		if(fileName==null){
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("上传文件名参数空");
			return rspMessage;
		}
		
		//检查文件是否已经上传合并
		String filePath = base+separator+fileName;
		if(!FileUtil.exist(filePath)){
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage(fileName+" 文件不存在");
			return rspMessage;
		}
		return rspMessage;
	}
	
	@PostMapping("/fileMerge")
	public ResponseMessage fileMerge(Param param){
		ResponseMessage rspMessage = new ResponseMessage(true, "合并成功");
		try {
			Map<String, Object> params = param.getParamMap();
			String fileName = (String) params.get("fileName");
			String base = ContantConfig.winDirPath;
			String separator = ContantConfig.fileSeparator;
			if(separator.equals("/")){
				base = ContantConfig.linuxDirPath;
			}
			
			if(fileName==null){
				rspMessage.setIsSuccess(false);
				rspMessage.setMessage("上传文件名参数空");
				return rspMessage;
			}
			
			String targetFilePath = base+separator+fileName;
			File targetFile = new File(targetFilePath);
			
			String srcDirFilePath = base+separator+MD5Util.encoderByMd5(fileName);
			File srcDirFile = new File(srcDirFilePath);
			List<File> cacheFiles = new ArrayList<File>();
			long cacheFilesSize = 0l;
			if(srcDirFile.isDirectory()&&srcDirFile.exists()){
				for(File file : srcDirFile.listFiles()){
					cacheFilesSize = cacheFilesSize + file.length();
					cacheFiles.add(file);
				}
			}else{
				rspMessage.setIsSuccess(false);
				rspMessage.setMessage(fileName+"文件的缓存文件夹"+srcDirFilePath+"不能存在,或不是文件夹");
				return rspMessage;
			}
			
			RandomAccessFile wraf = new RandomAccessFile(targetFile, "rw");
			try {
				int count = 0;
				for(File cacheFile : cacheFiles){
					ByteBuffer buf = ByteBuffer.allocate(1024);
					RandomAccessFile rraf = new RandomAccessFile(cacheFile, "r");
					try {
						while(rraf.getChannel().read(buf)>0){
							buf.flip();
							wraf.getChannel().write(buf);
							buf.rewind();
						}
					} finally{
						rraf.close();
					}
					if(count!=0&&count%10==0){
						wraf.getChannel().force(true);
					}
					count++;
				}
			} finally{
				for(File file : srcDirFile.listFiles()){
					file.delete();
				}
				srcDirFile.delete();
				wraf.close();
			}
			return rspMessage;
		} catch (Exception e) {
			e.printStackTrace();
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage("服务端出错: "+e.getMessage());
			return rspMessage;
		}
	}
	
	@PostMapping("/cacheFileUpload")
	public ResponseMessage cacheFileUpload(Param param){
		
		ResponseMessage rspMessage = new ResponseMessage(true, "上传成功", true);
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存缓冲区，超过后写入临时文件
		factory.setSizeThreshold(10*1024*1024);
		// 设置临时文件存储位置
		String base = "d:\\uploadFiles";
		String separator = File.separator;
		if(separator.equals("/")){
			base = "/usr/local/src/upload";
		}
		File file = new File(base);
		if(!file.exists())
			file.mkdirs();
		factory.setRepository(file);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置单个文件的最大上传值
		upload.setFileSizeMax(10*1024*1024*1024l);
		// 设置整个request的最大值
		upload.setSizeMax(10*1024*1024*1024l);
		upload.setHeaderEncoding("UTF-8");
		try {
			List<?> items = upload.parseRequest((HttpServletRequest) param.getParamMap().get("request"));
			String filename = null;
			String cacheFilename = null;
			FileItem realItem = null;
			for (int i = 0 ;i < items.size(); i++){
				FileItem item = (FileItem) items.get(i);
				// 保存文件
				if (!item.isFormField() && item.getName().length() > 0) {
					filename =  item.getFieldName();
					realItem = item;
				}else{
					cacheFilename = item.getName()==null?item.getFieldName():item.getName();
				}
			}
			if(realItem!=null&&filename!=null&&cacheFilename!=null){
				String fileDir = base + File.separator + MD5Util.encoderByMd5(filename);
				File dirFile = new File(fileDir);
				if(!dirFile.exists()){
					dirFile.mkdir();
				}
				String cacheFilePath = fileDir + File.separator + cacheFilename;
				File cacheFile = new File(cacheFilePath);
				if(cacheFile.exists()){
					cacheFile.delete();
					cacheFile = new File(cacheFilePath);
				}
				realItem.write(cacheFile);
			}else{
				rspMessage.setIsSuccess(false);
				rspMessage.setMessage("服务器出错");
			}
		} catch (Exception e) {
			e.printStackTrace();
			rspMessage.setIsSuccess(false);
			rspMessage.setMessage(e.getMessage());
			rspMessage.setBody(e);
		}
		return rspMessage;
	}
	
	
}
