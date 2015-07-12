package day2;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.util.regex.Pattern;

import day3.PixelData;

public class ImageProcessing {

	/**
	 * Convert a 2D pixel RGB | ARGB array to 1D byte array
	 * 
	 * @param srcFormat
	 * @return
	 */
	public static byte[] convertToRGBByteArray(PixelData src) {
		int[][] pixels = src.getPixels();
		int imageWidth = pixels[0].length;
		int imageHeight = pixels.length;
		byte[] byteArrayImage = null;
		boolean hasAlphaChannel = src.hasAlpha();
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
	 * Convert a 2D RGB pixels array to a 1D Grayscale pixels Array
	 * 
	 * @param pixels
	 * @return 2D Grayscale pixels Array
	 */
	public static int[][] ConvertRGBToGrayscale(int[][] pixels) {
		int[][] result;
		int imageWidth = pixels[0].length;
		int imageHeight = pixels.length;
		result = new int[imageHeight][imageWidth];
		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				int blue = pixels[row][col] & 0xFF;
				int green = (pixels[row][col] >> 8) & 0xFF;
				int red = (pixels[row][col] >> 16) & 0xFF;
				int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
				result[row][col] = gray;
				// Ix, y = 0.299 * Redx, y + 0.587 * Greenx, y + 0.114 * Bluex,y

			}
		return result;
	}

	public static void writeImage(String imgName, int[][] dstData) throws IOException {
		String[] strs = imgName.split(Pattern.quote("."));
		String format = strs[strs.length - 1];
		// Tao du lieu output
		// Chuyen doi mang pixels thanh mang cac byte
		byte[] output = convertGrayscaleTo24BitByteArray(dstData);
		// Thiet lap cac thong so anh dau ra
		int width = dstData[0].length;
		int height = dstData.length;
		int pixelStride = 3;
		int[] bandOffset = new int[] { 2, 1, 0 };
		
		BufferedImage bufImg = new BufferedImage(width, height,
				BufferedImage.TYPE_3BYTE_BGR);
		
		WritableRaster writableRaster = bufImg.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		int lineStride = coModel.getScanlineStride();	
		
		DataBuffer dataBuf = new DataBufferByte(output, output.length);
		SampleModel sampleModel = new ComponentSampleModel(
				DataBuffer.TYPE_BYTE, width, height, pixelStride, lineStride,
				bandOffset);
		Raster rasterOutput = Raster.createRaster(sampleModel, dataBuf, null);
		bufImg.setData(rasterOutput);
		ImageIO.write(bufImg, format, new File(imgName));

	}

	private static byte[] convertGrayscaleTo24BitByteArray(int[][] pixels) {
		int imageWidth = pixels[0].length;
		int imageHeight = pixels.length;
		byte[] byteArrayImage = null;

		final int pixelLength = 3;
		int byteArrayImageLength = (imageWidth * imageHeight) * pixelLength;
		byteArrayImage = new byte[byteArrayImageLength];

		for (int bytecount = 0, row = 0, col = 0; bytecount < byteArrayImageLength;) {
			int pixel = pixels[row][col];
			// blue
			int blue = pixel & 0xFF;
			byteArrayImage[bytecount++] = (byte) blue;
			// green
			int green = pixel & 0xFF;
			byteArrayImage[bytecount++] = (byte) green;
			// red
			int red = pixel & 0xFF;
			byteArrayImage[bytecount++] = (byte) red;

			col++;
			if (col == imageWidth) {
				col = 0;
				row++;
			}
		}

		return byteArrayImage;
	}
}
