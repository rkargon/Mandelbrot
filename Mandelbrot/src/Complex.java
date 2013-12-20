public class Complex {
	private double real;
	private double imag;
	
	public static Complex sum(Complex a, Complex b){return new Complex(a.real + b.real, a.imag+b.imag);}
	
	public static Complex product(Complex a, Complex b){return new Complex(a.real*b.real - a.imag*b.imag, a.real*b.imag + a.imag*b.real);}
	
	public static Complex quotient(Complex a, double b){return new Complex(a.real/b, a.imag/b);}
	
	//painfully slow :-(
	public static Complex pow(Complex a, int b)
	{
		if(b>=1){
			Complex tmp = a;
			while(b>=2){tmp = product(tmp, a);b--;}
			return tmp;
		}
		else if (b==0){return new Complex(1, 0);}
		else{return pow(a, -b).reciprocal();}
	}
	
	/* Constructors */
	public Complex(double r, double i)
	{
		real = r;
		imag = i;
	}
	
	public Complex(double a){
		real = a;
		imag=0;
	}
	
	public Complex(int r, int i)
	{
		real = (double)r;
		imag = (double)i;
	}

	/* Property Setters / Getters */
	public double getReal(){return real;}
	public double getImag(){return imag;}
	public void setReal(double r){real = r;}
	public void setImag(double i){imag = i;}
	
	@Override
	public String toString(){return real+" + "+imag+"i";}
	
	/* Unary Operators/Functions */
	public Complex neg(){return new Complex(-real, -imag);}
	
	public Complex conjugate(){return new Complex(real, -imag);}
	
	public double norm(){return (real*real+imag*imag);}
	
	public Complex scalarProduct(double a){return new Complex(a*real, a*imag);}
	
	public Complex reciprocal(){return quotient(conjugate(), norm());}
	
	public Complex square(){return new Complex(real*real - imag*imag, 2*real*imag);}
	
	/* Test Class*/
	public static void main(String[] args)
	{
		;
		
	}
}