import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

class BreakOutRunner extends JFrame
{
	private static final int WIDTH = 820;
	private static final int HEIGHT = 700;

	public BreakOutRunner()
	{
		super("BreakOut");

		setSize(WIDTH,HEIGHT);

		//use the ClassTester to test your classes
		//before you start to write the full game
		//getContentPane().add( new ClassTester() );

		//uncomment this to make the game
		getContentPane().add( new BreakOut() );
				
		setVisible(true);
	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main( String args[] )
	{
		BreakOutRunner run = new BreakOutRunner();
	}
}