package day2;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;


public class PerformanceTest {

	public static void main(String[] args) throws IOException {

		BufferedImage hugeImage = ImageIO.read(new File(
				"hdimage.jpg"));

		System.out.println("Testing convertTo2DUsingGetRGB:");
		for (int i = 0; i < 10; i++) {
			long startTime = System.nanoTime();
			int[][] result = convertTo2DUsingGetRGB(hugeImage);
			long endTime = System.nanoTime();
			System.out.println(String.format("%-2d: %s", (i + 1),
					toString(endTime - startTime)));
		}

		System.out.println("");

		System.out.println("Testing convertTo2DWithoutUsingGetRGB:");
		for (int i = 0; i < 10; i++) {
			long startTime = System.nanoTime();
			int[][] result = convertTo2DWithoutUsingGetRGB(hugeImage);
			long endTime = System.nanoTime();
			System.out.println(String.format("%-2d: %s", (i + 1),
					toString(endTime - startTime)));
		}
	}

	private static int[][] convertTo2DUsingGetRGB(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] result = new int[width][height];

		for (int row = 0; row < width; row++) {
			for (int col = 0; col < height; col++) {
				result[row][col] = image.getRGB(row, col);
			}
		}

		return result;
	}

	private static int[][] convertTo2DWithoutUsingGetRGB(BufferedImage image) {

		Raster raster = image.getRaster();
		DataBufferByte dataBufferByte = (DataBufferByte) raster.getDataBuffer();
		byte[] pixels = dataBufferByte.getData();

		int width = image.getWidth();
		int height = image.getHeight();
		boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			// Have alpha value
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb |= (((int) pixels[pixel] & 0xFF) << 24); // alpha
				argb |= ((int) pixels[pixel + 1] & 0xFF); // blue
				argb |= (((int) pixels[pixel + 2] & 0xFF) << 8); // green
				argb |= (((int) pixels[pixel + 3] & 0xFF) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			// Don't have alpha value
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb |= 0xFF << 24; // alpha = FF
				argb |= ((int) pixels[pixel] & 0xff); // blue
				argb |= (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb |= (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
				
			}
		}

		return result;
	}

	private static String toString(long nanoSecs) {
		int minutes = (int) (nanoSecs / 60000000000.0);
		int seconds = (int) (nanoSecs / 1000000000.0) - (minutes * 60);
		int millisecs = (int) (((nanoSecs / 1000000000.0) - (seconds + minutes * 60)) * 1000);

		if (minutes == 0 && seconds == 0)
			return millisecs + "ms";
		else if (minutes == 0 && millisecs == 0)
			return seconds + "s";
		else if (seconds == 0 && millisecs == 0)
			return minutes + "min";
		else if (minutes == 0)
			return seconds + "s " + millisecs + "ms";
		else if (seconds == 0)
			return minutes + "min " + millisecs + "ms";
		else if (millisecs == 0)
			return minutes + "min " + seconds + "s";

		return minutes + "min " + seconds + "s " + millisecs + "ms";
	}
}