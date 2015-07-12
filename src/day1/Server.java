package day1;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Server {
	private static final int PORT = 4000;
	public static void main(String[] args) {
		ServerSocket serverSoc = null;
		Socket socket;
		try {
			serverSoc = new ServerSocket(PORT);
			System.out.println("Server waiting for image");
			
			socket = serverSoc.accept();
			System.out.println("Client connected");
			
			//Tien hanh doc du lieu
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			int length = dis.readInt();
			System.out.println("Image size: " + length/1024 + "KB");
			
			//Get mang byte tu client gui len
			byte[] data = new byte[length];
			dis.readFully(data);
			dis.close();
			is.close();
			
			//Tao 1 luong de doc mang byte thanh image
			InputStream inputStream = new ByteArrayInputStream(data);
			BufferedImage bImg = ImageIO.read(inputStream);
			
			JFrame f = new JFrame("Server");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ImageIcon icon = new ImageIcon(bImg);
			JLabel label = new JLabel();
			label.setIcon(icon);
			f.add(label);
			f.pack();
			f.setVisible(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
