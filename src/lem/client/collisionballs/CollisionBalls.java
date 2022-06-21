package lem.client.collisionballs;

import java.util.ArrayList;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class CollisionBalls implements EntryPoint, AnimationCallback {

	private Canvas canvas;
	int width = 600; //pixel
	int height = 300;
	private Context2d context;
	ArrayList<Ball> balls = new ArrayList<Ball>();

	@Override
	public void onModuleLoad() {
		initUI();
		loop();
	}

	private void loop() {
		AnimationScheduler.get().requestAnimationFrame(this, canvas.getElement());			
	}

	private void initUI() {
		canvas = Canvas.createIfSupported();
		canvas.setStyleName("mainCanvas");
		canvas.setWidth("" + width + "px");
		canvas.setHeight("" + height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	
		context = canvas.getContext2d();
		RootPanel.get("canvas").add(canvas);
		
		createBalls(100);		
	}

	@Override
	public void execute(double timestamp) {
		  // clear the canvas
		context.clearRect(0, 0, width, height);	
		for (Ball ball : balls)
		{
		    // 1) move the ball
		    ball.move();
		    // 2) test if the ball collides with a wall
		    ball.testCollisionWithWalls();
		    // 3) draw the ball
		    ball.draw();

		}
		loop();
	}
	private void createBalls(int numberOfBalls) {
		  for(int i=0; i < numberOfBalls; i++) {
		    // Create a ball with random position and speed.
		    // You can change the radius
		    Ball ball = new Ball(width*Math.random(),
		                        height*Math.random(),
		                        (10*Math.random())-5,
		                        (10*Math.random())-5,
		                        30 - 20*Math.random(),context);
		    // add the ball to the array
		    balls.add(ball);
		   }
		}

}
