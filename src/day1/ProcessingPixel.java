package day1;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ProcessingPixel {
	BufferedImage image;
	int width;
	int height;

	public ProcessingPixel() {
		try {
			File inputFile = new File("digital_image_processing.jpg");
			image = ImageIO.read(inputFile);
			width = image.getWidth();
			height = image.getHeight();
			int count = 0;

			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					count++;
					Color color = new Color(image.getRGB(i, j));
					System.out.println("S.No: " + count + " Red: "
							+ color.getRed() + " Green: " + color.getGreen()
							+ " Blue: " + color.getBlue());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ProcessingPixel objPixel = new ProcessingPixel();
	}
}
