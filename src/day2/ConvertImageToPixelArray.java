package day2;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

public class ConvertImageToPixelArray {
	private static int imageWidth;
	private static int imageHeight;
	private static int lineStride;

	private static double factor = 1.0 / 9.0;
	private static double bias = 0.0;
	private static int filter_width;
	private static int filter_height;

	private static double[][] filter = new double[][] { 
		{-1, -1, -1},
		{-1,  9, -1},
		{-1, -1, -1}
	};

	public static void main(String[] args) throws IOException {
		File file = new File("photo3.bmp");

		// Read image to int[][] pixels
		BufferedImage bufImgInput = ImageIO.read(file);
		imageWidth = bufImgInput.getWidth();
		imageHeight = bufImgInput.getHeight();
		WritableRaster writableRaster = bufImgInput.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		lineStride = coModel.getScanlineStride();
		
		filter_width = filter[0].length;
		filter_height = filter.length;

		int[][] pixels = convertTo2DPixelArray(bufImgInput);

		
		// Processing the image here
		int[][] result = filterImage(pixels);

		// convert int pixels array to image
		byte[] outputStream = convertToByteArray(result, imageWidth,
				imageHeight);
		BufferedImage bufImgOutput = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_3BYTE_BGR);
		
		DataBuffer dataBuffer = new DataBufferByte(outputStream,
				outputStream.length);
		SampleModel sampleModel = new ComponentSampleModel(
				DataBuffer.TYPE_BYTE, imageWidth, imageHeight, 3, lineStride,
				new int[] { 2, 1, 0});
		Raster rasterOutput = Raster
				.createRaster(sampleModel, dataBuffer, null);
		bufImgOutput.setData(rasterOutput);

