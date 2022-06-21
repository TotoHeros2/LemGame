package lem.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.text.DecimalFormat;
import java.util.Date;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;


import com.google.gwt.i18n.client.NumberFormat;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LemGame implements EntryPoint, AnimationCallback {
	
	public interface GlobalImageResources extends ClientBundle {
		@Source("lem.gif")
		ImageResource lem();

	}
	private Context2d context;
	static final CssColor blackColor = CssColor.make(0, 0, 0);
	static final CssColor whiteColor = CssColor.make(255, 255, 255);
	static final CssColor redColor = CssColor.make(255, 0, 0);
	static final CssColor greenColor = CssColor.make(0, 255, 0);
	
//	private static DecimalFormat _decFormatter = new DecimalFormat("#####0.0#");
	
	private static NumberFormat _decFormatter = NumberFormat.getFormat("#####0.0#");
	

	private int SIZE_X_LEM = 21;
	private int SIZE_Y_LEM = 23;
	private double ratioVecteur = 0.10D;
	
	
	Canvas canvas;
	int width = 600; //pixel
	int height = 300;
	
	int winWidth = 30000; // 30km
//	int winHeight = 20000; // 20 km fix
	
	int winHeight = 16000; // 16 km fix
	
	
	Double startTimestamp = null;
	Double currentTimestamp = null;

	
	int temps = 0; // ms
	private Lem lem;
	private double dt;
	
	InputElement  sliderMassePourcentage;
	
	InputElement  sliderAngle;
	private Image imgLem;
	private Label altitude;
	private Label vx;
	private Label vy;
	private Label carburant;
	
	private Label tempsLabel;


	private int nbStep = 0;
//	private Label resultat;
	
	Button stopButton = null;
	
	boolean isStopped = false;
	
	Date startStop;
	long deltaTimeStop = 0;	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initUI();

		
		loop();
	}


	@Override
	public void execute(double timestamp) {
		// TODO Auto-generated method stub
		
		temps = getTime(timestamp);
		
		if (temps != 0)
		{
			update();
		}
		nbStep++;
		if (nbStep % 10 == 0)
		{
		 draw();
		}
//		if (this.lem.getX()[1] < 0.0D)
//		{
//			if ((Math.abs(this.lem.getX()[2]) > 5.0D) || (Math.abs(this.lem.getX()[3]) > 10.0D) || (Lem.alpha > 3.0D))
//				g2d.drawString("L'alunissage était trop violent. RIP", this.SIZE_X / 2, this.SIZE_Y / 2);
//			else
//				g2d.drawString("Félicitations, vous avez aluni.", this.SIZE_X / 2, this.SIZE_Y / 2);
//		}
		
		if (lem.moteurVide)
		{
	        context.setFillStyle(redColor);
	        context.fillText("Vous avez échoué. Plus de carburant !", width/2,height/2);
	        return;
		}
		
		
		
		
		if (lem.getX()[1] < 0)
		{
			if ((Math.abs(this.lem.getX()[2]) > 5.0D) || (Math.abs(this.lem.getX()[3]) > 10.0D) || (lem.alpha > 3.0D))
			{
		        context.setFillStyle(redColor);
		        context.fillText("L'alunissage était trop violent. RIP", width/2,height/2);				
			}
			else
			{
		        context.setFillStyle(greenColor);
		        context.fillText("Félicitations, vous avez aluni.", width/2,height/2);				
			}

		}
		else
		{
			loop();// loop ???
		}
	}
	
	private void draw() {

//		context.clearRect(0, 0, width, height);
		// init canvas black
        context.setFillStyle(blackColor);
        context.fillRect( 0,0, width, height);
        context.fill();

		// draw lem
		int[] position = transformScreenCoord(this.lem.getX()[0], this.lem.getX()[1]);
//		context.drawImage(ImageElement.as(imgLem.getElement()), position[0], position[1]);
//		context.fillText("X", position[0], position[1]);

		drawRotatedLem(position);
		
        

		
// sol + echelle
        context.setStrokeStyle(whiteColor);
        context.setFillStyle(whiteColor);

        int[] p1 = transformScreenCoord(0.0d, 1.d);
        int[] p2 = transformScreenCoord(winWidth -5, 1.0d);
        drawLine(context,p1[0],p1[1],p2[0],p2[1]);
		int nbSaut = (int)this.lem.getX()[0] / winWidth;
		int affich0 = nbSaut * winWidth/1000;
		double decallageForDisplayX = 30; // m 
		p1 = transformScreenCoord(decallageForDisplayX, decallageForDisplayX); // 5m
		context.fillText(" " + affich0 + " km", p1[0], p1[1] -5);
		p2 = transformScreenCoord(winWidth - 50*decallageForDisplayX, decallageForDisplayX);
		int affich30 = (nbSaut + 1) * winWidth/1000;
		context.fillText("" + affich30 + " km", p2[0] -5, p2[1] -5);
		
		// 15km 
		p1 = transformScreenCoord(0.0d, 15000);
		context.fillText(" 15 km", p1[0], p1[1]);

		// vitesse
        context.setStrokeStyle(redColor);
        context.setFillStyle(redColor);
        drawLine(context, position[0] + (SIZE_X_LEM / 2),  position[1] - (SIZE_Y_LEM / 2),  position[0]  + (SIZE_X_LEM / 2) + (int)(this.ratioVecteur * this.lem.getX()[2]),  position[1] - (SIZE_Y_LEM / 2) - (int)(this.ratioVecteur * this.lem.getX()[3]));
//      int vitesse = (int) (Math.sqrt(lem.getX()[2]*lem.getX()[2] + lem.getX()[3]*lem.getX()[3])*0.15);
//      context.fillRect(0,0, 2, vitesse);
//      context.fill();

		
//        drawLine(context,0,height - 5, width, height - 5);
//        context.fillRect( 0,height - 5, width, 1);
//        context.fill();
        
// put lem param utside
//		context.fillText("Temps = " + temps + " [ms]", width/2, height/2);	
//		context.fillText("Position = " + lem.getX()[0] + " / " + lem.getX()[1] + " [m]", width/2, 3*height/4);	

		this.altitude.setText(_decFormatter.format(lem.getX()[1]));
		this.vx.setText(_decFormatter.format(lem.getX()[2]));
		this.vy.setText(_decFormatter.format(lem.getX()[3]));
		this.carburant.setText(_decFormatter.format(lem.getX()[4] - lem.mmin));
		
		tempsLabel.setText(_decFormatter.format(lem.t));


	}

	private int getTime(double timestamp) {
		int temps = 0;
		if (startTimestamp == null)// debut
		{
			startTimestamp = timestamp;
			currentTimestamp = timestamp;

			temps = 0;
		}
		else
		{
			// stop retart button
			if (deltaTimeStop != 0)
			{
				startTimestamp += deltaTimeStop;
				currentTimestamp += deltaTimeStop;
				deltaTimeStop = 0;
			}
			dt = (timestamp - currentTimestamp)/1000;
			temps = (int) (timestamp - startTimestamp);
			currentTimestamp = timestamp;
		}
		return temps;
	}

	private void update() {
		lem.setStepSize(dt);
		lem.nextStep();
		
	}

	private void loop()
	{
		if (!isStopped)
		{
			AnimationScheduler.get().requestAnimationFrame(this, canvas.getElement());
		}
	}
	
	

	private void initUI() {
		ImageResource imfRes =  GWT.<GlobalImageResources>create(GlobalImageResources.class).lem();

		imgLem = new Image(imfRes);
		
		lem = new Lem();

		canvas = Canvas.createIfSupported();
		canvas.setStyleName("mainCanvas");
		canvas.setWidth("" + width + "px");
		canvas.setHeight("" + height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
	
		context = canvas.getContext2d();
		RootPanel.get("canvas").add(canvas);
	
		// poussee
		
		Label massePourcentageValue = Label.wrap(Document.get().getElementById("massePourcentageValue"));
		massePourcentageValue.setText("100");

		sliderMassePourcentage = InputElement.as(Document.get().getElementById("sliderMassePourcentage"));
		sliderMassePourcentage.setAttribute("min", "0");
		sliderMassePourcentage.setAttribute("max", "100");
		sliderMassePourcentage.setAttribute("step", "1");
		sliderMassePourcentage.setValue("100");
		

		


		Event.sinkEvents(sliderMassePourcentage, Event.ONCHANGE);
		Event.setEventListener(sliderMassePourcentage, new EventListener() {
		    @Override
		    public void onBrowserEvent(Event event) {
				int value = Integer.valueOf(sliderMassePourcentage.getValue());

				massePourcentageValue.setText(sliderMassePourcentage.getValue());
				lem.setPousseePourcentage(value);
		    }
		});	
		
		// angle
		
		sliderAngle = InputElement.as(Document.get().getElementById("sliderAngle"));

		Label angleValue = Label.wrap(Document.get().getElementById("angleValue"));
		angleValue.setText("90");	
		sliderAngle.setValue("90");

		Event.sinkEvents(sliderAngle, Event.ONCHANGE);
		Event.setEventListener(sliderAngle, new EventListener() {
		    @Override
		    public void onBrowserEvent(Event event) {
				int value = Integer.valueOf(sliderAngle.getValue());

				angleValue.setText(sliderAngle.getValue());
				lem.setAlpha(value);
		    }
		});	
		
		
		altitude = Label.wrap(Document.get().getElementById("altitude"));
		vx = Label.wrap(Document.get().getElementById("vx"));
		vy = Label.wrap(Document.get().getElementById("vy"));
		carburant = Label.wrap(Document.get().getElementById("carburant"));
		
		
		tempsLabel = Label.wrap(Document.get().getElementById("temps"));

		
//		resultat = Label.wrap(Document.get().getElementById("resultat"));

// solve focus attach to rootpanel
		
//		canvas.addKeyDownHandler(new KeyDownHandler() {
		RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) { // receive all ?????????
				event.preventDefault();
				event.stopPropagation();
				if (event.isDownArrow())
				{
					int value = Integer.valueOf(sliderMassePourcentage.getValue());
					if (value == 0)
						return;
					value--;
					massePourcentageValue.setText(String.valueOf(value));
					sliderMassePourcentage.setValue(String.valueOf(value));
					lem.setPousseePourcentage(value);	
				}
				else if (event.isUpArrow())
				{
					int value = Integer.valueOf(sliderMassePourcentage.getValue());
					if (value == 100)
						return;
					value++;
					massePourcentageValue.setText(String.valueOf(value));
					sliderMassePourcentage.setValue(String.valueOf(value));
					lem.setPousseePourcentage(value);	
				}
				else if (event.isLeftArrow())
				{
					int value = Integer.valueOf(sliderAngle.getValue());
					if (value == -90)
						return;
					value--;
					angleValue.setText(String.valueOf(value));
					sliderAngle.setValue(String.valueOf(value));
					lem.setAlpha(value);	
				}
				else if (event.isRightArrow())
				{
					int value = Integer.valueOf(sliderAngle.getValue());
					if (value == 90)
						return;
					value++;
					angleValue.setText(String.valueOf(value));
					sliderAngle.setValue(String.valueOf(value));
					lem.setAlpha(value);	
				}
				
			}
		}
		, KeyDownEvent.getType()// need for dom handler
		);
		// try to add focus for keyboard
