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

public class ConvertRGBToGrayscale {
	private static int imageHeight;
	private static int imageWidth;
	private static int lineStride;

	public static void main(String[] args) throws IOException {
		File file = new File("digital_image_processing.jpg");

		// Read image to int[][] pixels
		BufferedImage bufImgInput = ImageIO.read(file);
		imageWidth = bufImgInput.getWidth();
		imageHeight = bufImgInput.getHeight();
		WritableRaster writableRaster = bufImgInput.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		lineStride = coModel.getScanlineStride();

		int[][] pixels = ConvertImageToPixelArray.convertTo2DPixelArray(bufImgInput);

		pixels = ConvertRGBToGrayscale(pixels);

		// Convert a byte array to an image
		byte[] outputStream = convertToByteArray(pixels, imageWidth,
				imageHeight);
		
		BufferedImage bufImgOutput = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_BYTE_GRAY);
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
		ImageIO.write(bufImgOutput, "bmp", new File("output.jpg"));
	}

	private static int[][] ConvertRGBToGrayscale(int[][] pixels) {
		int[][] result;
		result = new int[imageHeight][imageWidth];
		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				int blue = pixels[row][col] & 0xFF;
				int green = (pixels[row][col] >> 8) & 0xFF;
				int red = (pixels[row][col] >> 16) & 0xFF;
				int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
				result[row][col] = gray;
				//Ix, y = 0.299 * Redx, y + 0.587 * Greenx, y + 0.114 * Bluex, y
				
			}
		return result;
	}

	/**
	 * Convert a 2D pixel array to 1D byte array
	 * 
	 * @param pixels
	 * @param imageWidth
	 * @param imageHeight
	 * @return
	 */
	private static byte[] convertToByteArray(int[][] pixels, int imageWidth,
			int imageHeight) {
		byte[] byteArrayImage = null;
		boolean hasAlphaChannel = (pixels[0][0] >> 24) == 0xFF;
		if (hasAlphaChannel) {
			// Having alpha value
			final int pixelLength = 4;
			int byteArrayImageLength = pixels[0].length + pixels.length;

		} else {
			// Convert Grayscale Pixel Array to Byte Array
			int byteArrayImageLength = imageWidth * imageHeight;
			byteArrayImage = new byte[byteArrayImageLength];

			for (int bytecount = 0, row = 0, col = 0; bytecount < byteArrayImageLength; bytecount++) {
				int pixel = pixels[row][col];
				byte value = (byte) pixel;
				byteArrayImage[bytecount] = value;

				col++;
				if (col == imageWidth) {
					col = 0;
					row++;
				}
			}

		}
		return byteArrayImage;
	}

	
}
