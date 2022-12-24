package DoodleClassifier;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

/**
 *
 * @author harryhe
 */
public class Classifier extends Canvas implements Runnable, KeyListener, MouseListener {
	public static final int WIDTH = 280, HEIGHT = 280, TRUE_HEIGHT = 280 + 32;
	private Thread thread;
	private boolean running = false;
	private Window window;

	final int len = 784;
	final int totalData = 100000;
	final double percentTrain = 0.8;
	final int amountTrain = (int) Math.round(totalData * percentTrain);
	final int amountTest = (int) Math.round(totalData * (1 - percentTrain));

	final int AIRPLANES = 0;
	final int CAKES = 1;
	final int COMPUTERS = 2;

	byte[] airplanes_data;
	byte[] cakes_data;
	byte[] computers_data;

	Data airplanes;
	Data cakes;
	Data computers;

	NeuralNetwork nn;
	Test[] training;
	Test[] testing;
	ArrayList<Test> trainTemp;
	ArrayList<Test> testTemp;
	int epochCounter = 1;

	boolean inFrame;
	boolean mousePressed;
	int diameter;
	boolean clear;

//	byte[][] airplanes_train;
//	byte[][] cakes_train;
//	byte[][] computers_train;
//	
//	byte[][] airplanes_test;
//	byte[][] cakes_test;
//	byte[][] computers_test;

//	BufferedImage [] images;

	public Classifier() {
		this.setBackground(new Color(255, 255, 255));
		init();

		airplanes = prepareData(airplanes_data, AIRPLANES);
		cakes = prepareData(cakes_data, CAKES);
		computers = prepareData(computers_data, COMPUTERS);

		nn = new NeuralNetwork(784, 64, 3);
		// Training
		trainTemp = new ArrayList<>();
		for (int n = 0; n < 3; n++) {
			for (int i = 0; i < amountTrain; i++) {
				if (n == 0) {
					trainTemp.add(airplanes.training[i]);
				} else if (n == 1) {
					trainTemp.add(cakes.training[i]);
				} else if (n == 2) {
					trainTemp.add(computers.training[i]);
				}
			}
		}

		testTemp = new ArrayList<>();
		for (int n = 0; n < 3; n++) {
			for (int i = 0; i < amountTest; i++) {
				if (n == 0) {
					testTemp.add(airplanes.testing[i]);
				} else if (n == 1) {
					testTemp.add(cakes.testing[i]);
				} else if (n == 2) {
					testTemp.add(computers.testing[i]);
				}
			}
		}

//		for(int t = 1; t <= 5; t++) {
//			trainEpoch();
//			System.out.println("Epoch: " + t);
//			
//			double accuracy = testAll();
//			System.out.println("Accuracy: " + accuracy * 100 + "%");
//		}

//		Collections.shuffle(testTemp);
//		testing = new Test[amountTest * 3];
//		for(int i = 0; i < amountTest * 3; i++) {
//			testing[i] = testTemp.get(i);
//		}
//		
//		double accuracy = testAll();
//		System.out.println("Accuracy: " + accuracy * 100 + "%");
		inFrame = true;
		mousePressed = false;
		diameter = 8;
		clear = false;

		addKeyListener(this);
		addMouseListener(this);
		window = new Window(WIDTH, TRUE_HEIGHT, "Doodle Classifier", this);
	}

