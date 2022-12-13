package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The Drone is a Bot that moves in squares and only ever fires in the direction it is facing.
 * It also turns if it detects that it has hit something. Occasionally puts out a happy drone message.
 *
 * @author sam.scott
 * @version 1.0 (March 3, 2011)
 */
public class Drone extends Bot {

	/**
	 * My name
	 */
	String name;
	/**
	 * My next message or null if nothing to say
	 */
	String nextMessage = null;
	/**
	 * Array of happy drone messages
	 */
	private String[] messages = {"I am a drone", "Working makes me happy", "I am content", "I like to vaccuum", "La la la la la...", "I like squares"};
	/**
	 * Image for drawing
	 */
	Image up, down, left, right, current;
	/**
	 * For deciding when it is time to change direction
	 */
	private int counter = 50;
	/**
	 * Current move
	 */
	private int move = BattleBotArena.UP;
	/**
	 * My last location - used for detecting when I am stuck
	 */
	private double x, y;

	/**
	 * Draw the current Drone image
	 */
	public void draw(Graphics g, int x, int y) {
		g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
	}


	/**
	 * Move in squares and fire every now and then.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots,
			BotInfo[] deadBots, Bullet[] bullets) {

		// decrease the counter to decide whether it is time to move
		counter--;
		// occasional messages
		if (Math.random() < 0.02)
			return BattleBotArena.SEND_MESSAGE;
		// Fire every now and then.
		if (counter % 25 == 0 && shotOK)
		{
			if (move == BattleBotArena.UP)
				return BattleBotArena.FIREUP;
			else if (move == BattleBotArena.DOWN)
				return BattleBotArena.FIREDOWN;
			else if (move == BattleBotArena.LEFT)
				return BattleBotArena.FIRELEFT;
			else if (move == BattleBotArena.RIGHT)
				return BattleBotArena.FIRERIGHT;
		}
		// change direction when the counter runs down or I detect I am stuck
		else if (counter == 0 || me.getX() == x && me.getY() == y)
		{
			if (move == BattleBotArena.UP)
				move = BattleBotArena.LEFT;
			else if (move == BattleBotArena.LEFT)
				move = BattleBotArena.DOWN;
			else if (move == BattleBotArena.DOWN)
				move = BattleBotArena.RIGHT;
			else if (move == BattleBotArena.RIGHT)
				move = BattleBotArena.UP;
			counter = 50+(int)(Math.random()*100);
		}
		// update my record of my most recent position
		x = me.getX();
		y = me.getY();
		// set the image to use for next draw
		if (move == BattleBotArena.UP || move == BattleBotArena.FIREUP)
			current = up;
		else if (move == BattleBotArena.DOWN || move == BattleBotArena.FIREDOWN)
			current = down;
		else if (move == BattleBotArena.LEFT || move == BattleBotArena.FIRELEFT)
			current = left;
		else if (move == BattleBotArena.RIGHT || move == BattleBotArena.FIRERIGHT)
			current = right;
		return move;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Drone"+(botNumber<10?"0":"")+botNumber;
		return name;
	}

	/**
	 * Team Arena!
	 */
	public String getTeamName() {
		return "Arena";
	}

	/**
	 * Pick a random starting direction
	 */
	public void newRound() {
		int i = (int)(Math.random()*4);
		if (i==0)
		{
			move = BattleBotArena.UP;
			current = up;
		}
		else if (i==1)
		{
			move = BattleBotArena.DOWN;
			current = down;
		}
		else if (i==2)
		{
			move = BattleBotArena.LEFT;
			current = left;
		}
		else
		{
			move = BattleBotArena.RIGHT;
			current = right;
		}

	}

	/**
	 * Image names
	 */
	public String[] imageNames()
	{
		String[] images = {"roomba_up.png","roomba_down.png","roomba_left.png","roomba_right.png"};
		return images;
	}

	/**
	 * Store the loaded images
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			current = up = images[0];
			down = images[1];
			left = images[2];
			right = images[3];
		}
	}

	/**
	 * Send my next message and clear out my message buffer
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Required abstract method
	 */
	public void incomingMessage(int botNum, String msg)
	{

	}

}
