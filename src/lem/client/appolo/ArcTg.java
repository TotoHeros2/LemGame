package lem.client.appolo;

public class ArcTg {
// return [0, 2pi]
	static double arctg(double dx, double dy) {
		if (dx == 0)
		{
			if (dy > 0)
			{
				return Math.PI/2;

			}
			else
				return 3*Math.PI/2;
		}
		else if (dx > 0 && dy > 0) // I
		{
			return Math.atan(dy/dx);
		}
		else if (dx < 0 && dy > 0) // II
		{
			return Math.atan(-dx/dy) + Math.PI/2;	
		}
		else if (dx < 0 && dy < 0) // III
		{
			return Math.atan(dy/dx) + Math.PI;	
		}	
		else if (dx > 0 && dy < 0) // IV ok
		{
			return Math.atan(-dx/dy) + 3*Math.PI/2;	
		}	
		else
		{
			System.err.println("Not take care of atan for : " + dx + " / " + dy);
			return 0;
		}
	}
}
