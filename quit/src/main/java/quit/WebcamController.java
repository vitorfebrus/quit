package quit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.github.sarxos.webcam.Webcam;

public class WebcamController {

	private Webcam webcam;

	private Gui window;

	private static Robot robot;

	private static final int SQ_SIZE = 5;

	private static final String COMPUTER_CAM = "Sony Visual Communication Camera 0";

	private static final String PHONE_CAM = "DroidCam Source 3 1";

	private int contNotRedSlice;
	
	private boolean rSliceOnFr;
	
	public WebcamController() throws Exception {
		window = new Gui();
		window.setVisible(true);

		webcam = Webcam.getWebcamByName(PHONE_CAM);
		webcam.setViewSize(new Dimension(640, 480));
		webcam.open();
		
		robot = new Robot();
		contNotRedSlice = 100;
		
		new ImageCatcher().start();

	}

	class ImageCatcher extends Thread {

		@Override
		public void run() {

			while (true) {

				try {

					BufferedImage image = webcam.getImage();

					if (webcam.getName().equals(COMPUTER_CAM)) {

						window.getImageHolder().setIcon(new ImageIcon(mirrorImage(image)));
						findRedSlice(mirrorImage(image));
						
					} else if (webcam.getName().equals(PHONE_CAM)) {

						window.getImageHolder().setIcon(new ImageIcon(image));
						findRedSlice(image);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * este metodo e' responsavel por verificar se um determinado frame tem ou nao
	 * uma fatia almostRed, caso tenha, move o mouse para o local onde a slice foi encontrada.
	 * 
	 * o metodo tambem conta quantos frames seguidos nao tem uma fatia vermelha, e reinicia
	 * o contador toda vez que um frame tem uma slice vermelha
	 * 
	 */
	
	public void findRedSlice(BufferedImage image) {

		rSliceOnFr = false;
		
		outerloop:
		for (int x = 0; x < image.getWidth(); x += SQ_SIZE) {
			for (int y = 0; y < image.getHeight(); y += SQ_SIZE) {

				if (almostRed(averageColor(slice(image, x, y)).getRGB())) {
					rSliceOnFr = true;
					System.out.println("Red slice! (" + x + ", " + y + ")" + " " + averageColor(slice(image, x, y)) + " " + contNotRedSlice);
					robot.mouseMove(x + 640, y + 150);
					break outerloop;
				}
				
			}
		}
		
		// se o laser for encontrado no frame atual, reinicia o contador de red frames
		contNotRedSlice = rSliceOnFr ? 0 : contNotRedSlice + 1;
		System.out.println(contNotRedSlice);
		
		if(contNotRedSlice < 20) {
			mousePress();
		}
		if(!rSliceOnFr) {
			mouseRelease();
		}
		
		
	}

	public BufferedImage mirrorImage(Image myImage) {

		BufferedImage bufferedImage = new BufferedImage(myImage.getWidth(null), myImage.getHeight(null),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) myImage.getGraphics();

		Graphics gb = bufferedImage.getGraphics();
		gb.drawImage(myImage, 0, 0, null);
		gb.dispose();

		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-myImage.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		bufferedImage = op.filter(bufferedImage, null);

		g2d.drawImage(myImage, 10, 10, null);
		g2d.drawImage(bufferedImage, null, 300, 10);

		return bufferedImage;

	}

	public BufferedImage slice(BufferedImage myImage, int x, int y) {

		return myImage.getSubimage(x, y, SQ_SIZE, SQ_SIZE);
	}

	public boolean almostRed(int pixel) {

		Color pColor = new Color(pixel);

		if (pColor.getRed() > 180 && pColor.getGreen() < 110 && pColor.getBlue() < 100) {
			return true;

		}
		return false;

	}

	public static Color averageColor(BufferedImage bi) {

		long sumr = 0, sumg = 0, sumb = 0;
		for (int x = 0; x < bi.getWidth(); x++) {
			for (int y = 0; y < bi.getHeight(); y++) {
				Color pixel = new Color(bi.getRGB(x, y));
				sumr += pixel.getRed();
				sumg += pixel.getGreen();
				sumb += pixel.getBlue();
			}
		}

		int num = bi.getWidth() * bi.getHeight();

		return new Color((int) sumr / num, (int) sumg / num, (int) sumb / num);
	}

	public void mouseClick() {
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
		
	}
	
	public void mousePress() {
		robot.mousePress(MouseEvent.BUTTON1_DOWN_MASK);
	}
	
	public void mouseRelease() {
		robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK);
	}
	
}
