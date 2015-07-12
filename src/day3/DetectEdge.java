package day3;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DetectEdge {
	public static void main(String[] args) throws IOException {
		File file = new File("pentagon.png");

		// Read image to int[][] pixels
		BufferedImage bufImgInput = ImageIO.read(file);

		CannyEdgeDetector detector = new CannyEdgeDetector();
		detector.setLowThreshold(0.5f);
		detector.setHighThreshold(1.0f);
		detector.setSourceImage(bufImgInput);
		detector.process();

		BufferedImage bufImgOut = detector.getEdgesImage();
		File outFile = new File("EdgesImageDetection.png");
		ImageIO.write(bufImgOut, "png", outFile);

	}
}
