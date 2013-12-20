import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class FractalDrawer extends JPanel
{
	//sets up starting window boundaries, and zoom factor. Also has nice default Julia coordinates
	public double rmin=-3, imax=1.5, delta = 0.004, zoomfactor = 2, power=2;
	public Complex julia = new Complex(-0.156844471694257101941, -0.649707745759247905171);
	//default itermax value and coloring mode, as well as the number of samples to take on each side of pixel (actual number of samples is aliasingSamplesPerSide^2
	int itermax = 100, colormode = 1, aliasingSamplesPerSide=2, height, width;
	boolean isJulia=false, antiAliasing=false;
	Timer resizeTimer;
	
	//constructs the panel, and sets up listeners
	public FractalDrawer(){
		super();
		addMouseListener(new ViewMouseListener());
		addMouseWheelListener(new ViewWheelListener());
		addKeyListener(new ViewKeyListener());
		addComponentListener(new ViewResizeListener());
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 * -------------------------------------------------------------
	 * Redraw fractal viewer. Using rmin, imax, and delta, it assigns each pixel
	 * a point on the complex plane and sends it to IteratePoint(). Then, based
	 * on the number of iterations returned, that pixel is colored.
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		height = getHeight();
		width = getWidth();
		int i, j;
		long time;
		Color pixelColor;

		time = System.nanoTime();
		//i - imaginary, vertical, y
		for(i=0; i<=height; i++){
			//j = real, horizontal, x
			for(j=0; j<=width; j++){
				if(antiAliasing){pixelColor = getAntiAliasedPixel(j, i);}
				else {pixelColor=color(Mandelbrot.IteratePoint(new Complex(rmin + j*delta, imax-i*delta), itermax, power, isJulia, julia));}
				
				g.setColor(pixelColor);
				g.drawLine(j, i, j, i);
			}
		}
		System.out.println("This draw took "+ (double)(System.nanoTime() - time)/1000000+"ms");
	}

	private class ViewMouseListener extends MouseAdapter{
		public void mousePressed(MouseEvent e)
		{
			//left click, zoom in
			if(e.getButton() == MouseEvent.BUTTON1){zoom(e.getX(), e.getY(), 1.0/zoomfactor);}
			//scroll wheel click, just changes #of iterations
			else if(e.getButton() == MouseEvent.BUTTON2){setIterations(100);}
			//right click, zoom out
			else if(e.getButton() == MouseEvent.BUTTON3){zoom(e.getX(), e.getY(), zoomfactor);}
			repaint();
		}
	}
	
	/*
	 * Change the number of iterations used for each point when the wheel is scrolled. 
	 * (scrolling up increases iterations, ie getting more detail)
	 * Clicking the scroll wheel  
	 */
	private class ViewWheelListener implements MouseWheelListener{
		public void mouseWheelMoved(MouseWheelEvent e){
			//change the number of iterations when mouse is scrolled
			setIterations(itermax-e.getWheelRotation());
			if(itermax<0){setIterations(0);}
			repaint();
		}
	}

	private class ViewKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e){
			if(e.getKeyChar() == 'm'){
				//when 'm' is pressed, the Julia set of the point the mouse is pointing to at the time is displayed.
				//Pressing 'M' brings up a dialog for entering a specific julia coordinate
				setJulia(rmin+delta*getMousePosition().x, imax-delta*getMousePosition().y, !isJulia);
				System.out.println("Julia coordinates set at: "+julia.toString());
				repaint();
			}
			if(e.getKeyChar() == 'M'){
				//TODO: Add julia coords dialog
				System.out.println("Woah!");
			}
			else if(e.getKeyChar() == 'a'){
				//when 'a' is pressed, anti-aliasing is turned on
				antiAliasing = !antiAliasing;

				System.out.println(antiAliasing? "Antialiasing enabled, samples: "+aliasingSamplesPerSide : "Antialiasing disabled");
				repaint();
			}
			else if (e.getKeyChar() == 'A'){
				//Pressing 'A' brings up a dialog to enter specific number of samples
				aliasingSamplesPerSide = Integer.decode(JOptionPane.showInputDialog(null, "Enter number of samples on each side of pixel (actual # of samples is square of this): "));
				if(aliasingSamplesPerSide<1){aliasingSamplesPerSide=1;}
				antiAliasing = true; //when aliasing is toggled, will always end up 'true'. So bringing up the dialog always turns antialiasing ON. 
				System.out.println("Antialiasing enabled, samples: "+aliasingSamplesPerSide);
				repaint();
			}

			/* Change mandelbrot power */
			else if(e.getKeyChar()=='`'){
				power = Integer.decode(JOptionPane.showInputDialog(null, "Enter Mandelbrot power (int): "));
				System.out.println("Mandelbrot power: "+power);
				repaint();
			}
			else if (e.getKeyChar() == '!'){power = 1; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '@'){power = 2; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '#'){power = 3; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '$'){power = 4; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '%'){power = 5; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '^'){power = 6; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '&'){power = 7; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '*'){power = 8; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == '('){power = 9; System.out.println("Mandelbrot power "+power); repaint();}
			else if (e.getKeyChar() == ')'){power = 0; System.out.println("Mandelbrot power "+power); repaint();}
			
			else if(e.getKeyCode()==KeyEvent.VK_0){colormode=0; System.out.println("Color mode "+colormode); repaint();}
			else if(e.getKeyCode()==KeyEvent.VK_1){colormode=1; System.out.println("Color mode "+colormode); repaint();}
			else if(e.getKeyCode()==KeyEvent.VK_2){colormode=2; System.out.println("Color mode "+colormode); repaint();}
			else if(e.getKeyCode()==KeyEvent.VK_3){colormode=3; System.out.println("Color mode "+colormode); repaint();}
			else if(e.getKeyCode()==KeyEvent.VK_4){colormode=4; System.out.println("Color mode "+colormode); repaint();}
			else if(e.getKeyCode()==KeyEvent.VK_5){colormode=5; System.out.println("Color mode "+colormode); repaint();}
		}
	}
	
	private class ViewResizeListener extends ComponentAdapter{
		public void componentResized(ComponentEvent e){
		}
	}
	
	/* Sets the maximum number of iterations in the fractal */
	public void setIterations(int i){itermax=i;}

	/* Sets coordinates for Julia set, and can toggle whether or not Julia set is displayed */
	public void setJulia(double r, double i){julia.setReal(r); julia.setImag(i);}
	public void setJulia(double r, double i, boolean showJulia){julia.setReal(r); julia.setImag(i); isJulia=showJulia;}
	
	/*
	 * void zoom(int x, int y, double zoomamount)
	 * ------------------------------------------
	 * This function zooms in the fractal window 
	 * around a given center and by a given factor zoomAmount)
	 * 
	 * The function is designed to keep the center point of the 
	 * zooming in the same place relative to the the window (ie the same 
	 * pixel coordinates). This is done by moving the rmin and imax values accordingly
	 * based on how much the range of the window changes with each zoom.
	 * 
	 * [xy]PointRatio = the ratio of how far right/down the x/y coordinates of the zooming center are. 
	 * This is used to determine how much to move the window's view, down or to the right, to keep
	 * the zoom center from moving relative to the user.
	 * 
	 *   The user wants to click on the same point without moving the mouse, to be able to continuously zoom.
	 */
	public void zoom(int newcenterX, int newcenterY, double zoomAmount)
	{
		double xPointRatio = ((double) newcenterX)/getWidth();
		double yPointRatio = ((double) newcenterY)/getHeight(); //sees how close to the edge the cursor is
		rmin -= xPointRatio * (zoomAmount-1.0)*(getWidth()*delta); //before zooming in, moves top left corner of view, so that cursor zooms in on the same point with each click
		imax += yPointRatio * (zoomAmount-1.0)*(getHeight()*delta);
		delta *= zoomAmount;
	}
	
	/*
	 * Color color(int iterations)
	 * ----------------
	 * Returns a color based on the number of iterations given
	 * Colormode is used to determine the exact coloring scheme
	 */
	public Color color(int iterations)
	{
		int r, g, b;
		switch (colormode){
		
		//linear coloring of iterations, black = 0 and white = maximum
		case 1:
			return new Color(r = (255*iterations)/(itermax+1), r, r);
			
		//odd iterations colored white, even are black
		case 2:
			return new Color(r=255 * (iterations%2), r, r);
			
		//shows iteration that is 1 less than maximum, ie a thin border around the mandelbrot set. Works best with low iterations and anti-aliasing
		case 3:
			return new Color(r = (iterations==itermax) ? 255 : 0, r, r);
			
		//Cubic coloring: standard black-and-white coloring of iterations, except fraction iterations/maximum is squared before being colored.
		//this increases contrast between iterations.
		case 4:
			return new Color(r = (int) (255 * Math.pow((double)iterations/(itermax+1), 2.0)), r, r);
			
		//Coloring is based on sine of iteration number. R, G, B vlues are phase shifted to produced different hues
		case 5:
			r = (int) (Math.abs(Math.sin((double)iterations/8))*256);
			g = (int) (Math.abs(Math.sin((double)iterations/8 + 2.08))*256);
			b = (int) (Math.abs(Math.sin((double)iterations/8 + 4.16 ))*256);
			return new Color(r, g, b);
		
		//just shows points in mandelbrot set. Otherwise, pixel is painted black
		default:
			return new Color(r = (iterations==itermax+1) ? 255 : 0, r, r);
		}
	}
	
	/*
	 * Color mixcolors(Color... cols)
	 * ------------------------------
	 * Mixes colors by averaging RGB values
	 * RGB averaging is additive, and can cause counter-intuitive results, 
	 * such as blue+yellow = gray instead of green. However, because each iteration color
	 * is very similar to the ones around it, this should not be, and is not, a problem
	 */
	public static Color mixcolors(Color... cols)
	{
		int items = cols.length, i;
		int r=0, g=0, b=0;
		
		for(i=0; i<items; i++){
			r+=cols[i].getRed();
			g+=cols[i].getGreen();
			b+=cols[i].getBlue();
		}
		
		return new Color(r/items, g/items, b/items);
	}
	
	public Color getAntiAliasedPixel(int x, int y){
		int i, j;
		double rtemp, itemp;
		ArrayList<Color> colors = new ArrayList<Color>();
		
		for(i=1; i<=aliasingSamplesPerSide; i++){
			for(j=1; j<=aliasingSamplesPerSide; j++){
				rtemp = rmin + (x+(double)(i-1)/aliasingSamplesPerSide)*delta; //(i-1)/numSamples is the fraction of a pixel that each antialiasing sample refers to
				itemp = imax-(y+(double)(j-1)/aliasingSamplesPerSide)*delta;
				colors.add(color(Mandelbrot.IteratePoint(new Complex(rtemp, itemp), itermax, power, isJulia, julia)));
				//System.out.println((x+(double)(i-1)/aliasingSamplesPerSide)+", "+(y+(double)(j-1)/aliasingSamplesPerSide));
			}
		}
		//System.out.println(colors.toString());
		Color[] colorsArray = new Color[colors.size()];
		return mixcolors(colorsArray = colors.toArray(colorsArray));
	}
}