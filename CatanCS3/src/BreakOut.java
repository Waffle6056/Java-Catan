import java.awt.*;
import java.io.File;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

class BreakOut extends JPanel implements Runnable, KeyListener
{
	private boolean[] keys; // this stores booleans for when a key is pressed or not.

	//PROCESS EACH ONE OF THESE ONE AT A TIME
	private boolean start;
	private int level,frame,points;
	//this is for resizing
	private int orHeight,orWidth;
	private double heightRatio,widthRatio;
	private int cooldown,hp,invincablity;
	//private ArrayList<PowerUp> PowerUps;
	//private POW g;
	private int frametime;
	private int spawned;
	private NewBoard X=new NewBoard();



	public BreakOut() // create all instance in here
	{
		setBackground(Color.WHITE);
		level=1;
		cooldown =0;
		invincablity=0;

		restart();
		frame=0;
		orWidth=1;
		orHeight=1;
		keys = new boolean[9];
		addKeyListener( this );   	//
		setFocusable( true );		// Do NOT DELETE these three lines
		new Thread(this).start();	//
	}
	public void restart(){
		if (points!=0){
			System.out.println(points);
			points=0;
		}
		frametime=5;
		hp=2;
		spawned=0;
	start=false;
}

	public void paint( Graphics window )// all other paint methods and game logic goes in here.
	{
		if (frame==0){
			orWidth=getWidth();
			orHeight=getHeight();
			frame++;
		}
		ratio();//building Ratio
/*
		window.setColor(Color.black);
		window.fillRect(0,0,getWidth(),getHeight());
		Graphics2D g3 = (Graphics2D) window;
		Image img2 = Toolkit.getDefaultToolkit().getImage("Defender.png"); //use .gif or .png, you can choose the image
		g3.drawImage(img2, 0,0,getWidth(),getHeight(), this);
		//window.drawString("Mouse  coordinates " + "(" + MouseInfo.getPointerInfo().getLocation().x + "   " + MouseInfo.getPointerInfo().getLocation().y + ")", 250, 30 );
		keyReader();
		//if (frame==1) {
		//	Ship.setY(orHeight-10);
		//balls.get(0).setY(orHeight-40);
		//}

		//make stuff move
		/*
		 for (int i = PowerUps.size()-1; i >=0; i--) {//PowerUp collection
					if(pad.intersects(PowerUps.get(i))) {
						int type=PowerUps.get(i).getType();
						if (type==0) {
							cooldown++;
						} else if (type==1) {
							frametime+=10;
						}
						else if (type==2) {
							Ball b=balls.get(0);
							balls.add(new BreakableBall(pad.getX(),600,balls.get(0).getW(),balls.get(0).getH(),5,5));
						}
						PowerUps.remove(i);
					}
		 }
		 //Using POW
		 if (cooldown>0&& keys[6]){
			 g=new POW(balls.get(0).getX()-balls.get(0).getWidth()/3,balls.get(0).getY()-balls.get(0).getH()/3,balls.get(0).getW()*3,balls.get(0).getH()*3,10);
			 cooldown--;
		 }

		 */
		 //collisons
		if (frame%5==0) {
			//collide(window);
		}
		// draw stuff
		draw(window);


		if (!start) {
			Graphics2D g2 = (Graphics2D) window;
			Image img1 = Toolkit.getDefaultToolkit().getImage("BrickOut.png"); //use .gif or .png, you can choose the image
			if (level==4){
				img1 = Toolkit.getDefaultToolkit().getImage("BrickWin.png");
			}
		//	g2.drawImage(img1, 0, getHeight()/2, getWidth(), getHeight()/2, this);
		}
		//else {
			//if (frame%(100*level)==0 && frametime!=5){
			//	frametime--;
			//}
			//frame++;
		//}
		//if (bricks.isEmpty()){
		//	level++;
		//	restart();
			//System.out.println("You Win");
		//}
		keys[6] = false;
		frame++;
		if (cooldown!=0){
			cooldown--;
		}
		if (invincablity!=0){
		invincablity--;
		}
		if (hp<0){
			System.out.println("you lost");
			restart();
		}
	}
	public void keyReader(){
		if (keys[3]){// R is pressed
			System.out.println("Start Game");
			start=true;
			keys[3] = false;
			//balls.get(0).start();
		}
		if(keys[0]) // space is pressed
		{
			keys[0]=false;

		}
		if(keys[1]) // Left Arrow is pressed
		{
			//move the paddle left
		//	if (!start){
		//		balls.get(0).setX(Ship.getX()+Ship.getW()/2-balls.get(0).getW()/2);
		//	}
		}
		if(keys[2]) // Right Arrow is pressed
		{
			//move the paddle right
		//	if (!start){
//				balls.get(0).setX(Ship.getX()+Ship.getW()/2-balls.get(0).getW()/2);
		//	}
		}
		if(keys[7]) // Down Arrow is pressed
		{

			//if (!start){
			//	balls.get(0).setY(Ship.getX()+Ship.getW()/2-balls.get(0).getW()/2);
			//}
		}
		if(keys[8]) // Up Arrow is pressed
		{

			//if (!start){
			//	balls.get(0).setX(Ship.getX()+Ship.getW()/2-balls.get(0).getW()/2);
			//}
		}
		if (keys[5]){// Shift
		}
	}

