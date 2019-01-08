package com.game.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {
	
	public  boolean saveFile(String path,String fileName,File ObjFile)throws Exception{
		File f=new File(path);
	    f.mkdirs();
		new File(path+File.separator+fileName).createNewFile();
		return copyNIO(new File(path+File.separator+fileName),ObjFile);
	}

	public static void saveFile(String path,String fileName,String content){
		File file = FileUtil.createNewFile(fileName, path);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.getBytes("utf-8"));
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * NIO 方式拷贝文件
	 * @param objectiveFile
	 * @param resourse
	 * @return
	 */
	private  boolean copyNIO(File objectiveFile,File resourse){
		FileInputStream fis = null;
		FileOutputStream fos = null;
		boolean flag=true;
		try {
			fis = new FileInputStream(resourse);
			fos = new FileOutputStream(objectiveFile);
			FileChannel fic = fis.getChannel();
			FileChannel foc = fos.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true) {
				buffer.clear();   
				int r = fic.read(buffer);   
				if (r == -1) {   
			      break;   
				}   
				// flip方法让缓冲区可以将新读入的数据写入另一个通道   
				buffer.flip();   
			    // 从输出通道中将数据写入缓冲区   
				foc.write(buffer);   
			}
		} catch (FileNotFoundException e) {
			flag = false;
			e.printStackTrace();
		} catch (IOException e) {
			flag = false;
			e.printStackTrace();
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fos) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	
	/**
	 * 在指定目录下创建一个空文件
	 * @param fileName 文件全名
	 * @param path 目录
	 * @return File
	 */
	public static  File createNewFile(String fileName,String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		file = new File(file.getAbsolutePath(),fileName);
		if(!file.exists()){
			try {
				file.createNewFile();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			return file;
		}
		return null;
	}
	
	/**
	 * 读取文件
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static String readFileText(String path,String fileName){
		File file = new File(path+File.separator+fileName);
		if(!file.exists())return null;
		FileInputStream fis = null;
		InputStreamReader read = null;
		BufferedReader bread = null;
		StringBuilder sb = new StringBuilder();
		try{
			fis = new FileInputStream(file);	
			read = new InputStreamReader(fis, "utf-8");
			bread = new BufferedReader(read);
			String lineTxt = null;
	        while ((lineTxt = bread.readLine()) != null) {
	               sb.append(lineTxt);
	         }       
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			try {
				if(bread != null)bread.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(read != null)read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(fis != null)fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
