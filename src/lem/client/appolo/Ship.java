package lem.client.appolo;

import lem.client.Derivatives;
import lem.client.RungeKutta8;
import lem.client.planete.Lune;
import lem.client.planete.Terre;

public class Ship implements Derivatives{
	public double masseTotale;
	public double masseCarburant;
	double poussee;
	public double consommation;
	public double alpha;
	
	// calcul
	Terre terre;
	Lune lune;
	double t;
	double dt;
	public double[] X = new double[5];
	private RungeKutta8 rk8 = null;
	public boolean moteurVide;
	public boolean moteurAllume;

	public double mUtile;
	private double distanceTerre2;
	private double distanceLune2;
	
	public Ship(double masseTotale, double masseCarburant, double x, double y, double vx, double vy,
			double poussee, double consommation, double alpha) {
		super();
		this.masseTotale = masseTotale;
		this.masseCarburant = masseCarburant;
		this.consommation = consommation;
		this.alpha = alpha;
		this.poussee = poussee;
		X[0] = x;
		X[1] = y;
		X[2] = vx;
		X[3] = vy;
		X[4] = masseTotale;
		this.mUtile = masseTotale - masseCarburant;
	}

	public void init(Terre terre, Lune lune, double deltaT)
	{
		this.dt = deltaT;
		this.terre = terre;
		this.lune = lune;
		rk8 = new RungeKutta8(deltaT);
		t = 0;
	}

	public void nextStep()
	{
		t += dt;
		X = rk8.step(t, X, this);

		if (X[4] <= mUtile)
		{
			moteurVide = true;
		}
	}

	@Override
	public double[] derivs(double t, double[] x) {
		double[] toReturn = new double[5];
		distanceTerre2  = Math.pow(x[0], 2.0d)  + Math.pow(x[1], 2.0d);
		double[] posLune = lune.positions(t);
		distanceLune2  = Math.pow(x[0] - posLune[0], 2.0d)  +Math.pow(x[1]- posLune[1], 2.0d);
		
//		double theta = Math.atan(x[0] / x[1]);// angle T- ship
		double theta = ArcTg.arctg(x[0], x[1]);
		
		
		toReturn[0] = x[2];
		toReturn[1] = x[3];
		// terre
		double terreConst = - terre.g0* Math.pow(terre.rayon,2.0d)/distanceTerre2;

		toReturn[2] = terreConst*Math.cos(theta);
		toReturn[3] = terreConst*Math.sin(theta);

		// lune
//		double gamma = Math.atan((posLune[1] - x[1]) /(posLune[0] - x[0]) );// angle L- ship
		double gamma = ArcTg.arctg(posLune[0] - x[0], posLune[1] - x[1]);
		double luneConst = lune.g0* Math.pow(lune.rayon,2.0d)/distanceLune2;

		toReturn[2] += luneConst*Math.cos(gamma);
		toReturn[3] += luneConst*Math.sin(gamma);

		if (moteurAllume && !moteurVide) {
			toReturn[2] -= poussee*Math.cos(alpha*Math.PI/180)/x[4]; // alpha en degré
			toReturn[3] -= poussee*Math.sin(alpha*Math.PI/180)/x[4];
					
					
			toReturn[4] =  -consommation;
		}
		else
		{
			toReturn[4] =  0;
		}
		return toReturn;
	}

 	public double distanceLune ()
 	{
 		return Math.sqrt(distanceLune2);
 	}

 	public double distanceTerre ()
 	{
 		return Math.sqrt(distanceTerre2);
 	}
 	
 	public double vitesse ()
 	{
		return Math.sqrt(Math.pow(X[2], 2.0d)  + Math.pow(X[3], 2.0d));
 	}	

}
