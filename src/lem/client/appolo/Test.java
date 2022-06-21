package lem.client.appolo;

import lem.client.planete.Lune;
import lem.client.planete.Terre;

public class Test {


	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		double distanceTerreLune = 384399000d;
		Terre terre = new Terre(5.9722e24d, 6378137d, 0d, 0d);
		
		Lune lune = new Lune(7.349e22d, 3474800d/2, distanceTerreLune*Math.cos(Math.PI/4d), distanceTerreLune*Math.sin(Math.PI/4d));
		
		// SM + LEM
		double x = lune.distanceTerre*Math.cos(Math.PI/4d)*0.97;// à 90% du chemin
		double y = lune.distanceTerre*Math.sin(Math.PI/4d)*0.97;
		double masseTotale = 30370d + 15000d;
		double masseCarburant= 18413d;
		// test with v = 8km/s
		double vx = 500d*Math.cos(Math.PI/4d);
		double vy = 500d*Math.sin(Math.PI/4d);
		double poussee = 91200d;
		double consommation = poussee/3090d;
		double alpha = 45d;
		
		Ship ship = new Ship(masseTotale,masseCarburant,x,y,vx,vy,poussee,consommation,alpha);
		
		// lune
		double[] posLune = lune.positions(0);
		double distanceLune2  = Math.pow(x - posLune[0], 2.0d)  +Math.pow(y- posLune[1], 2.0d);
		double gamma = Math.atan((posLune[1] - y) /(posLune[0] - x) );// angle L- ship

		double luneAcc = lune.g0* Math.pow(lune.rayon,2.0d)/distanceLune2;		
		// acceLune a 90% de la lune
		
		
		
		System.err.println(luneAcc);
	}

}
