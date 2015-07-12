package day1;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test extends JPanel {
	public void paint(Graphics g) {
		Image image = createImageWithText();
		g.drawImage(image, 20, 20, this);
	}
	
		

	private Image createImageWithText() {
		BufferedImage bufferedImage = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		
		g.drawString("My name is Hop", 10, 10);
		g.drawString("My name is Hop", 10, 40);
		g.drawString("My name is Hop", 10, 80);
		g.drawString("My name is Hop", 10, 110);
		return bufferedImage;
	}
	
	public static void main(String[] args){
		JFrame frame = new JFrame();
		frame.getContentPane().add(new Test());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		frame.setVisible(true);
	} 
}
