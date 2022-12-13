package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.StringTokenizer;


import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The SentryBot just stays put and shoots randomly. It yells at anything that gets too close to it.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 */
public class SentryBot extends Bot {

	/**
	 * Image for drawing
	 */
	private Image image = null;
	/**
	 * Next message to send
	 */
	private String nextMessage;
	/**
	 * My name
	 */
	private String name;
	/**
	 * Timer for when to shoot
	 */
	private int countDown=0;
	/**
	 * Array of warning messages
	 */
	String[] messages = {"Stand back ","You are too close, ","Get back or face the consequences, ","Hands up, "};
	/**
	 * Array to remember who has been warned, so we don't spam chat with the
	 * same warning over and over again.
	 */
	private boolean[] warned;
	/**
	 * Name of my image
	 */
	public String[] imageNames()
	{
		String[] paths = {"starfish4.png"};
		return paths;
	}

	/**
	 * Save my image
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null && images.length > 0)
			image = images[0];
	}

	/**
	 * Shoots occasionally or yells at anything too close
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
		// check for a bot that's too close
		for (int i=0; i<liveBots.length; i++)
		{
			if (!warned[liveBots[i].getBotNumber()]) // skip if already warned this Bot
			{
				// compute the Manhattan distance to the Bot
				double d = Math.abs(me.getX()-liveBots[i].getX())+Math.abs(me.getY()-liveBots[i].getY());
				if (d < 50) // warn if within 50 pixels
				{
					nextMessage = messages[(int)(Math.random()*messages.length)]+liveBots[i].getName()+".";
					warned[liveBots[i].getBotNumber()] = true;
					return BattleBotArena.SEND_MESSAGE;
				}
			}
		}

		// if no warning, take a shot
		if (--countDown <= 0 && shotOK)
		{
			countDown = 15;
			int dir = (int)(Math.random()*4);
			if (dir == 0)
				return BattleBotArena.FIRERIGHT;
			else if (dir == 1)
				return BattleBotArena.FIRELEFT;
			else if (dir == 2)
				return BattleBotArena.FIREDOWN;
			else
				return BattleBotArena.FIREUP;
		}
		else
			return BattleBotArena.STAY;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Sentry"+(botNumber<10?"0":"")+botNumber;
		return name;
	}

	/**
	 * Reset the "warned" array
	 */
	public void newRound()
	{
		warned = new boolean[BattleBotArena.NUM_BOTS];
		for (int i=0; i<warned.length; i++)
			warned[i] = false;
	}

	/**
	 * Draw the sentry
	 */
	public void draw (Graphics g, int x, int y)
	{
		if (image != null)
			g.drawImage(image, x,y,Bot.RADIUS*2, Bot.RADIUS*2, null);
		else
		{
			g.setColor(Color.gray);
			g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
		}
	}

	/**
	 * Required method
	 */
	public void incomingMessage(int id, String msg)
	{
	}

	/**
	 * Team ARENA!
	 */
	public String getTeamName()
	{
		return "Arena";
	}

	/**
	 * Send and clear my message buffer
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}
}
