import javax.swing.*;
import java.awt.*;

public class Mandelbrot {
	
	public static void main(String[] args) {
		
		//set up window
		JFrame window = new JFrame();
		window.setSize(1200, 800);
		window.setTitle("Mandelbrot");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//add fractal drawing area
		Container pane = window.getContentPane();
		FractalDrawer drawer = new FractalDrawer();
		drawer.setBackground(Color.BLACK); //set BG color
		pane.add(drawer);
		
		//set up keyboard focus
		drawer.setFocusable(true);
		drawer.requestFocusInWindow();
		
		window.setVisible(true);
		
		//Command-line output of fractal
		/*double i, j;
		int iterations;
		
		//i = imaginary, vertical
		for(i=IMAX; i>=IMIN; i-=(IMAX-IMIN)/HEIGHT){
			//j = real, horizontal
			for(j=RMIN; j<=RMAX; j+=(RMAX-RMIN)/WIDTH){
				iterations=IteratePoint(j, i);
				if(iterations > ITERMAX){System.out.print("*");}
				else{System.out.print(" ");}
			}
			System.out.println();
		}*/
		
	}
	
	/* int IteratePoint (double, double)
	 * ---------------------------------
	 * Iterates a point on the complex plane according to the function z_n+1 = z_n^2 + C
	 * where C is the initial value passed to this function. z0=C.
	 * 
	 * It returns the number of iterations it takes for the point to exit a circle of radius 2
	 * around the origin. (If a point is outside this circle, it will iterate to infinity). 
	 * 
	 * If point is already outside circle, return 0
	 * If point exits circle at some point, return # of iterations (1 to ITERMAX)
	 * If point never escapes, return ITERMAX+1; 
	 */
	
	/* CLASSIC MANDELBROT FUNCTION -- Uses doubles instead of Complex, only does power 2 */
	public static int IteratePoint(double real, double imag, int itermax, boolean isJulia, double juliaX, double juliaY)
	{
		int i; //number of iterations
		double iter_real=real, iter_imag=imag;
		double rtemp, itemp; //store intermediary steps of calculations
		
		//check if point is already outside of circle
		if(real*real + imag*imag > 4){return 0;}
		
		if(isJulia){real=juliaX; imag=juliaY;}
		
		for(i=1; i<=itermax; i++){
			//square z
			rtemp = iter_real*iter_real - iter_imag*iter_imag;
			itemp = iter_real*iter_imag*2;
			
			rtemp += real;
			itemp += imag;
			
			if((rtemp*rtemp + itemp*itemp) > 4){return i;} //check if point has escaped
			
			iter_real = rtemp;
			iter_imag = itemp;
		}
		
		return i;
	}	
	
	public static int IteratePoint(Complex z, int itermax, double power, boolean isJulia, Complex julia)
	{
		//check if point is already outside of circle
		if(z.norm() > 4){return 0;}

		int i; //number of iterations
		Complex C = isJulia ? julia : z;
		
		if(power==2){ //if power = 2, use faster function for classic mandelbrot
			for(i=1; i<=itermax; i++){
				//z^2 + C
				z = Complex.sum(z.square(), C);
				if(z.norm() > 4){return i;} //check if point has escaped
			}
		}
		else{
			for(i=1; i<=itermax; i++){
				//z^n + C
				z = Complex.sum(Complex.pow(z, (int) power), C);
				if(z.norm() > 4){return i;} //check if point has escaped
			}
		}
		
		
		return i;
	}

}