//		canvas.setFocus(true);
		
		// stop button
		stopButton = Button.wrap(Document.get().getElementById("stopButton"));
		stopButton.setHTML("Stop simulation");
		stopButton.addClickHandler(new ClickHandler() {
			


			@Override
			public void onClick(ClickEvent event) {
				isStopped = ! isStopped;
				if (isStopped)
				{
					stopButton.setHTML("Redemarre");	
					startStop = new Date();
				}
				else
				{
					stopButton.setHTML("Stop simulation");	
//					canvas.setFocus(true);
					deltaTimeStop = new Date().getTime() - startStop.getTime(); // will shift the start of game
					loop();
				}
			}
		});
		
		
		
//		canvas.addKeyUpHandler(new KeyUpHandler() {
//			
//			@Override
//			public void onKeyUp(KeyUpEvent event) {
//				event.preventDefault();
//				event.stopPropagation();
//				int value = Integer.valueOf(sliderMassePourcentage.getValue());
//				if (value == 100)
//					return;
//				value++;
//				massePourcentageValue.setText(String.valueOf(value));
//				sliderMassePourcentage.setValue(String.valueOf(value));
//				lem.setPousseePourcentage(value);				
//			}
//		});
		
		
		
		
//		RootPanel.get().addHandler(new KeyDownHandler() {
//			
//			@Override
//			public void onKeyDown(KeyDownEvent event) {
//				if (event.isDownArrow())
//				{
////					int value = Integer.valueOf(sliderMassePourcentage.getValue());
////					value--;
////					massePourcentageValue.setText(String.valueOf(value));
////					lem.setPousseePourcentage(value);					
//				}
//				else if (event.isLeftArrow())
//				{
//					int value = Integer.valueOf(sliderAngle.getValue());
//					value--;
//					angleValue.setText(sliderAngle.getValue());
//					sliderAngle.setValue(String.valueOf(value));
//					lem.setAlpha(value);
//				}
//				else if (event.isRightArrow())
//				{
//					int value = Integer.valueOf(sliderAngle.getValue());
//					value++;
//					angleValue.setText(sliderAngle.getValue());
//					sliderAngle.setValue(String.valueOf(value));
//					lem.setAlpha(value);
//				}
//				
//			}
//		}, KeyDownEvent.getType());
//		Event.sinkEvents(RootPanel.getBodyElement(), Event.KEYEVENTS);		
//		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
//			  @Override
//			  public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
//			    if (event.getNativeEvent()
//			             .getType().equals("keydown")) {
////			      if (event.getNativeEvent().getAltKey() &&
////			          event.getNativeEvent().getKeyCode() == KeyCodes.KEY_S) 
//			    	if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ONE)	
//			      {
//						int value = Integer.valueOf(sliderMassePourcentage.getValue());
//						value--;
//						massePourcentageValue.setText(String.valueOf(value));
//						lem.setPousseePourcentage(value);				      }
//			    }
//			  }
//			});
		
		

	}
	
	// tranform m reel en pixel
	int[] transformScreenCoord(double X, double Y) {
		int x = (int)(1.0d*X * width / winWidth) % width;
		int y = (int) (height*(1.d - (Y  / winHeight)));
		return new int[] { x, y };
	}
	
	public void drawRotatedLem(int[] position){
//	    context.clearRect(0,0,canvas.width,canvas.height);

	    // save the unrotated context of the canvas so we can restore it later
	    // the alternative is to untranslate & unrotate after drawing
	    context.save();

	    // move to the center of the canvas
	    context.translate(position[0] + (SIZE_X_LEM / 2),position[1] - (SIZE_Y_LEM / 2));

	    // rotate the canvas to the specified degrees
	    context.rotate(-lem.alpha*Math.PI/180);

	    // draw the image
	    // since the context is rotated, the image will be rotated also
	    context.drawImage(ImageElement.as(imgLem.getElement()),-imgLem.getWidth()/2,-imgLem.getHeight()/2);


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
