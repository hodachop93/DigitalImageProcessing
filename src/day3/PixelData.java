package day3;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.omg.CORBA.INITIALIZE;

public class PixelData {
	private int[][] pixels;
	private int imageWidth;
	private int imageHeight;
	private boolean hasAlpha;

	public PixelData(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedImage bufImg = ImageIO.read(file);
		imageWidth = bufImg.getWidth();
		imageHeight = bufImg.getHeight();
		
		WritableRaster writableRaster = bufImg.getRaster();
		ComponentSampleModel coModel = (ComponentSampleModel) writableRaster
				.getSampleModel();
		Raster raster = bufImg.getAlphaRaster();
		hasAlpha= bufImg.getAlphaRaster() != null;
		initialize(bufImg);
	}

	public PixelData(int width, int height){
		pixels = new int[height][width];
	}
	
	public PixelData(){
		
	}
	private void initialize(BufferedImage bufImg) {
		WritableRaster raster = bufImg.getRaster();
		DataBufferByte dataBufferByte = (DataBufferByte) raster.getDataBuffer();
		byte[] byteArray = dataBufferByte.getData();
		pixels = new int[imageHeight][imageWidth];
		if (hasAlpha) {
			// Having alpha value
			final int pixelLength = 4;
			for (int bytecount = 0, row = 0, col = 0; bytecount < byteArray.length; bytecount += pixelLength) {
				int argb = 0;
				argb |= (((int) byteArray[bytecount] & 0xFF) << 24); // alpha
				argb |= ((int) byteArray[bytecount + 1] & 0xFF); // blue
				argb |= (((int) byteArray[bytecount + 2] & 0xFF) << 8); // green
				argb |= (((int) byteArray[bytecount + 3] & 0xFF) << 16); // red
				pixels[row][col] = argb;
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
				pixels[row][col] = argb;
				// System.out.println(row + "-" + col + "-" + result[row][col]);
				col++;
				if (col == imageWidth) {
					col = 0;
					row++;
				}

			}
		}
	}

	public int[][] getPixels() {
		return pixels;
	}

	public void setPixels(int[][] pixels) {
		this.pixels = pixels;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	
	public boolean hasAlpha() {
		return hasAlpha;
	}


	public void setAlpha(boolean hasAlpha) {
		this.hasAlpha = hasAlpha;
	}

	
}
