package com.pintu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.pintu.ads.facade.AdConst;

public class FileToZip {
	
	/**
	 * 替换id 并 打包文件
	 * @param destFile 目的zip文件
	 * @param folderPath 要打包的文件目录
	 * @param venderId
	 * @param fileName 要替换的文件名
	 * @param version 
	 * @param dataSource 
	 */
	public String toZip(String destFile,String folderPath,String venderId, String version, String dataSource, String fileName ) {
		
		
		//首先根据版本来确定广告的宽度和高度
		String height = "";
		String width = "";
		if(version.equals(AdConst.FreeVersion)){
			height = AdConst.FreeHeight;
			width = AdConst.FreeWidth;
		}else if(version.equals(AdConst.StandardVersion)){
			height = AdConst.StandardHeight;
			width = AdConst.StandardWidth;
		}else if(version.equals(AdConst.UpgradeVersion)){
			height = AdConst.UpgradeHeight;
			width = AdConst.UpgradeWidth;
		}else if(version.equals(AdConst.AdvancedVersion)){
			height = AdConst.AdvancedHeight;
			width = AdConst.AdvancedWidth;
		}
		
		//替换文件中的指定内容
		ZipOutputStream zo;
		try {
			zo = new ZipOutputStream(new FileOutputStream(destFile));

			File targetFolder = new File(folderPath);

			if (targetFolder.exists() && targetFolder.isDirectory()) {

				File[] fileArray = targetFolder.listFiles();

				byte[] byt;
				
				for (File childFile : fileArray) {
					
					if(childFile.isDirectory()){
						
						//TODO 要改这里，如果是文件夹目录就读取再写到zip文件里
						dirToZip(targetFolder.getPath(), childFile, zo);
						
					}else{
						
						String childFileName = childFile.getName();
						//如果是需要替换的文件
						if(fileName.equals(childFileName)) {
							String content = FileUtils.readFileToString(childFile,"UTF-8");
							content = content.replace("###", venderId);
							//loca or network
							content = content.replace("@@@", dataSource);
							//free standard upgrade advanced
							content = content.replace("$$$", version);
							content = content.replace("^^^", width);
							content = content.replace("%%%", height);
							byt = content.getBytes("UTF-8");
						} else{
							byt = new byte[(int)childFile.length()];
							FileInputStream fi = new FileInputStream(childFile);
							fi.read(byt);
							fi.close();
							fi = null;
						}
						
						zo.putNextEntry(new ZipEntry(childFileName));
						zo.write(byt);
						
					}
				}
				zo.flush();
				zo.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			zo = null;
		}

		return destFile;
	}
	

	private static void fileToZip(String baseDirPath, File file,
			ZipOutputStream out) throws IOException {
	
		FileInputStream in = null;
		ZipEntry entry = null;
		// 创建复制缓冲区 1024*4 = 4K
		byte[] buffer = new byte[1024 * 4];
		int bytes_read = 0;
		if (file.isFile()) {
			in = new FileInputStream(file);
			// 根据 parent 路径名字符串和 child 路径名字符串创建一个新 File 实例
			String zipFileName = getEntryName(baseDirPath, file);
			entry = new ZipEntry(zipFileName);
			// "压缩文件" 对象加入 "要压缩的文件" 对象
			out.putNextEntry(entry);
			// 现在是把 "要压缩的文件" 对象中的内容写入到 "压缩文件" 对象
			while ((bytes_read = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytes_read);
			}
			out.closeEntry();
			in.close();
			//System.out.println("添加文件" + file.getAbsolutePath()+ "被添加到 ZIP 文件中!");
		}
	}
	
	private static void dirToZip(String baseDirPath, File dir,
			ZipOutputStream out) throws IOException {
		// 得到一个文件列表 (本目录下的所有文件对象集合)
		File[] files = dir.listFiles();
		/**
		 * 要是这个文件集合数组的长度为 0 , 也就证明了这是一个空的文件夹
		 * 
		 * 虽然没有再循环遍历它的必要,但是也要把这个空文件夹也压缩到目标文件中去
		 */
		if (files.length == 0) {
			// 根据 parent 路径名字符串和 child 路径名字符串创建一个新 File 实例
			String zipFileName = getEntryName(baseDirPath, dir);
			ZipEntry entry = new ZipEntry(zipFileName);
			out.putNextEntry(entry);
			out.closeEntry();
		} else {
			// 遍历所有的文件 一个一个地压缩
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isFile()) {
					// 调用本类的一个静态方法 压缩一个文件
					fileToZip(baseDirPath, file, out);
				} else {
					/*
					 * 这是一个文件夹 所以要再次得到它下面的所有的文件
					 * 这里是自己调用自己..............递归..........
					 */
					dirToZip(baseDirPath, file, out);
				}
			}
		}
	}
	
	private static String getEntryName(String baseDirPath, File file) {
		/**
		 * 改变 baseDirPath 的形式 把 "C:/temp" 变成 "C:/temp/"
		 */
		if (!baseDirPath.endsWith(File.separator)) {
			baseDirPath += File.separator;
		}
		String filePath = file.getAbsolutePath();
		/**
		 * 测试此抽象路径名表示的文件是否是一个目录。 要是这个文件对象是一个目录 则也要变成 后面带 "/"
		 * 
		 * 这个文件对象类似于 "C:/temp/人体写真/1.jpg"
		 * 
		 * 要是这个文件是一个文件夹 则也要变成 后面带 "/" 因为你要是不这样做,它也会被压缩到目标文件中 但是却不能正解显示
		 * 也就是说操作系统不能正确识别它的文件类型(是文件还是文件夹)
		 */
		if (file.isDirectory()) {
			filePath += "/";
		}
		int index = filePath.indexOf(baseDirPath);
		return filePath.substring(index + baseDirPath.length());
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileToZip zip = new FileToZip();
		zip.toZip("E://miniadplugin.zip","E://miniadplugin","1234567890","MiniAds.html","free","local");
	}


}
