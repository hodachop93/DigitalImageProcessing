package day1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class GrayScale {
	BufferedImage image;
	int width, height;

	public GrayScale() {
		try {
			File input = new File("digital_image_processing.jpg");
			image = ImageIO.read(input);
			width = image.getWidth();
			height = image.getHeight();
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Color color = new Color(image.getRGB(i, j));
					int red = (int) (color.getRed() * 0.299);
					int green = (int) (color.getGreen() * 0.587);
					int blue = (int) (color.getBlue() * 0.114);
					int sum = red + green + blue;
					Color newColor = new Color(sum, sum, sum);
					System.out.println("No " + i + "," + j);
					image.setRGB(i, j, newColor.getRGB());
				}
			}

			File outFile = new File("gray_scale_picture.jpg");
			ImageIO.write(image, "jpg", outFile);
			System.out.println(outFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GrayScale grayScale = new GrayScale();
	}
}