	public void init() {
		try {
			airplanes_data = Files.readAllBytes(Paths.get("data/airplane100000.bin"));
			cakes_data = Files.readAllBytes(Paths.get("data/cake100000.bin"));
			computers_data = Files.readAllBytes(Paths.get("data/computer100000.bin"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Data prepareData(byte[] data, int label) {
		Test[] training = new Test[amountTrain];
		Test[] testing = new Test[amountTest];
		for (int i = 0; i < totalData; i++) {
			byte[] temp = new byte[len];
			for (int j = 0; j < len; j++) {
				int index = j + i * len;
				temp[j] = data[index];
			}
			if (i < amountTrain) {
				training[i] = new Test(temp, label);
			} else {
				testing[i - amountTrain] = new Test(temp, label);
			}
		}
		return new Data(training, testing);
	}

	public void trainEpoch() {
		Collections.shuffle(trainTemp);
		training = new Test[amountTrain * 3];
		for (int i = 0; i < amountTrain * 3; i++) {
			training[i] = trainTemp.get(i);
		}

		// Train for one EPOCH
		for (int i = 0; i < training.length; i++) {
			double[] inputs = new double[len];
			int label = training[i].label;
			for (int j = 0; j < len; j++) {
				inputs[j] = (training[i].data[j] & 0xff) / 255.0;
			}
			double[] targets = { 0, 0, 0 };
			targets[label] = 1;
			try {
				nn.train(inputs, targets);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public double testAll() {
		Collections.shuffle(testTemp);
		testing = new Test[amountTest * 3];
		for (int i = 0; i < amountTest * 3; i++) {
			testing[i] = testTemp.get(i);
		}

		int correct = 0;
		for (int i = 0; i < testing.length; i++) {
			double[] inputs = new double[len];
			int label = testing[i].label;
			for (int j = 0; j < len; j++) {
				inputs[j] = (testing[i].data[j] & 0xff) / 255.0;
			}
			try {
				double[] guess = nn.predict(inputs);
				int classification = argmax(guess);
				if (classification == label) {
					correct++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return (double) correct / testing.length;
	}

	public int argmax(double[] arr) {
		if (arr.length == 0)
			return -1;
		double max = arr[0];
		int maxIndex = 0;
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
				maxIndex = i;
			}
		}
		return maxIndex;
	}

//	public void imagesInit() {
//		int total = 100;
//		images = new BufferedImage[total];
//		
//		for(int n = 0; n < total; n++) {
//			int start = n * 784;
//			int [] pixels = new int[784];
//			for(int i = 0; i < 784; i++) {
//				int index = i + start;
//				int val = 0xff;
//				val = (val << 8) + (255 - computers[index]);
//				val = (val << 8) + (255 - computers[index]);
//				val = (val << 8) + (255 - computers[index]);
//				pixels[i] = val;
//			}
//			try {
//				images[n] = createImage(28, 28, pixels);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				delta--;
			}
			if (running) {
				render();
				if (mousePressed) {
					try {
						Thread.sleep((long) (100 / amountOfTicks));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep((long) (1000 / amountOfTicks - 500 / amountOfTicks));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			frames++;
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
		stop();
	}

	public void tick() {
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
//		g.setColor(this.getBackground());
//		g.fillRect(0, 0, WIDTH, HEIGHT);
		if (clear) {
			g.setColor(this.getBackground());
			g.fillRect(0, 0, WIDTH, TRUE_HEIGHT);
			clear = false;
			g.setColor(new Color(0, 0, 0));
		}

		if (mousePressed && inFrame) {
			Point p = MouseInfo.getPointerInfo().getLocation();
			Point org = this.getLocationOnScreen();
			g.fillOval(p.x - org.x - diameter / 2, p.y - org.y - diameter / 2, diameter, diameter);
		}

//		for(int i = 0; i < images.length; i++) {
//			g.drawImage(images[i], i % 10 * 28, i / 10 * 28, null);
//		}

		g.dispose();
		bs.show();
	}

	public static BufferedImage createImage(int width, int height, int[] data) throws IOException {
		MemoryImageSource mis = new MemoryImageSource(width, height, data, 0, width);
		Image im = Toolkit.getDefaultToolkit().createImage(mis);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(im, 0, 0, null);
		return bufferedImage;
	}

	public BufferedImage getPanelImage() {
		Robot r;
		try {
			r = new Robot();
			return insertBackground(r.createScreenCapture(
					new Rectangle(this.getLocationOnScreen().x, this.getLocationOnScreen().y, WIDTH, TRUE_HEIGHT))
					.getSubimage(0, 0, WIDTH, HEIGHT));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage insertBackground(BufferedImage org) {
		BufferedImage output = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) output.getGraphics();
		g.setColor(this.getBackground());
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.drawImage(org, 0, 0, null);
		return output;
	}

	public static BufferedImage toBufferedImage(Image img) {
		BufferedImage convertImage = new BufferedImage(img.getWidth(null), img.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D bufferedGraphics = convertImage.createGraphics();
		bufferedGraphics.drawImage(img, 0, 0, null);
		bufferedGraphics.dispose();
		return convertImage;
	}
	
	public static double[] softmax(double [] arr) {
		double total = 0;
		double[] output = new double[arr.length];
		for(int i = 0; i < arr.length; i++) {
			total += arr[i];
		}
		for(int i = 0; i < arr.length; i++) {
			output[i] = arr[i] / total;
		}
		return output;
	}

	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}

	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Classifier();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_E) {
			System.out.println("Training Commence");
			trainEpoch();
			System.out.println("Epoch: " + epochCounter);
			epochCounter++;
		} else if (key == KeyEvent.VK_R) {
			System.out.println("Testing Commence");
			double accuracy = testAll();
			System.out.println("Accuracy: " + accuracy * 100 + "%");
		} else if (key == KeyEvent.VK_T) {
			System.out.println("Guess Commence");
			try {
				Robot r = new Robot();
				BufferedImage img = getPanelImage();
				BufferedImage rescaled = toBufferedImage(img.getScaledInstance(28, 28, java.awt.Image.SCALE_SMOOTH));
				double[] inputs = new double[len];
				for (int i = 0; i < rescaled.getHeight(); i++) {
					for (int j = 0; j < rescaled.getWidth(); j++) {
						inputs[i * rescaled.getWidth() + j] = (255 - (rescaled.getRGB(j, i) & 0xff)) / 255.0;
					}
				}
				double [] guess = nn.predict(inputs);
				int classification = argmax(guess);
				if(classification == AIRPLANES) {
					System.out.println("Airplane");
				} else if(classification == CAKES) {
					System.out.println("Cake");
				} else if(classification == COMPUTERS) {
					System.out.println("Computer");
				}
				System.out.println("Confidence: " + softmax(guess)[classification] * 100 + "%");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		} else if (key == KeyEvent.VK_S) {
			System.out.println("Save Commence");
			try {
				Robot r = new Robot();
				BufferedImage img = getPanelImage();
				ImageIO.write(img, "jpg", new File("ScreenShot.jpeg"));
				System.out.println("Written Image");
			} catch (AWTException | IOException e1) {
				e1.printStackTrace();
			}

		} else if (key == KeyEvent.VK_C) {
			clear = true;
		} else if (key == KeyEvent.VK_O) {
			try {
				nn.serialize("SavedNetwork.txt");
				System.out.println("Saved Network");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (key == KeyEvent.VK_P) {
			try {
				nn.deserialize("SavedNetwork.txt");
				System.out.println("Uploaded Saved Network");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (key == KeyEvent.VK_OPEN_BRACKET) {
			if (diameter > 1)
				diameter--;
		} else if (key == KeyEvent.VK_CLOSE_BRACKET) {
			if (diameter < 17)
				diameter++;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		inFrame = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		inFrame = false;
	}

}
