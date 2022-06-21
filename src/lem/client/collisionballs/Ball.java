package lem.client.collisionballs;

import com.google.gwt.canvas.dom.client.Context2d;

public class Ball {
	private double x, y, vx, vy, radius;
	private Context2d context;
	private int width;
	private int height;

	public Ball(double x, double y, double vx, double vy, double diameter,Context2d context) {
		super();
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.radius = diameter/2.0;
		this.context = context;
		width = context.getCanvas().getWidth();
		height = context.getCanvas().getHeight();


	}
	
	public void draw()
	{
		context.beginPath();
		context.arc(x, y, radius, 0.0, 2.0*Math.PI);
		context.fill();
	}
	
	public void move()
	{
		x += vx;
		y += vy;
	}
	public void testCollisionWithWalls()
	{
		  // left
		  if (x < radius) { // x and y of the ball are at the center of the circle
		    x = radius;     // if collision, we replace the ball at a position
		    vx *= -1;            // where it's exactly in contact with the left border
		  }                           // and we reverse the horizontal speed
		  // right
		  if (x > width - (radius)) {
		    x = width - (radius);
		    vx *= -1;
		  }
		  // up
		  if (y < radius) {
		    y = radius;
		    vy *= -1;
		  }
		  // down
		  if (y > height - (radius)) {
		    y = height - (radius);
		    vy *= -1;
		  }		
	}
}
