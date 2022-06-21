package lem.client.planete;

public class Lune extends Planete {
	
	private double alpha0;
	public double distanceTerre = 384399000d;
	private double tempsRotation = 28*3600*24;// 28j

	public Lune(double masse, double rayon, double px0, double py0) {
		super(masse, rayon, px0, py0);
		// TODO Auto-generated constructor stub
		alpha0 = Math.acos(px0/distanceTerre);
//		g0 = 1.623d;


	}

	@Override
	public double[] positions(double t) {
		double alpha = alpha0 +  (2d*Math.PI*t)/tempsRotation;
		positions[0] = Math.cos(alpha)*distanceTerre;
		positions[1] = Math.sin(alpha)*distanceTerre;
		
		return positions;
	}

}
