package com.pintu.tools;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.fileupload.FileItem;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 处理图片生成手机图和缩略图
 * @author liumingli
 *
 */

public class ImageHelper {

     private static final String BMP = "bmp";
     private static final String PNG = "png";
     private static final String GIF = "gif";
     private static final String JPEG = "jpeg";
     private static final String JPG = "jpg";
//     public static final String THUMBNAIL = "thumbnail.jpg";
     public static void zoomPicture(
             FileItem fileItem, 
             int width, 
             int height,
             boolean adjustSize,String type,String fileName) {
         if (fileItem == null) {
             return;
         }
         String fileType = fileItem.getName().substring(fileName.lastIndexOf(".")+1).toUpperCase();
         if (fileType.endsWith(BMP)) {
//             BMPThumbnailHandler(fileItem, width, height, adjustSize,type);
         } else if (fileType.endsWith(PNG) || fileType.endsWith(GIF)
                 || fileType.endsWith(JPEG) || fileType.endsWith(JPG)) {
             thumbnailHandler(fileItem, width, height, adjustSize,type,fileName);
         }
     }
     
     /**
      * 处理图片
      * @param source
      * @param width
      * @param height
      * @param adjustSize
      * @param type 分两种mobile或缩略图
      */
     static Object thumbnailHandler(FileItem fileItem,  int width,  int height, boolean adjustSize, String type,String fileName) {
         try {
             if (fileItem != null) {
                 Image image = ImageIO.read(fileItem.getInputStream());
                 int theImgWidth = image.getWidth(null);
                 int theImgHeight = image.getHeight(null);
                 int[] size = { theImgWidth, theImgHeight };
                 if (adjustSize) {
                     size = adjustImageSize(theImgWidth, theImgHeight, width,height,type);
                 }
                 StringBuffer thumbnailFile=new StringBuffer();
                 thumbnailFile.append(fileName);
                 if(type.equals("mobile")){
                	 writeFile(image, size[0], size[1], thumbnailFile.toString());
                	 return true;
                 }else{
                	 return getThumbnailStream(image, size[0], size[1]);
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
		return null;
     }

    
     //得到生成的缩略图的bufferedImage信息
     private static BufferedImage getThumbnailStream( Image image,  int width,  int height){
        
         if (image == null) return null;
     
         BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
         tag.getGraphics().drawImage(image, 0, 0, width, height, null);
         return tag;
        		 
     }
     
     /**
      * 写文件
      * @param image
      * @param width
      * @param height
      * @param thumbnailFile
      * @throws Exception
      */
     private static void writeFile(Image image,  int width,  int height, String thumbnailFile) {
        
         if (image == null) return;
     
         BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
         tag.getGraphics().drawImage(image, 0, 0, width, height, null);
         FileOutputStream out = null;
         try {
             out = new FileOutputStream(thumbnailFile);
             JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
             encoder.encode(tag);
         } catch (Exception e) {
             e.printStackTrace();
             try {
				throw new Exception(e);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
         } finally {
             if (out != null) {
                 try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
             }
         }
     }

     private static int[] adjustImageSize(int theImgWidth, int theImgHeight, int defWidth, int defHeight ,String type) {        
         int[] size = { 0, 0 };
        
         float theImgHeightFloat=Float.parseFloat(String.valueOf(theImgHeight));
         float theImgWidthFloat=Float.parseFloat(String.valueOf(theImgWidth));
         
         if(type.equals("mobile") && theImgWidth < defWidth){
     	 	size[0] = theImgWidth;
     	 	size[1] = theImgHeight;
         }else{
	         if (theImgWidth<theImgHeight) {
	             float scale=theImgHeightFloat/theImgWidthFloat;
	             size[0]=Math.round(defHeight/scale);
	             size[1]=defHeight;
	         }else {
	             float scale=theImgWidthFloat/theImgHeightFloat;
	             size[0]=defWidth;
	             size[1]=Math.round(defWidth/scale);
	         }
         }
         
         System.out.println("size:宽"+size[0]+"高"+size[1]);
         
         return size;
     }
}
