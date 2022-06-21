package lem.client.appolo;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import lem.client.planete.Lune;
import lem.client.planete.Terre;

import com.google.gwt.storage.client.Storage;


public class ApolloGame implements EntryPoint, AnimationCallback {
	
	private static double timeQuantum = 1.d/60.d; // imposé par le natif requestAnimationFrame
	
	private static NumberFormat _decFormatter = NumberFormat.getFormat("#####0.0#");

	
	public interface GlobalImageResources extends ClientBundle {
		@Source("smlem.png")
		ImageResource shipImage();

	}
	private Context2d context;
	static final CssColor blackColor = CssColor.make(0, 0, 0);
//	static final CssColor whiteColor = CssColor.make(255, 255, 255);
	static final CssColor redColor = CssColor.make(255, 0, 0);



	private Label tempsLabel;
	private Label tempsReelLabel;

//	private double lastTime;
	private Ship ship;
	private boolean isStopped = false;
	InputElement  sliderAngle;
	
	
	private int SIZE_X_SHIP = 35;
	private int SIZE_Y_SHIP = 13;
	
	
	Canvas canvas;
	int width = 1200; //pixel
	int height =1200;
	
	int winWidth = 384399000; // distance Terre lune !!
	
	int winHeight = 384399000; // 

	// game has the time
	private double tempsMission;// ms
	private double startTimestamp;
	
	private int nbOfTimeQuantum = 60;// will do compute and draw
	
	private int timeQuantumLoop = 0;
	
	
	Terre terre = new Terre(5.9722e24d, 6378137d, 0d, 0d);
	
	Lune lune = new Lune(7.349e22d, 3474800d/2, 384399000d*Math.cos(Math.PI/4d), 384399000d*Math.sin(Math.PI/4d));
	
	private Image imgShip;
//	private double deltaT = 10d; // calcul RK
//	private double deltaT = 1d/60d; // calcul reel
	private double deltaT = nbOfTimeQuantum*timeQuantum; // calcul reel

	
	private Label distanceLuneLabel;
	private Label distanceTerreLabel;
	private Label vitesseLabel;
	
	private ArrayList<int[]> trajectoire = new ArrayList<int[]>();
	
	private Storage appoloStore = null;
	private Label carburant;
	
	Button startButton = null;


	
	@Override
	public void onModuleLoad() {

		initUI();
		draw();
		// temps simulé
//		for (int i = 0;i < 100000;i++)
//		{
//			tempsMission += deltaT;
//			ship.nextStep();
//			draw();
//		}
		loop();

	}

	private void loop() {
		if (!isStopped )
		{
			AnimationScheduler.get().requestAnimationFrame(this, canvas.getElement());		
		}
	}

