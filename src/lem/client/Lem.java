package lem.client;

public class Lem implements Derivatives
{

	double R0 = 1737000.0D;
	double g0 = 1.623D;
	double x0 = 0.0D;
	double y0 = R0 + 15000.0D;

	double u0 = 1695.0D;
	double v0 = 0.0D;

	double R02 = R0 * R0;

	double m0 = 15000.0D;
	public double mmin = 7000.0D;

	double Vejection = 2780.0D;
	double debit = 16.0D;
	double alpha0 = 90.0D;
	double pousseePourcentage;
	double pousseePourcentage0 = 1.0D;
	double pousseeMax = 45000.0D;
	public boolean moteurVide = false;
	public double alpha;
	private double[] X = new double[5];
	private RungeKutta8 rk8 = null;
	int nSteps = 1000;
	double t;
	double dt = 0.2D;

	public Lem()
	{

			t = 0.0D;
			X[0] = x0;
			X[1] = y0;
			X[2] = u0;
			X[3] = v0;
			X[4] = m0;
			alpha = alpha0;

			pousseePourcentage = pousseePourcentage0;
// test Kepler 
//			pousseePourcentage = 0.0;
			rk8 = new RungeKutta8(dt);
			rk8.setStepSize(dt);
	}
	public void reset() {
//		instance = null;
	}

	public void nextStep()
	{
		t += dt;
		X = rk8.step(t, X, this);

		if (X[4] <= mmin)
		{
			moteurVide = true;
		}
	}

	public double[] derivs(double t, double[] x)
	{
		double[] toReturn = new double[5];
		double poussee = 0.0D;
		if (!moteurVide) {
			poussee = pousseeMax * pousseePourcentage;
		}

		double theta = Math.atan(x[0] / x[1]);
		double cosa = Math.cos(alpha * 0.0174532925199433D - theta);
		double sina = Math.sin(alpha * 0.0174532925199433D - theta);
		double r2 = x[0] * x[0] + x[1] * x[1];

		toReturn[0] = x[2];
		toReturn[1] = x[3];
		toReturn[2] = (-g0 * x[0] * R02 / (r2 * Math.sqrt(r2)) - poussee * sina / x[4]);
		toReturn[3] = (-g0 * x[1] * R02 / (r2 * Math.sqrt(r2)) + poussee * cosa / x[4]);
		toReturn[4] = (-poussee / Vejection);

		return toReturn;
	}

//	public void main(String[] args)
//	{
//		Lem descenteLem1 = new Lem();
//		instance = descenteLem1;
//		t = 0.0D;
//		X[0] = x0;
//		X[1] = y0;
//		X[2] = u0;
//		X[3] = v0;
//		X[4] = m0;
//		alpha = alpha0;
//
//		pousseePourcentage = pousseePourcentage0;
//
//		rk8 = new RungeKutta8(dt);
//		rk8.setStepSize(dt);
//	}

	public void setPousseePourcentage(double pousseePourcentage) {
		this.pousseePourcentage = pousseePourcentage / 100.0D;
		if (moteurVide)
			pousseePourcentage = 0.0D; 
	}

	public synchronized void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public synchronized double[] getX()
	{
		double[] R = new double[5];
		double theta = Math.atan(X[0] / X[1]);
		double r2 = X[0] * X[0] + X[1] * X[1];
		double r = Math.sqrt(r2);
		R[0] = (theta * R0);
		R[1] = (r - R0);

		R[2] = (X[2] * (X[1] / r) - X[3] * (X[0] / r));
		R[3] = (X[2] * (X[0] / r) + X[3] * (X[1] / r));

		R[4] = X[4];
		return R;
	}
	public void setStepSize(double dt)
	{
		this.dt = dt;
		rk8.setStepSize(dt);	
	}
	

}