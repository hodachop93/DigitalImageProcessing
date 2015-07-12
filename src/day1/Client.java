package day1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

public class Client {
	private static final int PORT = 4000;

	public static void main(String[] args) {
		BufferedImage img = null;
		Socket soc = null;
		try {
			soc = new Socket("localhost", PORT);
			System.out.println("Client is running");
			System.out.println("Reading image from disk");

			img = ImageIO.read(new File("digital_image_processing.jpg"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			baos.flush();

			byte[] bytes = baos.toByteArray();
			baos.close();
			System.out.println("Sending image to server");

			OutputStream os = soc.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeInt(bytes.length);
			System.out.println("Length of byte array: " + bytes.length);
			dos.write(bytes, 0, bytes.length);
			System.out.println("Image sent to server");

			dos.close();
			os.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (!soc.isClosed())
					soc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
