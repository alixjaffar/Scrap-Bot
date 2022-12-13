package arena;

import bots.CloseBot;

public class TestCloseBot {

	/**
	 * This method runs all of the tests for CloseBot. When many tests are developed, it may be
	 * helpful to group them into methods that are invoked from main().
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Setup arguments for call to getMove()
		 */
		BotInfo me = new BotInfo(200, 300, 0, "");
		
		BotInfo[] liveBots = new BotInfo[1];
		BotInfo info = new BotInfo(200, 200, 0, "");
		liveBots[0] = info;
		
		BotInfo[] deadBots = new BotInfo[0];
		
		Bullet[] bullets = new Bullet[0];
		
		/**
		 * The object to test.
		 */
		CloseBot bot = new CloseBot();
		
		/**
		 * Test different situations. Here is one example.
		 */
		
		/**
		 * The bot shots when another live bot is present north of the CloseBot exactly 100 pixels away.
		 */
		if (bot.getMove(me, true, liveBots, deadBots, bullets) == BattleBotArena.FIREUP)
			System.out.println("Fire above test passed.");
		else
			System.out.println("Fire above test failed.");
	}

}