	private void initUI() {
		// start engine button
		startButton = Button.wrap(Document.get().getElementById("stopButton"));
		startButton.setHTML("Démarre moteur");
		startButton.addClickHandler(new ClickHandler() {
			


			@Override
			public void onClick(ClickEvent event) {
				ship.moteurAllume = !ship.moteurAllume;
				if (!ship.moteurAllume)
				{
					startButton.setHTML("Démarre moteur");	
				}
				else
				{
					startButton.setHTML("Stop moteur");	
				}
			}
		});
		
		
		appoloStore = Storage.getLocalStorageIfSupported();
		

		ImageResource imfRes =  GWT.<GlobalImageResources>create(GlobalImageResources.class).shipImage();

		imgShip = new Image(imfRes);
		
		canvas = Canvas.createIfSupported();
		canvas.setStyleName("mainCanvas");
		canvas.setWidth("" + width + "px");
		canvas.setHeight("" + height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	
		context = canvas.getContext2d();
		RootPanel.get("canvas").add(canvas);

		tempsLabel = Label.wrap(Document.get().getElementById("temps"));
		tempsReelLabel = Label.wrap(Document.get().getElementById("tempsReel"));

		startTimestamp = new Date().getTime();
		tempsMission = 0;
		tempsLabel.setText(_decFormatter.format(tempsMission));
		
		distanceLuneLabel = Label.wrap(Document.get().getElementById("distanceLune"));
		distanceTerreLabel = Label.wrap(Document.get().getElementById("distanceTerre"));
		vitesseLabel = Label.wrap(Document.get().getElementById("v"));
		carburant = Label.wrap(Document.get().getElementById("carburant"));



		// SM + LEM
		double x = lune.distanceTerre*Math.cos(Math.PI/4d + 0.1)*0.97;// à 97% du chemin
		double y = lune.distanceTerre*Math.sin(Math.PI/4d + 0.1)*0.97;
		double masseTotale = 30370d + 15000d;
		double masseCarburant= 18413d;
		// test with v = 8km/s
		double vx = 500d*Math.cos(Math.PI/4d);
		double vy = 500d*Math.sin(Math.PI/4d);
		double poussee = 91200d;
		double consommation = poussee/3090d;
		double alpha = 45d;
		alpha = 0d;
		
		String lsX = appoloStore.getItem("apollo.P2.x");
		

		if (appoloStore != null)
		{
			if (lsX == null)
			{
				// init from LS
				appoloStore.setItem("apollo.P2.x", Double.valueOf(x).toString());
				appoloStore.setItem("apollo.P2.y", Double.valueOf(y).toString());
				appoloStore.setItem("apollo.P2.vx", Double.valueOf(vx).toString());
				appoloStore.setItem("apollo.P2.vy", Double.valueOf(vy).toString());				
			}
			else
			{
				x = Double.valueOf(appoloStore.getItem("apollo.P2.x"));
				y = Double.valueOf(appoloStore.getItem("apollo.P2.y"));
				vx = Double.valueOf(appoloStore.getItem("apollo.P2.vx"));
				vy = Double.valueOf(appoloStore.getItem("apollo.P2.vy"));
			}


		}
		ship = new Ship(masseTotale,masseCarburant,x,y,vx,vy,poussee,consommation,alpha);
	
		sliderAngle = InputElement.as(Document.get().getElementById("sliderAngle"));

		Label angleValue = Label.wrap(Document.get().getElementById("angleValue"));
		angleValue.setText("0");	
		sliderAngle.setValue("0");

		Event.sinkEvents(sliderAngle, Event.ONCHANGE);
		Event.setEventListener(sliderAngle, new EventListener() {
		    @Override
		    public void onBrowserEvent(Event event) {
				int value = Integer.valueOf(sliderAngle.getValue());

				angleValue.setText(sliderAngle.getValue());
				ship.alpha = value;
		    }
		});	
		
		ship.init(terre, lune,deltaT);
		
	}
// should be called at 60 frames/s
	@Override
	public void execute(double timestamp) {
//		if (timestamp % 5 == 0) // tous les 10 fois = 0.16 s
//		{
//			compute(timestamp);
//			draw();
//		}
		timeQuantumLoop++;
		double tempsReel = (timestamp - startTimestamp)/1000d;
		tempsReelLabel.setText(_decFormatter.format(tempsReel));

//		// test * 100
//		tempsMission *= 3600; 
		tempsMission += deltaT;
		if (timeQuantumLoop % nbOfTimeQuantum == 0) // tous les 10 fois = 0.16 s
		{
			ship.nextStep();
			draw();
		}
		loop();
	}

	private void draw()
	{
        context.setFillStyle(redColor);
        context.setStrokeStyle(redColor);
		context.clearRect(0, 0, width, height);

//        context.fillRect( 0,0, width, height);
//        context.fill();
		int[] position = transformScreenCoord(ship.X[0], ship.X[1]);
		drawRotatedShip(position);

//		tempsLabel.setText(String.valueOf(timestamp) + " diff = " + String.valueOf(diff));	
//		tempsLabel.setText(String.valueOf(ship.timeMission()/1000.d));
		tempsLabel.setText(_decFormatter.format(tempsMission));
		carburant.setText(_decFormatter.format(ship.X[4] -ship.mUtile));

		double[] positionTerre = terre.positions(tempsMission);
		int[] positionTerreCanvas = transformScreenCoord(positionTerre[0], positionTerre[1]);
		drawCircle(context, positionTerreCanvas[0], positionTerreCanvas[1],(int)terre.rayon*width/winWidth);
		double[] positionLune = lune.positions(tempsMission);
		int[] positionLuneCanvas = transformScreenCoord(positionLune[0], positionLune[1]);
		drawCircle(context, positionLuneCanvas[0], positionLuneCanvas[1],(int)lune.rayon*width/winWidth);
		
		

		context.fillText("X", position[0], position[1]);
		
		distanceLuneLabel.setText(_decFormatter.format(ship.distanceLune()/1000.d));
		distanceTerreLabel.setText(_decFormatter.format(ship.distanceTerre()/1000.d));
		vitesseLabel.setText(_decFormatter.format(ship.vitesse()));
		
		for (int[] aPosition :trajectoire)
		{
			context.fillText(".", aPosition[0], aPosition[1]);
		}
		
		trajectoire.add(position);
		
		// test gravité lune
		// lune

//		double gamma = ArcTg.arctg(positionLune[0] - ship.X[0], (positionLune[1] - ship.X[1]));
//
//		double gLune = lune.g0* Math.pow(lune.rayon,2.0d)/Math.pow(ship.distanceLune(), 2);
//
//		double gLuneX = gLune*Math.cos(gamma);
//		double gLuneY = gLune*Math.sin(gamma);
//        context.setStrokeStyle(blackColor);
//        context.setFillStyle(blackColor);
//        drawLine(context, position[0] ,  position[1],  position[0]  +  (int)(10000*gLuneX),  position[1] - (int)(10000 * gLuneY));
//        
//        // attraction Terre 
//		double theta = ArcTg.arctg(ship.X[0], ship.X[1]);
//		
//		// terre
//		double gTerre = - terre.g0* Math.pow(terre.rayon,2.0d)/Math.pow(ship.distanceTerre(), 2);
//		double gTerreX = gTerre*Math.cos(theta);
//		double gTerreY = gTerre*Math.sin(theta);
//        drawLine(context, position[0] ,  position[1],  position[0]  +  (int)(10000*gTerreX),  position[1] - (int)(10000 * gTerreY));


	}
	// tranform m reel en pixel
	int[] transformScreenCoord(double X, double Y) {
//		int x = (int)(1.0d*X * width / winWidth) % width;
		int x = (int)(1.0d*X * width / winWidth);
		int y = (int) (height*(1.d - (Y  / winHeight)));
		
		return new int[] { x + 50, y - 50};// decallage 100/-100 pixel pour voir toute la terre
	}
	
	public void drawCircle(Context2d ctx, int p1x,int p1y,int rayon)
	{
		ctx.beginPath();
		ctx.arc(p1x,p1y,rayon, 0, 2.d*Math.PI);
		ctx.stroke();
	}
	
	public void drawRotatedShip(int[] position){
//	    context.clearRect(0,0,canvas.width,canvas.height);

	    // save the unrotated context of the canvas so we can restore it later
	    // the alternative is to untranslate & unrotate after drawing
	    context.save();

	    // move to the center of the canvas
//	    context.translate(position[0] + (SIZE_X_SHIP / 2),position[1] - (SIZE_Y_SHIP / 2));

	    context.translate(position[0] ,position[1]);
	    // rotate the canvas to the specified degrees
	    context.rotate(-ship.alpha*Math.PI/180);
//	    context.rotate(-Math.PI/4);
	    
	    //PG try to center the img on 
//	    context.translate(- (SIZE_X_SHIP / 2), - (SIZE_Y_SHIP / 2));


	    // draw the image
	    // since the context is rotated, the image will be rotated also
	    context.drawImage(ImageElement.as(imgShip.getElement()),-imgShip.getWidth()/2,-imgShip.getHeight()/2);


	    // we’re done with the rotating so restore the unrotated context
	    context.restore();
	}
	public void drawLine(Context2d ctx, int p1x,int p1y,int p2x,int p2y)
	{
		ctx.beginPath();
		ctx.moveTo(p1x, p1y);
		ctx.lineTo(p2x, p2y);
		ctx.stroke();
	}

}
