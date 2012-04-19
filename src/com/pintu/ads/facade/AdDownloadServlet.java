package com.pintu.ads.facade;

/** 
 * 微广告 文件下载Servlet 
 *  
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pintu.utils.FileToZip;

public class AdDownloadServlet extends HttpServlet{

	private static final long serialVersionUID = 8228591081694973459L;
	protected final static Log logger = LogFactory
			.getLog(AdDownloadServlet.class);
	
	
	private static String miniadPath = "";
	
	private static String tempzipPath = "";
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String venderId = request.getParameter("venderId");
		String version = request.getParameter("version");
		String dataSource = request.getParameter("dataSource");
		
		String fileFolder = miniadPath;
		String zipFile = AdConst.miniadzip;
		String destFile = tempzipPath+File.separator+zipFile;
		String modifyFile = AdConst.modifyFile;
		
		//修改内容并打包
		FileToZip zip = new FileToZip();
		zip.toZip(destFile,fileFolder,venderId,version,dataSource,modifyFile);
		
		
		//下载安装包
		File path = new File(destFile);
		String file = zipFile;
		
		if (!fileSafeCheck(file)) {
			StringBuilder sb = new StringBuilder("filename:").append(file)
					.append(" is invalid");
			if (logger.isDebugEnabled()) {
				logger.debug(sb.toString());
			}
			response.setCharacterEncoding("UTF-8");
			response.setContentLength(sb.length());
			response.getWriter().write(sb.toString());
		}
		// 以下是文件名的完整路径，具体根据情况需要自己在这里处理一下了
		String filePath = path.getAbsoluteFile().getParent() +File.separator+ file;
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("filename:").append(file)
					.append(",file absolute path:").append(filePath);
			logger.debug(sb.toString());
		}
		if (filePath != null && filePath.length() > 0) {
			try {
				downloadFile(response, filePath);
			} catch (Exception e) {
				StringBuilder sb = new StringBuilder(
						"download file exception,filename:").append(file)
						.append(",file absolute path:").append(filePath);
				logger.error(sb.toString(), e);
				sb.delete(0, sb.length());
				// sb.append("<mce:script language=/'javascript/'><!--  alert('文件下载异常,请确定文件名是否正确.');  --></mce:script>");
				sb.append("<mce:script language=/'javascript/'> alert('download file error.');</mce:script>");
				response.setCharacterEncoding("UTF-8");
				response.setContentLength(sb.length());
				response.getWriter().write(sb.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig conf) throws ServletException {
		// 初始化
			super.init(conf);
			// 从配置文件中获得初始化参数
			miniadPath = conf.getInitParameter("miniadpath");
			tempzipPath = conf.getInitParameter("tempzippath");

			ServletContext context = getServletContext();

			miniadPath = context.getRealPath(miniadPath);
			tempzipPath = context.getRealPath(tempzipPath);

			File fp = new File(miniadPath);
			if (!fp.exists())
				fp.mkdir();

			File tp = new File(tempzipPath);
			if (!tp.exists())
				tp.mkdir();

	}

	/**
	 * 下载文件
	 * 
	 * @param response
	 * @param filePath
	 * @throws RuntimeException
	 * @throws IOException
	 */
	private void downloadFile(HttpServletResponse response, String filePath)
			throws RuntimeException, IOException {
		File file = new File(filePath);
		if (!file.exists() || !file.canRead()) {
			throw new RuntimeException("File not exists or can not be read!");
		}
		FileInputStream fileinputstream = new FileInputStream(file);
		String filename = getFileName(filePath);
		downloadFile(fileinputstream, null, filename, 2048, response);
		fileinputstream.close();
		fileinputstream = null;
		file = null;
	}

	/**
	 * 下载文件
	 * 
	 * @param filedata
	 *            下载数据流
	 * @param contentType
	 *            文件类型
	 * @param filename
	 *            (下载后的)文件名称
	 * @param buffersize
	 *            缓冲区大小
	 * @param m_response
	 * @throws IOException
	 * @throws BimisException
	 */
	private void downloadFile(InputStream filedata, String contentType,
			String filename, int buffersize, HttpServletResponse m_response)
			throws IOException, RuntimeException {
		if (filedata == null) {
			return;
		}
		if (buffersize <= 0) {
			buffersize = 2048;
		}
		long contentLengh = filedata.available();
		if (contentType == null || contentType.length() == 0) {
			m_response.setContentType("application/x-msdownload");
		} else {
			m_response.setContentType(contentType);
		}
		if (contentLengh > 0) {
			m_response.setContentLength((int) contentLengh);
		}
		String m_contentDisposition = "attachment;";
		if (filename == null || filename.length() == 0) {
			m_response.setHeader("Content-Disposition", m_contentDisposition);
		} else {
			m_response.setHeader("Content-Disposition", m_contentDisposition
					+ " filename=" + toUtf8String(filename));
		}
		ServletOutputStream rspout = m_response.getOutputStream();
		copyStream(filedata, rspout, buffersize, true);
	}

	/**
	 * 将文件名中的汉字转为UTF8编码的串,以便下载时能正确显示另存的文件名.
	 * 
	 * @param s
	 *            原文件名
	 * @return 重新编码后的文件名
	 */
	private String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("utf-8");
				} catch (Exception ex) {
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	private final long copyStream(InputStream source, OutputStream dest,
			int bufferSize, boolean flush) throws RuntimeException {
		int bytes;
		long total;
		byte[] buffer;
		buffer = new byte[bufferSize];
		total = 0;
		try {
			while ((bytes = source.read(buffer)) != -1) {
				// Technically, some read(byte[]) methods may return 0 and we
				// cannot
				// accept that as an indication of EOF.
				if (bytes == 0) {
					bytes = source.read();
					if (bytes < 0)
						break;
					dest.write(bytes);
					if (flush)
						dest.flush();
					++total;
					continue;
				}
				dest.write(buffer, 0, bytes);
				if (flush)
					dest.flush();
				total += bytes;
			}
		} catch (IOException e) {
			throw new RuntimeException("IOException caught while copying.", e);
		}
		return total;
	}

	/**
	 * 根据传入的路径，获取文件的名称
	 * 
	 * @param fullFilePath
	 * @return
	 */
	private String getFileName(String fullFilePath) {
		if (fullFilePath == null) {
			return "";
		}
		int index1 = fullFilePath.lastIndexOf("/");
		int index2 = fullFilePath.lastIndexOf("//");

		// index is the maximum value of index1 and index2
		int index = (index1 > index2) ? index1 : index2;
		if (index == -1) {
			// not found the path separator
			return fullFilePath;
		}
		String fileName = fullFilePath.substring(index + 1);
		return fileName;
	}

	/**
	 * 文件名的安全检查，以免被人通过"../../../"的方式获取其它文件
	 * 
	 * @param filename
	 * @return
	 */
	private boolean fileSafeCheck(String filename) {
		if (filename.indexOf("../") >= 0) {
			return false;
		}
		return true;
	}

	

}