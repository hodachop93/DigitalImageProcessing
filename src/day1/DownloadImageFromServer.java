package day1;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadImageFromServer {
	public static void main(String[] args) {
		String fileName = "digital_image_processing.jpg";
		String urlImage = "http://tutorialspoint.com/java_dip/images/" + fileName;
		System.out.println("Downloading file from: " + urlImage);
		
		//Tien hanh download
		URL url;
		try {
			url = new URL(urlImage);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(fileName);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = is.read(buffer)) != -1) {
				System.out.println("Buffer Read of Length: " + length);
				os.write(buffer, 0, length);
			}
			is.close();
			os.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
