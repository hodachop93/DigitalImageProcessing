package day2;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import sun.security.x509.AVA;

public class BinaryImageProcessing {
	private static int imageWidth;
	private static int imageHeight;
	private static int lineStride;

	private static int numOfBit = 2;

	public static void main(String[] args) throws IOException {
		File file = new File("digital_image_processing.jpg");

		// Read image to int[][] pixels
		BufferedImage bufImgInput = ImageIO.read(file);

		/*
		 * IndexColorModel index = (IndexColorModel)
		 * bufImgInput.getColorModel(); //Kich thuoc cua mang mau int size_color
		 * = index.getMapSize(); byte[] red = new byte[size_color];
		 * index.getReds(red);
		 */

		imageWidth = bufImgInput.getWidth();
		imageHeight = bufImgInput.getHeight();
		WritableRaster writableRaster = bufImgInput.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		lineStride = coModel.getScanlineStride();

		int[][] pixels = ConvertImageToPixelArray.convertTo2DPixelArray(bufImgInput);

		// Processing image here
		byte[][] result = convertToBinaryImage(pixels);

		// convert byte pixels array to binary image
		byte[] outputStream = convertToByteArray(result, imageWidth,
				imageHeight);

		// Convert a byte array to an image
		// IndexColorModel indexColorModel = new IndexColorModel(numOfBit,1,
		// hop, hop, hop);
		BufferedImage bufImgOutput = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster rasterOutput = bufImgOutput.getRaster();
		rasterOutput.setDataElements(0, 0, imageWidth, imageHeight,
				outputStream);
		DataBuffer dataBuffer = new DataBufferByte(outputStream,
				outputStream.length);
		SampleModel sampleModel = new ComponentSampleModel(
				DataBuffer.TYPE_BYTE, imageWidth, imageHeight, 1, lineStride,
				new int[] { 0 });
		/*
		 * Raster rasterOutput = Raster .createRaster(sampleModel, dataBuffer,
		 * null);
		 */
		bufImgOutput.setData(rasterOutput);

		// Chu y jpeg khong ho tro anh nhi phan 1 bit depth
		ImageIO.write(bufImgOutput, "bmp", new File("output.bmp"));
	}

	/**
	 * Convert a 2D pixel array to 1D byte array
	 * 
	 * @param pixels
	 * @param imageWidth
	 * @param imageHeight
	 * @return
	 */
	private static byte[] convertToByteArray(byte[][] pixels, int imageWidth,
			int imageHeight) {
		byte[] byteArrayImage = null;

		int byteArrayImageLength = imageWidth * imageHeight;
		byteArrayImage = new byte[byteArrayImageLength];

		int count = 0;
		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				byteArrayImage[count++] = pixels[row][col];
			}

		return byteArrayImage;
	}

	private static byte[][] convertToBinaryImage(int[][] pixels) {
		byte[][] result = new byte[imageHeight][imageWidth];
		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				int red, green, blue;
				blue = pixels[row][col] & 0xFF;
				green = (pixels[row][col] >> 8) & 0xFF;
				red = (pixels[row][col] >> 16) & 0xFF;

				int average = (red + green + blue) / 3;
				average = (average > 127) ? 1 : 0;
				// average = (int) (average * Math.pow(2, numOfBit) /255);

				result[row][col] = (byte) average;

			}
		return result;
	}

	/*
	 * private static int[][] convertToBinaryImage(int[][] pixels) { int[][]
	 * result = new int[imageHeight][imageWidth]; for (int row = 0; row <
	 * imageHeight; row++) for (int col = 0; col < imageWidth; col++) { int red,
	 * green, blue; blue = pixels[row][col] & 0xFF; green = (pixels[row][col] >>
	 * 8) & 0xFF; red = (pixels[row][col] >> 16) & 0xFF;
	 * 
	 * int average = (red + green + blue) / 3; average = (average > 127) ? 255 :
	 * 0;
	 * 
	 * int new_argb = 0; new_argb = (((new_argb | average) | (average << 8)) |
	 * (average << 16)); result[row][col] = new_argb;
	 * 
	 * } return result; }
	 */

}