		ImageIO.write(bufImgOutput, "jpg", new File("output.jpg"));
	}
	
	private static int[][] filterImage(int[][] pixels){
		int[][] result = new int[imageHeight][imageWidth];
		for (int x = 0; x < imageHeight; x++)
			for (int y = 0; y < imageWidth; y++) {

				int red = 0, green = 0, blue = 0;

				for (int filterX = 0; filterX < filter_height; filterX++)
					for (int filterY = 0; filterY < filter_width; filterY++) {
						
						int imageX = (x - filter_height / 2 + filterX + imageHeight)
								% imageHeight;
						int imageY = (y - filter_width / 2 + filterY + imageWidth)
								% imageWidth;

						blue += (pixels[imageX][imageY] & 0xFF)
								* filter[filterX][filterY];
						green += ((pixels[imageX][imageY] >> 8) & 0xFF)
								* filter[filterX][filterY];
						red += ((pixels[imageX][imageY] >> 16) & 0xFF)
								* filter[filterX][filterY];
					}

				red = Math.min(Math.abs((int) (factor*red+bias)), 255);
				green = Math.min(Math.abs((int) (factor*green+bias)), 255);
				blue = Math.min(Math.abs((int) (factor*blue+bias)), 255);
				
				int new_argb = 0;
				new_argb = (((new_argb | blue) | (green << 8)) | (red << 16));
				result[x][y] = (pixels[x][y] & 0xFF000000) | new_argb;
			}
		return result;
	}

	/**
	 * Convert a 2D pixel array to 1D byte array
	 * @param pixels
	 * @param imageWidth
	 * @param imageHeight
	 * @return
	 */
	public static byte[] convertToByteArray(int[][] pixels, int imageWidth,
			int imageHeight) {
		byte[] byteArrayImage = null;
		boolean hasAlphaChannel = (pixels[0][0] >> 24) == 0xFF;
		if (hasAlphaChannel) {
			// Having alpha value
			final int pixelLength = 4;
			int byteArrayImageLength = pixels[0].length + pixels.length;

		} else {
			// Not having alpha value
			final int pixelLength = 3;
			int byteArrayImageLength = (imageWidth * imageHeight) * pixelLength;
			byteArrayImage = new byte[byteArrayImageLength];

			for (int bytecount = 0, row = 0, col = 0; bytecount < byteArrayImageLength;) {
				int argb = pixels[row][col];
				// blue
				int blue = argb & 0xFF;
				byteArrayImage[bytecount++] = (byte) blue;
				// green
				int green = (argb >> 8) & 0xFF;
				byteArrayImage[bytecount++] = (byte) green;
				// red
				int red = (argb >> 16) & 0xFF;
				byteArrayImage[bytecount++] = (byte) red;

				col++;
				if (col == imageWidth) {
					col = 0;
					row++;
				}
			}

		}
		return byteArrayImage;
	}

	/**
	 * Convert a BufferedImage of a RGB | ARGB Image to a 2D pixel array
	 * @param bufImg
	 * @return
	 */
	public static int[][] convertTo2DPixelArray(BufferedImage bufImg) {
		WritableRaster raster = bufImg.getRaster();
		DataBufferByte dataBufferByte = (DataBufferByte) raster.getDataBuffer();
		byte[] byteArray = dataBufferByte.getData();

		int imageWidth = bufImg.getWidth();
		int imageHeight = bufImg.getHeight();
		boolean hasAlphaChannel = bufImg.getAlphaRaster() != null;

		int[][] result = new int[imageHeight][imageWidth];
		if (hasAlphaChannel) {
			// Having alpha value
			final int pixelLength = 4;
			for (int bytecount = 0, row = 0, col = 0; bytecount < byteArray.length; bytecount += pixelLength) {
				int argb = 0;
				argb |= (((int) byteArray[bytecount] & 0xFF) << 24); // alpha
				argb |= ((int) byteArray[bytecount + 1] & 0xFF); // blue
				argb |= (((int) byteArray[bytecount + 2] & 0xFF) << 8); // green
				argb |= (((int) byteArray[bytecount + 3] & 0xFF) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == imageWidth) {
					col = 0;
					row++;
				}
			}
		} else {
			// Not having alpha value
			final int pixelLength = 3;
			int size_byte = byteArray.length;
			for (int bytecount = 0, row = 0, col = 0; bytecount < byteArray.length; bytecount += pixelLength) {
				int argb = 0;
				argb |= 0xFF << 24; // alpha = FF
				argb |= ((int) byteArray[bytecount] & 0xff); // blue
				argb |= (((int) byteArray[bytecount + 1] & 0xff) << 8); // green
				argb |= (((int) byteArray[bytecount + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				// System.out.println(row + "-" + col + "-" + result[row][col]);
				col++;
				if (col == imageWidth) {
					col = 0;
					row++;
				}

			}
		}

		return result;
	}

}

/*Some common mask to filter:
Blur:
{1, 0, 0, 0, 0, 0, 0, 0, 0},
{0, 1, 0, 0, 0, 0, 0, 0, 0},
{0, 0, 1, 0, 0, 0, 0, 0, 0},
{0, 0, 0, 1, 0, 0, 0, 0, 0},
{0, 0, 0, 0, 1, 0, 0, 0, 0},
{0, 0, 0, 0, 0, 1, 0, 0, 0},
{0, 0, 0, 0, 0, 0, 1, 0, 0},
{0, 0, 0, 0, 0, 0, 0, 1, 0},
{0, 0, 0, 0, 0, 0, 0, 0, 1}

Find horizotal edges		 Find vertical edges   		Find edges in all directions
{0,  0,  0,  0,  0},		 {0,  0, -1,  0,  0},		{-1, -1, -1},
{0,  0,  0,  0,  0},		 {0,  0, -1,  0,  0},		{-1, 8, -1},
{-1, -1,  2,  0,  0},		 {0,  0,  4,  0,  0},		{-1, -1, -1}
{0,  0,  0,  0,  0},		 {0,  0, -1,  0,  0},
{0,  0,  0,  0,  0}		 	 {0,  0, -1,  0,  0}

Mean and Median Filter: to remove noise from image
{1, 1, 1},
{1, 1, 1},
{1, 1, 1}

Sharpen Filter
{-1, -1, -1},
{-1,  9, -1},
{-1, -1, -1}

Emboss Filter: to give a 3D shadow effect to the image
{-1, -1,  0},
{-1,  0,  1},
{0,  1,  1}
*/
