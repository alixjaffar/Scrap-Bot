package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The RandBot is a very basic Bot that moves and shoots randomly. Sometimes it overheats.
 * It trash talks when it kills someone.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 */
public class RandBot extends Bot {

	/**
	 * Next message to send, or null if nothing to send.
	 */
	private String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = {"Woohoo!!!", "In your face!", "Pwned", "Take that.", "Gotcha!", "Too easy.", "Hahahahahahahahahaha :-)"};
	/**
	 * Bot image
	 */
	Image current, up, down, right, left;
	/**
	 * My name (set when getName() first called)
	 */
	private String name = null;
	/**
	 * Counter for timing moves in different directions
	 */
	private int moveCount = 99;
	/**
	 * Next move to make
	 */
	private int move = BattleBotArena.UP;
	/**
	 * Counter to pause before sending a victory message
	 */
	private int msgCounter = 0;
	/**
	 * Used to decide if this bot should overheat or not
	 */
	private int targetNum = (int)(Math.random()*BattleBotArena.NUM_BOTS);
	/**
	 * The amount to sleep to simulate overheating because of excessive CPU
	 * usage.
	 */
	private int sleep = (int)(Math.random()*5+1);
	/**
	 * Set to True if we are trying to overheat
	 */
	private boolean overheat = false;

	/**
	 * Return image names to load
	 */
	public String[] imageNames()
	{
		String[] paths = {"drone_up.png", "drone_down.png", "drone_right.png", "drone_left.png"};
		return paths;
	}

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
	 * Generate a random direction, then stick with it for a random count between
	 * 30 and 90 moves. Randomly take a shot when done each move.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
		// for overheating
		if (overheat){try{Thread.sleep(sleep);}catch (Exception e){}}

		// increase the move counter
		moveCount++;

		// Is it time to send a message?
		if (--msgCounter == 0)
		{
			move = BattleBotArena.SEND_MESSAGE;
			moveCount = 99;
		}
		// Time to choose a new move?
		else if (moveCount >= 30+(int)Math.random()*60)
		{
			moveCount = 0;
			int choice = (int)(Math.random()*8);
			if (choice == 0)
			{
				move = BattleBotArena.UP;
				current=up;
			}
			else if (choice == 1)
			{
				move = BattleBotArena.DOWN;
				current=down;
			}
			else if (choice == 2)
			{
				move = BattleBotArena.LEFT;
				current=left;
			}
			else if (choice == 3)
			{
				move = BattleBotArena.RIGHT;
				current=right;
			}
			else if (choice == 4)
			{
				move = BattleBotArena.FIREUP;
				moveCount = 99; // make sure we choose a new move next time
				current=up;
			}
			else if (choice == 5)
			{
				move = BattleBotArena.FIREDOWN;
				moveCount = 99; // make sure we choose a new move next time
				current=down;
			}
			else if (choice == 6)
			{
				move = BattleBotArena.FIRELEFT;
				moveCount = 99; // make sure we choose a new move next time
				current=left;
			}
			else if (choice == 7)
			{
				move = BattleBotArena.FIRERIGHT;
				moveCount = 99; // make sure we choose a new move next time
				current=right;
			}
		}
		return move;
	}

	/**
	 * Decide whether we are overheating this round or not
	 */
	public void newRound()
	{
		if (botNumber >= targetNum-3 && botNumber <= targetNum+3)
			overheat = true;
	}

	/**
	 * Send the message and then blank out the message string
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Rand"+(botNumber<10?"0":"")+botNumber;
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
	 * If the message is announcing a kill for me, schedule a trash talk message.
	 * @param botNum ID of sender
	 * @param msg Text of incoming message
	 */
	public void incomingMessage(int botNum, String msg)
	{
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by "+getName()+".*"))
		{
			int msgNum = (int)(Math.random()*killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int)(Math.random()*30 + 30);
		}
	}

}
