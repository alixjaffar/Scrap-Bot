package arena;

import bots.ScrapBot;

public class TestScrapAMethods {
    public static void main(String[] args) {

    /**
		Test cases for focusBot group A method 
		@author Jannah Khaja
        */

		System.out.println("FOCUSING NEARBY TESTS");
		ScrapBot scrap = new ScrapBot();
        BotInfo[] enemyBots = new BotInfo[1];

		//enemy bot to the left of scrap
		enemyBots[0] = new BotInfo(1, 5, 0, null);
		BotInfo me = new BotInfo(5, 5, 0, null);

		System.out.println("Left side test move: " + scrap.focusShooting(me, enemyBots));

		//enemy bot to the right of scrap
		enemyBots[0] = new BotInfo(15, 5, 0, null);
		me = new BotInfo(5, 5, 0, null);

		System.out.println("Right side test move: " + scrap.focusShooting(me, enemyBots));

		//enemy bot above scrap
		enemyBots[0] = new BotInfo(5, 1, 0, null);
		me = new BotInfo(5, 5, 0, null);

		System.out.println("Above test move: " + scrap.focusShooting(me, enemyBots));

		//enemy bot below scrap
		enemyBots[0] = new BotInfo(5, 20, 0, null);
		me = new BotInfo(5, 5, 0, null);

		System.out.println("Below test move: " + scrap.focusShooting(me, enemyBots));


    
}
}
