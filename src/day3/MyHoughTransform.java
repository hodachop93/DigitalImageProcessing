package day3;

import java.io.IOException;

import javax.swing.text.TabableView;

import com.sun.org.apache.xml.internal.utils.Trie;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.ws.api.pipe.Tube;

import sun.awt.UngrabEvent;
import day2.ImageProcessing;

public class MyHoughTransform {
	public static final int MIN_CONTRAST = 100;

	public static void main(String[] args) throws IOException {
		// Doc du lieu tu file va luu vao kieu du lieu PixelData chua thong tin
		// can thiet ve buc anh
		PixelData pixelData = new PixelData("pentagon.png");
		// Lay du lieu la 2 chieu pixel de xu ly
		int[][] pixels = pixelData.getPixels();
		// Truoc khi thuc hien bien doi Hough, ta phai xu ly buc anh thanh
		// GRAYSCALE hoac tim bien buc anh bang pp Canny
		pixels = ImageProcessing.ConvertRGBToGrayscale(pixels);

		int thetaAxisSize = pixelData.getImageWidth();
		int rAxisSize = pixelData.getImageHeight();
		// Tao khong gian Polar
		int[][] pixelsPolar = startHoughTransform(pixels, thetaAxisSize,
				rAxisSize, MIN_CONTRAST);
		// Chuyen cac gia tri trong khong gian Polar thanh cac muc xam tuong ung
		// tu 0-> 255
		pixelsPolar = adjustToImage(pixelsPolar);
		// Luu anh
		ImageProcessing.writeImage("PolarSpace.png", pixelsPolar);

	}

	private static int[][] adjustToImage(int[][] pixelsPolar) {
		int width = pixelsPolar[0].length;
		int height = pixelsPolar.length;
		int[][] output = new int[height][width];
		// Lay gia tri lon nhat trong mang khong gian Polar r,theta
		int max = 0;
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++) {
				if (pixelsPolar[row][col] > max)
					max = pixelsPolar[row][col];
			}
		// Hieu chinh
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++) {
				output[row][col] = (int) Math.round(pixelsPolar[row][col] * 255
						/ max);
			}
		return output;
	}

	private static int[][] startHoughTransform(int[][] pixels,
			int thetaAxisSize, int rAxisSize, int minContrast) {
		int imageWidth = pixels[0].length;
		int imageHeight = pixels.length;

		// Gia tri rMax = duong cheo cua hinh anh
		int rMax = (int) Math.ceil(Math.hypot(imageWidth, imageHeight));

		// Lay 1 nua kich thuoc cua truc r
		int halfRAxisSize = rAxisSize >> 1;

		// Khoi tao mang accumulator
		int[][] accumulator = new int[rAxisSize][thetaAxisSize];
		for (int row = 0; row < rAxisSize; row++)
			for (int col = 0; col < thetaAxisSize; col++) {
				accumulator[row][col] = 0;
			}

		// Khoi tao 2 mang cac gia tri sin va cos cua theta.
		double[] sinTheta = new double[thetaAxisSize];
		double[] cosTheta = new double[thetaAxisSize];
		for (int theta = 0; theta < thetaAxisSize; theta++) {
			double thetaRadian = theta * Math.PI / thetaAxisSize;
			sinTheta[theta] = Math.sin(thetaRadian);
			cosTheta[theta] = Math.cos(thetaRadian);
		}

		// Thuc hien bien doi Hough, tren toan bo buc anh
		for (int row = 0; row < imageHeight; row++)
			for (int col = 0; col < imageWidth; col++) {
				if (checkContrast(pixels, row, col, minContrast)) {
					/*
					 * Neu do tuong phan cua diem anh voi cac diem xung quanh
					 * dat yeu cau ta thuc hien tinh tat ca cac gia tri r_max,
					 * theta tuong ung voi tat ca cac duong thang di quanh diem
					 * do
					 */
					for (int theta = 0; theta < thetaAxisSize; theta++) {
						// Luu y: x la truc width, y la truc height
						// r = x * cos(theta) + y * sin(theta)
						int x = col, y = row;
						double r = x * cosTheta[theta] + y * sinTheta[theta];
						/*
						 * r khi tinh ra co the nhan gia tri am hoac vuot ra
						 * ngoai khoang rAxisSize, do do ta phai scale no lai de
						 * chi con nam trong khoang Tube 0 -> rAxisSize
						 */
						int rScaled = (int) Math
								.round(r * halfRAxisSize / rMax)
								+ halfRAxisSize;
						// Tang gia tri trong mang accumulator tuong ung len 1
						// don vi
						accumulator[rScaled][theta]++;
					}

				}
			}

		return accumulator;
	}

	/**
	 * Kiem tra do xam cua 1 diem anh
	 * 
	 * @param pixels
	 *            mang diem anh truyen vao
	 * @param row
	 *            toa do hang
	 * @param col
	 *            toa do cot
	 * @param minContrast
	 *            do sang toi thieu can so sanh
	 * @return true | false
	 */
	private static boolean checkContrast(int[][] pixels, int row, int col,
			int minContrast) {
		int width = pixels[0].length;
		int height = pixels.length;
		int centerValue = pixels[row][col];
		// Kiem tra muc xam cua 8 diem xung quah muc xam dang xet
		for (int i = 0; i < 9; i++) {
			if (i == 4)
				continue;
			int newRow = row + (i % 3) - 1;
			int newcol = col + (i / 3) - 1;

			if ((newRow < 0) || (newRow >= height) || (newcol < 0)
					|| (newcol >= width))
				continue;
			// Tra ve true neu xung quanh co nhung diem chenh lenh muc xam voi
			// diem trung tam
			// cao hon gia tri minContrast
			if (Math.abs(pixels[newRow][newcol] - centerValue) >= minContrast) {
				return true;
			}
		}
		return false;
	}
}
