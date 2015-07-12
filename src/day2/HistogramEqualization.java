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
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class HistogramEqualization {
	private static int imageHeight;
	private static int imageWidth;
	private static int lineStride;

	public static void main(String[] args) throws IOException {
		File file = new File("input.jpg");

		// Read image to int[][] pixels
		BufferedImage bufImgInput = ImageIO.read(file);
		imageWidth = bufImgInput.getWidth();
		imageHeight = bufImgInput.getHeight();
		WritableRaster writableRaster = bufImgInput.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		lineStride = coModel.getScanlineStride();

		int[][] pixels = ConvertImageToPixelArray.convertTo2DPixelArray(bufImgInput);

		//Processing image here
		//Create a the histogram from pixels array of an image
		ArrayList<int[]> imageHistogram = getHistogram(pixels);
		//Create the histogram lookup table
		ArrayList<int[]> imageLUT = gethistogramEqualizationLUT(imageHistogram, imageWidth, imageHeight);
		//Create a new 2D pixels array to a new histogram equalization image
		int[][] result = doHistogramEqualization(pixels, imageLUT);
		
		
		/// convert int pixels array to image
		byte[] outputStream = ConvertImageToPixelArray.convertToByteArray(result, imageWidth,
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

	
	public static int[][] doHistogramEqualization(int[][] pixels, ArrayList<int[]> histLUT) {
		int imageWidth = pixels[0].length;
		int imageHeight = pixels.length;
	    int red;
	    int green;
	    int blue;
	    int newPixel;
  
	    int[][] result = new int[imageHeight][imageWidth];
	    
	    for(int row=0; row<imageHeight; row++) {
	        for(int col=0; col<imageWidth; col++) {
	 
	            // Get pixels by R, G, B
	        	blue = pixels[row][col] & 0xFF;
				green = (pixels[row][col] >> 8) & 0xFF;
				red = (pixels[row][col] >> 16) & 0xFF;
	 
	            // Set new pixel values using the histogram lookup table
	            red = histLUT.get(0)[red];
	            green = histLUT.get(1)[green];
	            blue = histLUT.get(2)[blue];
	 
	            // Return back to original format
	            newPixel = 0;
	            newPixel = (((newPixel | blue) | (green << 8)) | (red << 16));
	 
	            // Write pixels into image
	            result[row][col] = newPixel;
	 
	        }
	    }
	 
	    return result;
	 
	}
	// Get the histogram equalization lookup table for separate R, G, B channels
	public static ArrayList<int[]> gethistogramEqualizationLUT(
			ArrayList<int[]> imageHist, int imageWidth, int imageHeight) {

		// Create the lookup table
		ArrayList<int[]> imageLUT = new ArrayList<int[]>();

		// Fill the lookup table
		int[] rhistogram = new int[256];
		int[] ghistogram = new int[256];
		int[] bhistogram = new int[256];

		for (int i = 0; i < rhistogram.length; i++) {
			rhistogram[i] = 0;
			ghistogram[i] = 0;
			bhistogram[i] = 0;
		}

		long sumr = 0;
		long sumg = 0;
		long sumb = 0;

		// Calculate the scale factor
		float scale_factor = (float) (255.0 / (imageWidth * imageHeight));

		for (int i = 0; i < rhistogram.length; i++) {
			sumr += imageHist.get(0)[i];
			int valr = (int) (sumr * scale_factor);
			if (valr > 255) {
				rhistogram[i] = 255;
			} else
				rhistogram[i] = valr;

			sumg += imageHist.get(1)[i];
			int valg = (int) (sumg * scale_factor);
			if (valg > 255) {
				ghistogram[i] = 255;
			} else
				ghistogram[i] = valg;

			sumb += imageHist.get(2)[i];
			int valb = (int) (sumb * scale_factor);
			if (valb > 255) {
				bhistogram[i] = 255;
			} else
				bhistogram[i] = valb;
		}

		imageLUT.add(rhistogram);
		imageLUT.add(ghistogram);
		imageLUT.add(bhistogram);

		return imageLUT;

	}

	/**
	 * get the Histogram of a RGB image
	 * 
	 * @param pixels
	 * @return an ArrayList containing histogram values for separate R, G, B
	 *         channels.
	 */
	public static ArrayList<int[]> getHistogram(int[][] pixels) {
		int[] rhistogram = new int[256];
		int[] ghistogram = new int[256];
		int[] bhistogram = new int[256];

		for (int i = 0; i < rhistogram.length; i++) {
			rhistogram[i] = 0;
			ghistogram[i] = 0;
			bhistogram[i] = 0;
		}

		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				int blue = pixels[row][col] & 0xFF;
				int green = (pixels[row][col] >> 8) & 0xFF;
				int red = (pixels[row][col] >> 16) & 0xFF;

				// Increase the values of colors
				bhistogram[blue]++;
				ghistogram[green]++;
				rhistogram[red]++;
			}

		ArrayList<int[]> histogram = new ArrayList<int[]>();
		histogram.add(rhistogram);
		histogram.add(ghistogram);
		histogram.add(bhistogram);
		return histogram;
	}

}
