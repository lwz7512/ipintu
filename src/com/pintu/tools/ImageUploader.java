package com.pintu.tools;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;


/**
 * Servlet implementation class ImageUploader 似乎不能用这个上传类，而应该统一到一个外部访问接口中
 */
public class ImageUploader extends HttpServlet {

	private static final long serialVersionUID = -543085089916376144L;

	private String filePath; // 文件存放目录
	private String tempPath; // 临时文件目录
	
	// 最大文件上传尺寸设置
	private int fileMaxSize = 4 * 1024 * 1024;

	
	private Logger log = Logger.getLogger(ImageUploader.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageUploader() {
		super();
	}

	// 初始化
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 从配置文件中获得初始化参数
		filePath = config.getInitParameter("filepath");
		tempPath = config.getInitParameter("temppath");

		ServletContext context = getServletContext();

		filePath = context.getRealPath(filePath);
		tempPath = context.getRealPath(tempPath);

		File fp = new File(filePath);
		if (!fp.exists())
			fp.mkdir();

		File tp = new File(tempPath);
		if (!tp.exists())
			tp.mkdir();

		log.debug("File storage directory, the temporary file directory is ready ...");
		log.debug("filePah: " + filePath);
		log.debug("tempPah: " + tempPath);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter pw = response.getWriter();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		// 不是文件上传请求
		if (!isMultipart) {
			log.debug(">>>Invalid request, not to deal with! ! !");
			pw.write(">>> Invalid request, not to deal with! ! !");
			pw.close();
			return;
		}			
		
		
		try {
			DiskFileItemFactory diskFactory = new DiskFileItemFactory();
			// threshold 极限、临界值，即内存缓存 空间大小
			diskFactory.setSizeThreshold(fileMaxSize);
			// repository 贮藏室，即临时文件目录
			diskFactory.setRepository(new File(tempPath));

			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			// 设置允许上传的最大文件大小 4M
			upload.setSizeMax(fileMaxSize);
			
			
			// 解析HTTP请求消息头
			List<FileItem> fileItems = upload.parseRequest(request);
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()) {
					log.debug("Processing the contents of the form...");
					processFormField(item, pw);
				} else {
					log.debug("Upload file handling...");
					processUploadFile(item, pw);
				}
			}// end while()

			// close write to front end
			pw.close();

		} catch (SizeLimitExceededException e) {
			
			log.debug(">>>File size exceeds the limit, can not upload!");
			pw.print(">>> File size exceeds the limit, can not upload!");
			return;
			
		} catch (Exception e) {
			log.debug("Exception occurs when using fileupload package...");
			e.printStackTrace();
		}// end try ... catch ...

	}// end of doPost

	// 处理表单内容
	private void processFormField(FileItem item, PrintWriter pw)
			throws Exception {
		String name = item.getFieldName();
		String value = item.getString();
		log.debug(name + " : " + value + "\r\n");
		pw.print(name + " : " + value + "\r\n");
	}

	// 处理上传的文件
	private void processUploadFile(FileItem item, PrintWriter pw)
			throws Exception {
		// 此时的文件名包含了完整的路径，得注意加工一下
		String fileName = item.getName();
		int dotPos = fileName.lastIndexOf(".");
		String fileType = fileName.substring(dotPos+1);
		
		if(fileType.equals("png") || fileType.equals("jpg") || fileType.equals("gif")){
			log.debug(">>>The current file type is:"+fileType);
		}else{
			pw.print(">>> The current file is not a picture file, not to generate!");
			return;			
		}

		// 如果是用IE上传就需要处理下文件名，否则是全路径了
		if (fileName != null) {
			fileName = FilenameUtils.getName(fileName);
		}

		long fileSize = item.getSize();
		String sizeInK = (int) fileSize / 1024 + "K";

		if ("".equals(fileName) && fileSize == 0) {
			log.debug("fileName is null ...");
			return;
		}

		File uploadFile = new File(filePath + File.separator + fileName);
		// 生成文件
		item.write(uploadFile);
	
		
		log.debug(fileName + " File is complete ...");
		
		// 返回客户端信息
		pw.print(fileName + " File is complete ...");
		pw.print("file size is:" + sizeInK);
		pw.print("file route is:" + uploadFile.getAbsolutePath() + "\r\n");
	}

}
