package DoodleClassifier;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class DataProcessor {
	public static final int total = 100000;
	public static final int WIDTH = 28, HEIGHT = 28;
	public static byte[] outData;

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("data/computer.npy");
		byte[] data = Files.readAllBytes(path);
		System.out.println("Total Images: " + (data.length - 80) / 784);
		System.out.println("Total Saved: " + total + " " + 
		(double)total / ((data.length - 80) / 784) * 100 + "%");

		outData = new byte[total * WIDTH * HEIGHT];
		int outIndex = 0;
		for (int n = 0; n < total; n++) {
			int start = 80 + n * 784;
			for (int i = 0; i < 784; i++) {
				int index = i + start;
				outData[outIndex] = data[index];
				outIndex++;
			}
		}
		saveBytes("data/computer100000.bin", outData);
		
//		int start = 80;
//		int [] pixels = new int[784];
//		for(int i = 0; i < 784; i++) {
//			int index = i + start;
//			int val = 0xff;
//			val = (val << 8) + (255 - data[index]);
//			val = (val << 8) + (255 - data[index]);
//			val = (val << 8) + (255 - data[index]);
//			pixels[i] = val;
//		}
//		arrayToImage("FirstAirplane.png", 28, 28, pixels);
	}

	public static void arrayToImage(String path, int width, int height, int[] data) throws IOException {
		MemoryImageSource mis = new MemoryImageSource(width, height, data, 0, width);
		Image im = Toolkit.getDefaultToolkit().createImage(mis);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(im, 0, 0, null);
		ImageIO.write(bufferedImage, "jpg", new File(path));
	}

	public static void saveBytes(String path, byte[] data) {
		try (FileOutputStream fos = new FileOutputStream(path)) {
			fos.write(data);
			// fos.close(); There is no more need for this line since you had created the
			// instance of "fos" inside the try. And this will automatically close the
			// OutputStream
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
