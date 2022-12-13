package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The CloseBot only shoots if another bot is directly above it at a distance of 100 units.
 *
 * @author S. Camilleri
 * @version 1.0 (April 27, 2022)
 */
public class CloseBot extends Bot {
	private String name;
	
	/**
	 * Bot image
	 */
	private Image current, up, down, right, left;
	
	/**
	 * Store the images loaded by the arena
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			if (images.length > 0)
				up = images[0];
			if (images.length > 1)
				down = images[1];
			if (images.length > 2)
				right = images[2];
			if (images.length > 3)
				left = images[3];
			current = up;
		}
	}

	/**
	 * This bot's strategy is to fire when another live bot is exactly 100 pixels north. The purpose of it
	 * is to demonstrate how unit testing can be done in the TestCloseBot class.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
		for (BotInfo bot : liveBots)
		{
			if (bot.getX() == me.getX() && me.getY()-100 == bot.getY() ) {
				return BattleBotArena.FIREUP;
			}
		}
		
		return BattleBotArena.STAY;
	}

	/**
	 * This bot does nothing to prepare for the next round.
	 */
	public void newRound()
	{
		current = up;
	}

	/**
	 * This bot does not send messages.
	 */
	public String outgoingMessage()
	{
		return null;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Close"+(botNumber<10?"0":"")+botNumber;
		return name;
	}

	/**
	 * Team "Arena"
	 */
	public String getTeamName()
	{
		return "Arena";
	}

	/**
	 * Draws the bot at x, y
	 * @param g The Graphics object to draw on
	 * @param x Left coord
	 * @param y Top coord
	 */
	public void draw (Graphics g, int x, int y)
	{
		if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
		}
	}

	/**
	 * This bot does not use incoming messages.
	 */
	public void incomingMessage(int botNum, String msg)
	{
	}

	/**
	 * Image names
	 */
	public String[] imageNames()
	{
		String[] images = {"roomba_up.png","roomba_down.png","roomba_left.png","roomba_right.png"};
		return images;
	}
}