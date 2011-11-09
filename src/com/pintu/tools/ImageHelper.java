package com.pintu.tools;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 处理图片生成手机图和缩略图
 * 
 * @author liumingli
 * 
 */

public class ImageHelper {

	/**
	 * 处理图片
	 * 
	 * @param fileItem
	 * @param width
	 * @param height
	 * @param adjustSize
	 * @param type
	 *            分两种mobile或缩略图
	 * @param fileName
	 *            包括全路径的文件名
	 */
	static Object thumbnailHandler(FileItem fileItem, int width, int height,
			boolean adjustSize, String type, String fileName) {
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
		try {
			if (fileItem != null) {
				Image image = ImageIO.read(fileItem.getInputStream());
				int theImgWidth = image.getWidth(null);
				int theImgHeight = image.getHeight(null);
				int[] size = { theImgWidth, theImgHeight };
				if (adjustSize) {
					size = adjustImageSize(theImgWidth, theImgHeight, width,
							height, type);
				}
				StringBuffer strBuffer = new StringBuffer();
				strBuffer.append(fileName);
				writeFile(image, size[0], size[1], strBuffer.toString(),
						fileType);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 写文件
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @param strBuffer
	 * @param fileType
	 * @throws Exception
	 */
	private static void writeFile(Image image, int width, int height,
			String strBuffer, String fileType) {

		if (image == null)
			return;

//		if (fileType.toLowerCase().equals("png")) {
//			// PngEncoder encoder = new
//			// PngEncoder(PngEncoder.COLOR_TRUECOLOR_ALPHA);
//			// try {
//			// encoder.encode(image, new FileOutputStream(strBuffer));
//			// } catch (IOException e) {
//			// e.printStackTrace();
//			// }
//
//			try {
//				File file = new File(strBuffer);
//				ImageIO.write((RenderedImage) image, "png", file);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		} else {
			BufferedImage tag = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			// tag.getGraphics().drawImage(image, 0, 0, width, height, null);
			tag.getGraphics().drawImage(
					image.getScaledInstance(width, height, Image.SCALE_SMOOTH),
					0, 0, null);
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(strBuffer);
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(tag);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
//		}
	}

	private static int[] adjustImageSize(int theImgWidth, int theImgHeight,
			int defWidth, int defHeight, String type) {
		int[] size = { 0, 0 };

		float theImgHeightFloat = Float
				.parseFloat(String.valueOf(theImgHeight));
		float theImgWidthFloat = Float.parseFloat(String.valueOf(theImgWidth));

		if ((type.equals("mobile") || type.equals("thumbnail"))
				&& theImgWidth <= defWidth && theImgHeight <= defHeight){
			size[0] = theImgWidth;
			size[1] = theImgHeight;
		} else {
			if (theImgWidth < theImgHeight) {
				float scale = theImgHeightFloat / theImgWidthFloat;
				size[0] = Math.round(defHeight / scale);
				size[1] = defHeight;
			} else {
				float scale = theImgWidthFloat / theImgHeightFloat;
				size[0] = defWidth;
				size[1] = Math.round(defWidth / scale);
			}
		}

		if (type.equals("mobile")) {
			System.out.println("create mobile pic size:width" + size[0]
					+ "height" + size[1]);
		} else {
			System.out.println("create thumbnail pic size:width" + size[0]
					+ "height" + size[1]);
		}
		return size;
	}
}
