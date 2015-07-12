package day3;

import java.io.IOException;

import day2.ImageProcessing;

public class Thresholding {
	public static final int THRESHOLD = 127;

	public static void main(String[] args) throws IOException {
		PixelData pixelData = new PixelData("PolarSpace.png");
		int[][] pixels = pixelData.getPixels();
		pixels = startThresholding(pixels, THRESHOLD);
		ImageProcessing.writeImage("AfterThresholding.png", pixels);
	}

	private static int[][] startThresholding(int[][] pixels, int threshold) {
		int width = pixels[0].length;
		int height = pixels.length;
		int[][] output = new int [height][width];
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++) {
				int pixel = pixels[row][col] & 0xFF;
				if (pixel > threshold){
					output[row][col]=255;
				}
				else {
					output[row][col]=0;
				}
			}
		return output;
	}
}