	public void ratio(){
		int cuHeight = getHeight();
		int cuWidth = getWidth();
		widthRatio=((double) cuWidth)/((double)orWidth) ;
		heightRatio=((double) cuHeight)/((double) orHeight);
	}

	public void draw(Graphics window){
		X.paint(window,widthRatio,heightRatio);
	}

	public void PointReader(Graphics window){
		Font f=new Font("defender-arccade.ttf", Font.PLAIN,(int)(orHeight*heightRatio/10));
		window.setFont(f);
		window.setColor(Color.blue);
		window.drawString(""+points,10,100);
		for (int i = 0; i <= hp; i++) {
			String str="ShipRight.png";
			Graphics2D g2 = (Graphics2D) window;
			Image img1 = Toolkit.getDefaultToolkit().getImage(str); //use .gif or .png, you can choose the image
			g2.drawImage(img1,(int)(10*widthRatio), (int)((50*(i+1)+orHeight/10)*heightRatio), (int)(widthRatio*100), (int)(heightRatio*50), this);
		}
	}

// only edit if you would like to add more key functions	
	public void keyPressed(KeyEvent e)
	{
		//System.out.println(e.getKeyCode());
		if( e.getKeyCode()  == KeyEvent.VK_SPACE ) {//fire
			keys[0]=true;
		}
		if( e.getKeyCode()  == KeyEvent.VK_LEFT ) {//move left
			keys[1]=true;
		}
		if( e.getKeyCode()  == KeyEvent.VK_RIGHT ){//move right
			keys[2]=true;
		}
		if( e.getKeyCode()  == KeyEvent.VK_R ) {//start
			keys[3]=true;
		}
		if( e.getKeyCode()  == KeyEvent.VK_C ) {//crazy
			keys[4]=!keys[4];
//			if (keys[4]){
//				System.out.println("Crazy");
//			}
//			else {
//				System.out.println("Not Crazy");
//			}
		}
		if ( e.getKeyCode() == KeyEvent.VK_SHIFT) {//fast
			keys[5] = true;
		}
		if ( e.getKeyCode() == KeyEvent.VK_F) {
			keys[6] = true;
		}
		if ( e.getKeyCode() == KeyEvent.VK_UP) {//move up
		keys[7] = true;
	}
		if ( e.getKeyCode() == KeyEvent.VK_DOWN) {//move down
			keys[8] = true;
		}

	}
/*****~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*****/
// do not edit anything from this point on!!!
	public void keyTyped(KeyEvent e)
	{
		keyPressed(e);
	}		
	public void keyReleased(KeyEvent e)
	{
		if ( e.getKeyCode() == KeyEvent.VK_SHIFT) {
			keys[5] = false;
		}
		if ( e.getKeyCode() == KeyEvent.VK_UP) {//move up
			keys[7] = false;
		}
		if ( e.getKeyCode() == KeyEvent.VK_DOWN) {//move down
			keys[8] = false;
		}
		if( e.getKeyCode()  == KeyEvent.VK_LEFT ) {//move left
			keys[1]=false;
		}
		if( e.getKeyCode()  == KeyEvent.VK_RIGHT ){//move right
			keys[2]=false;
		}
		
	}	
	
	public void run()
	{
		try
		{
			while( true )
			{	
			   Thread.sleep( frametime );
			   repaint();
			}
		}
		catch( Exception e )
		{			
		}
	}
}