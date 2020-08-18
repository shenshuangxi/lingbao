package com.sundy.lingbao.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class GZipUtils {

	// 压缩  
    public static byte[] compress(byte[] data) throws IOException {  
        if (data == null || data.length == 0) {  
            return null;  
        }  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        GZIPOutputStream gzip = new GZIPOutputStream(out);  
        gzip.write(data);  
        gzip.close();  
        return  out.toByteArray();//out.toString("ISO-8859-1");  
    } 
      
    public static byte[] compress(String str) throws IOException {  
        if (str == null || str.length() == 0) {  
            return null;  
        }  
        return compress(str.getBytes("utf-8"));  
    }
    
    // 解压缩  
    public static byte[] uncompress(byte[] data) throws IOException {  
        if (data == null || data.length == 0) {  
            return data;  
        }  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        ByteArrayInputStream in = new ByteArrayInputStream(data);  
        GZIPInputStream gunzip = new GZIPInputStream(in);  
        byte[] buffer = new byte[256];  
        int n;  
        while ((n = gunzip.read(buffer)) >= 0) {  
            out.write(buffer, 0, n);  
        }  
        gunzip.close();  
        in.close();  
        return out.toByteArray();  
    }  
      
    public static String uncompress(String str) throws IOException {  
        if (str == null || str.length() == 0) {  
            return str;  
        }  
        byte[] data = uncompress(str.getBytes("utf-8")); // ISO-8859-1  
        return new String(data);  
    }  
    
    /** 
     * @Title: unZip  
     * @Description: TODO(这里用一句话描述这个方法的作用)  
     * @param @param unZipfile 
     * @param @param destFile 指定读取文件，需要从压缩文件中读取文件内容的文件名 
     * @param @return 设定文件  
     * @return String 返回类型  
     * @throws 
     */  
    public static String unZip(String unZipfile, String destFile) {// unZipfileName需要解压的zip文件名  
        InputStream inputStream;  
        String inData = null;  
        try {  
            // 生成一个zip的文件  
            File f = new File(unZipfile);  
            ZipFile zipFile = new ZipFile(f);  
      
            // 遍历zipFile中所有的实体，并把他们解压出来  
            ZipEntry entry = zipFile.getEntry(destFile);  
            if (!entry.isDirectory()) {  
                // 获取出该压缩实体的输入流  
                inputStream = zipFile.getInputStream(entry);  
      
                ByteArrayOutputStream out = new ByteArrayOutputStream();  
                byte[] bys = new byte[4096];  
                for (int p = -1; (p = inputStream.read(bys)) != -1;) {  
                    out.write(bys, 0, p);  
                }  
                inData = out.toString();  
                out.close();  
                inputStream.close();  
            }  
            zipFile.close();  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        }  
        return inData;  
    }  
    
    //解压gzip文件  
    public static boolean extractZip(File file, File parent) {  
        ZipFile zf = null;  
        try {  
            zf = new ZipFile(file);  
            Enumeration<? extends ZipEntry> entries = zf.entries();  
            if (entries == null)  
                return false;  
      
            final byte[] buf = new byte[256];  
      
            while (entries.hasMoreElements()) {  
                ZipEntry entry = entries.nextElement();  
                if (entry == null)  
                    continue;  
      
                if (entry.isDirectory()) {  
                    File dir = new File(parent, entry.getName());  
                    dir.mkdirs();  
                    continue;  
                }  
      
                File dstFile = new File(parent, entry.getName());  
                if (!dstFile.exists()) {  
                    dstFile.getParentFile().mkdirs();  
                }  
      
                InputStream fis = zf.getInputStream(entry);  
                BufferedInputStream bis = new BufferedInputStream(fis);  
                FileOutputStream fos = new FileOutputStream(dstFile);  
                BufferedOutputStream bos = new BufferedOutputStream(fos);  
                int read = 0;  
                while ((read = bis.read(buf)) > 0) {  
                    bos.write(buf, 0, read);  
                }  
                fis.close();  
                bis.close();  
                bos.close();  
                fos.close();  
            }  
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        } finally {  
            try {  
                zf.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
    
    /** 
     * 对将单个文件进行压缩 
     * @param source 源文件 
     * @param target 目标文件 
     * @throws IOException 
     */  
    public static void zipFile(String source, String target) throws IOException {  
        FileInputStream fin = null;  
        FileOutputStream fout = null;  
        GZIPOutputStream gzout = null;  
        try {  
            fin = new FileInputStream(source);  
            fout = new FileOutputStream(target);  
            gzout = new GZIPOutputStream(fout);  
            byte[] buf = new byte[1024];  
            int num;  
            while ((num = fin.read(buf)) != -1) {  
                gzout.write(buf, 0, num);  
            }  
        } finally {  
            if (gzout != null)  
                gzout.close();  
            if (fout != null)  
                fout.close();  
            if (fin != null)  
                fin.close();  
        }  
    }  
    
    //多个文件压缩成gzip文件  
    public static void mutileFileToGzip(ArrayList<String> filePaths, String targetFileName) {  
        try {  
            File file = new File(targetFileName);  
            FileOutputStream fout = new FileOutputStream(file);  
            BufferedInputStream bin = null;  
            ZipOutputStream zout = new ZipOutputStream(fout);  
            for (String fileSource : filePaths) {  
                String[] fileNames = fileSource.split("/");  
                zout.putNextEntry(new ZipEntry(fileNames[fileNames.length - 1]));  
                int c;  
                bin = new BufferedInputStream(new FileInputStream(fileSource));  
                while ((c = bin.read()) != -1) {  
                    zout.write(c);  
                }  
                bin.close();  
            }  
            zout.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        System.out.println("压缩成功！");  
      
    }  
	
}
