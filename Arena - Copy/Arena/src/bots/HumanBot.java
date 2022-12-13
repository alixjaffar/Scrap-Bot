package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The HumanBot is a Bot that is under human control and should only be used for test purposes (e.g.
 * have a match with only two Bots - the one you are developing and this one - then move and fire
 * this Bot to test the behaviours of your AI.)<br><Br>
 *
 * When adding this Bot to the Arena, be sure to include the command <i>addKeyListener(Bots[n]);</i>
 * where <i>i</i> is the number of the HumanBot. Failure to do this will mean the Bot will not
 * react to keypresses.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 *
 */

public class HumanBot extends Bot implements KeyListener {

	private int move = BattleBotArena.STAY;
	private int resume;
	private boolean cocked = true;
	private String msg = null;

		public HumanBot() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw(Graphics g, int x, int y) {
		// TODO Auto-generated method stub
		g.setColor(Color.white);
		g.fillRect(x+2, y+2, RADIUS*2-4, RADIUS*2-4);
		if (!cocked)
		{
			g.setColor(Color.red);
			g.fillRect(x+3, y+3, RADIUS*2-6, RADIUS*2-6);
		}
	}

	@Override
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots,
			BotInfo[] deadBots, Bullet[] bullets) {
		if (msg != null)
			return BattleBotArena.SEND_MESSAGE;
		cocked = shotOK;
		int moveNow = move;
		move = resume;
		return moveNow;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Human";
	}

	@Override
	public String getTeamName() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String[] imageNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void incomingMessage(int botNum, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadedImages(Image[] images) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newRound() {
		msg="Arrow keys to move, WASD or CTRL to fire. Good luck!";
	}

	@Override
	public String outgoingMessage() {
		// TODO Auto-generated method stub
		String x = msg;
		msg = null;
		return x;
	}

	public void keyPressed(KeyEvent e) {
		//System.out.println(e.getKeyCode());
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			move = BattleBotArena.UP;
			resume = move;
			break;
		case KeyEvent.VK_DOWN:
			move = BattleBotArena.DOWN;
			resume = move;
			break;
		case KeyEvent.VK_LEFT:
			move = BattleBotArena.LEFT;
			resume = move;
			break;
		case KeyEvent.VK_RIGHT:
			move = BattleBotArena.RIGHT;
			resume = move;
			break;
		case KeyEvent.VK_CONTROL:
			resume = move;
			if (move == BattleBotArena.UP)
				move = BattleBotArena.FIREUP;
			else if (move == BattleBotArena.DOWN)
				move = BattleBotArena.FIREDOWN;
			else if (move == BattleBotArena.LEFT)
				move = BattleBotArena.FIRELEFT;
			else if (move == BattleBotArena.RIGHT)
				move = BattleBotArena.FIRERIGHT;
			break;
		case 'W':
			resume = move;
			move = BattleBotArena.FIREUP;
			break;
		case 'A':
			resume = move;
			move = BattleBotArena.FIRELEFT;
			break;
		case 'S':
			resume = move;
			move = BattleBotArena.FIREDOWN;
			break;
		case 'D':
			resume = move;
			move = BattleBotArena.FIRERIGHT;
			break;
		}

	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
