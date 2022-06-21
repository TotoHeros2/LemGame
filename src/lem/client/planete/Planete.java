package lem.client.planete;

public abstract class Planete {
	
	public static double G = 6.67408e-11;
	
	public double masse;
	public double rayon;
	public double px0;
	public double py0;
	protected double[] positions = new double[2];

	public double g0;
	
	public Planete(double masse, double rayon, double px0, double py0) {
		super();
		this.masse = masse;
		this.rayon = rayon;
		this.px0 = px0;
		this.py0 = py0;
		g0 = G*masse/(rayon*rayon);
	}

	abstract public double[] positions(double t);

}
