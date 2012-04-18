package com.pintu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 
 * ************************************************* 使用 ZipOutputStream
 * 可以实现文件压缩, 所有写入到 ZipOutputStream 输出流的数据都会被 ZIP 格式压缩!
 * 
 * 每个压缩的文件或者目录在 ZIP 文件中都对应一个 ZipEntry 对象, 每个 ZipEntry 都有一个name属性,表示它相对于 Zip
 * 文件根目录的相对路径, 对于目录,路径以"/"结尾! 对于文件,路径以文件名结尾!
 * 
 * ZipFile 表示一个 ZIP 文件, 它的 entries() 方法能获得ZIP文件中所有的ZipEntry集合
 * ZipEntry的isDirectory 方法判断该ZipEntry是否代表一个目录, getInputStream方法获得ZipEntry 的输入流
 * 
 */
public class ZipFolder {
	/**
	 * 
	 * ************************************************* 压缩文件 或者 目录
	 * 
	 * @param baseDirName
	 *            压缩的根目录
	 * @param fileName
	 *            根目录下待压缩的文件或文件夹名
	 * @param targetFileName
	 *            目标 ZIP 文件
	 * 
	 *            星号 "*" 表示压缩根目录下的全部文件
	 * 
	 */
	public static void zipFile(String baseDirName, String[] fileNames,
			String targetFileName) throws IOException {
		/**
		 * 由这个 "压缩的根目录" 文件名路径得到一个 File 对象 并判断这个 File 对象表示的文件是否存在! 是否是一个文件夹!.....
		 */
		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || (!baseDir.isDirectory())) {
			System.out.println("压缩失败! 根目录不存在: " + baseDirName);
			return;
		}
		// 得到这个 "压缩的根目录" 的路径.........
		String baseDirPath = baseDir.getAbsolutePath();
		/**
		 * 由这个 "目标 ZIP 文件" 文件名得到一个 压缩对象 ZipOutputStream
		 */
		File targetFile = new File(targetFileName);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				targetFile));
		// "*" 表示压缩包括根目录 baseDirName 在内的全部文件 到 targetFileName文件下
		if (fileNames[0].equals("*")) {
			ZipFolder.dirToZip(baseDirPath, baseDir, out);
		} else {
			File[] files = new File[fileNames.length];
			for (int i = 0; i < files.length; i++) {
				// 根据 parent 抽象路径名和 child 路径名字符串创建一个新 File 实例。
				files[i] = new File(baseDir, fileNames[i]);
			}
			if (files[0].isFile()) {
				// 调用本类的一个静态方法 压缩一个文件
				// ZipFolder.fileToZip(baseDirPath, file, out);
				ZipFolder.filesToZip(baseDirPath, files, out);
			}

		}
		out.close();
		System.out.println("压缩成功! 目标文件名为: " + targetFileName);
	}

	/**
	 * 
	 * ************************************************* 获取待压缩文件在 ZIP 文件中的 entry
	 * 的名字! 即相对于根目录的相对路径名 *************************************************
	 * 
	 */
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
	 * 
	 * ************************************************* 将文件压缩到Zip 输出流
	 * *************************************************
	 * 
	 * @author 周宇
	 * @throws IOException
	 * @Mar 4, 2008 11:12:24 PM
	 * 
	 */
	private static void fileToZip(String baseDirPath, File file,
			ZipOutputStream out) throws IOException {
		//
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
			System.out.println("添加文件" + file.getAbsolutePath()
					+ "被添加到 ZIP 文件中!");
		}
	}

	/**
	 * 
	 * *************************************************多个文件目录压缩到Zip 输出流
	 * *************************************************
	 */
	@SuppressWarnings("unused")
	private static void filesToZip(String baseDirPath, File[] files,
			ZipOutputStream out) throws IOException {
		// 遍历所有的文件 一个一个地压缩
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isFile()) {
				// 调用本类的一个静态方法 压缩一个文件
				ZipFolder.fileToZip(baseDirPath, file, out);
			} else {
				/*
				 * 这是一个文件夹 所以要再次得到它下面的所有的文件 这里是自己调用自己..............递归..........
				 */
				ZipFolder.dirToZip(baseDirPath, file, out);
			}
		}
	}

	/**
	 * 
	 * ************************************************* 将文件目录压缩到Zip 输出流
	 * *************************************************
	 */
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
					ZipFolder.fileToZip(baseDirPath, file, out);
				} else {
					/*
					 * 这是一个文件夹 所以要再次得到它下面的所有的文件
					 * 这里是自己调用自己..............递归..........
					 */
					ZipFolder.dirToZip(baseDirPath, file, out);
				}
			}
		}
	}

	/**
	 * 
	 * ************************************************* 这个方法是用来 解
	 * 压缩文件的.......... *************************************************
	 * 
	 */
	public static void upzipFile(String zipFileName, String targetBaseDirName)
			throws IOException {
		/**
		 * 改变 targetBaseDirName 的形式 把 "C:/temp.zip" 变成 "C:/temp.zip/"
		 */
		if (!targetBaseDirName.endsWith(File.separator)) {
			targetBaseDirName += File.separator;
		}
		// 根所 ZIP 文件创建 zipFileName 对象
		ZipFile zipFile = new ZipFile(zipFileName);
		ZipEntry entry = null;
		String entryName = null;
		String targetFileName = null;
		// 创建复制缓冲区 1024*4 = 4K
		byte[] buffer = new byte[1024 * 4];
		int bytes_read;
		// 由 ZipFile 对象 得到一个列举集合.......
		Enumeration entrys = zipFile.entries();
		// 遍历这个集合
		while (entrys.hasMoreElements()) {
			// 强制转换成 ZipEntry 对象
			entry = (ZipEntry) entrys.nextElement();
			// 得到 ZipEntry 对象的文件名子;
			entryName = entry.getName();
			/**
			 * C:/temp.zip/ + love.txt = C:/temp.zip/love.txt
			 */
			targetFileName = targetBaseDirName + entryName;
			// 如果 ZipEntry 是一个目录 则创建这个目录
			if (entry.isDirectory()) {
				new File(targetFileName).mkdirs();
				// 返回 并重新遍历..............
				continue;
			} else {
				/**
				 * 如果是一个普通文件 则创建这个文件的父目录以存储本文件
				 * 
				 * 比如 C:/temp.zip/other/love.txt 那么它就先创建 C:/temp.zip/other 文件目录
				 * 再往下执行以写入 love.txt 文件对象...........
				 * 
				 * 接着往下执行.............
				 */
				new File(targetFileName).getParentFile().mkdirs();
			}
			File targetFile = new File(targetFileName);
			System.out.println("创建文件成功.........."
					+ targetFile.getAbsolutePath());
			// 由这个文件对象 得到一个文件写入流对象..........
			FileOutputStream os = new FileOutputStream(targetFile);
			// 由 ZipFile 对象的getInputStream()方法 并传入 ZipEntry 得到读取流对象
			InputStream is = zipFile.getInputStream(entry);
			// 边读边写...........
			while ((bytes_read = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytes_read);
			}
			// 关闭流..........
			os.close();
			is.close();
		}
		System.out.println("@@@@@@@@@解压缩文件成功@@@@@@@@@");
	}

	/**
	 * 
	 * ************************************************* 现在要开始测试了...........<br/>
	 * <br/>
	 * 
	 * 先要确定这个要压缩的文件的文件全路径(包括文件名子)!<br/>
	 * <br/>
	 * 
	 * 再确定要压缩的文件名 (它一定要在 要压缩文件 baseDirName 的里面)! 要是给的字符串不是文件名而是 "*" 那么就是要压缩
	 * "要压缩的文件" 包括它自己本身在内的所有文件<br/>
	 * <br/>
	 * 
	 * 然后确定要压缩后的压缩文件全路径(包括文件名子及它的 zip 后缀)!<br/>
	 * <br/>
	 * 
	 * 
	 * 
	 * @param baseDirName
	 *            要压缩的文件目录路径名
	 * 
	 * @param fileName
	 *            要压缩的文件名 仅仅是一个名子,不是路径! 也可是 "*" 意为压缩本目录中的所有文件)
	 * 
	 * @param zipFileName
	 *            压缩完成后的文件路径名称.......
	 * 
	 * @param targetBaseDirName
	 *            要 解压缩 后的文件路径名称..........
	 *            **************************************************
	 * 
	 * @author 周宇 Mar 5, 2008 11:48:29 PM
	 * 
	 */
	public static void main(String[] args) {

		String baseDirName = "E:/temp";

		String[] fileNames = {"*"};
		// String fileName = "test";
		String zipFileName = "E:/temp.zip";
		try {
			ZipFolder.zipFile(baseDirName, fileNames, zipFileName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		/**
		 * *************************************************** 解压缩
		 * ***************************************************
		 */
//		String targetBaseDirName = "D:/test";
//		try {
//			ZipFolder.upzipFile(zipFileName, targetBaseDirName);
//		} catch (IOException e) {
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}
	}
}
