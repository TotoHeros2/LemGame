package lem.client.planete;

public class Terre extends Planete{

	public Terre(double masse, double rayon, double px0, double py0) {
		super(masse, rayon, px0, py0);
		// TODO Auto-generated constructor stub
		positions[0] = px0;
		positions[1] = py0;
//		g0 = 9.81d;
	}

	@Override
	public double[] positions(double t) {
		// TODO Auto-generated method stub
		return positions;
	}




}
