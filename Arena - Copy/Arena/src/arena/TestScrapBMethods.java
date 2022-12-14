package arena;

import bots.ScrapBot;

/**
This class is used to test the methods created for Group B.
@author Jannah Khaja
 */

public class TestScrapBMethods {
	public static void main(String[] args) {

		/**
		Test cases for dodgeBullets method
		*/
		System.out.println("DODGING BULLETS TESTS");
		ScrapBot scrap = new ScrapBot();
		Bullet[] bullets = new Bullet[1];
		//potential method return values
		//no move = 0
		//up = 1
		//down = 2
		//left = 3
		//right 4

		//bullet to the left of bot
		bullets[0] = new Bullet(1,5,1,0);
		BotInfo me = new BotInfo(5, 5, 0, null);
		
		System.out.println("Left side test move: " + scrap.dodgeBullets(me, bullets));

		//bullet to the right of bot
		bullets[0] = new Bullet(30,10,-1,0);
		me = new BotInfo(10, 10, 0, null);

		System.out.println("Right side test move: " + scrap.dodgeBullets(me, bullets));

		//bullet above the bot 
		bullets[0] = new Bullet(27,23,0,-1);
		me = new BotInfo(27, 27, 0, null);

		System.out.println("Above test move: " + scrap.dodgeBullets(me, bullets));

		//bullet below the bot
		bullets[0] = new Bullet(27,50,0,1);
		me = new BotInfo(27, 45, 0, null);

		System.out.println("Below test move: " + scrap.dodgeBullets(me, bullets));

		

		/**
		Test cases for spinning method 
		*/
		System.out.println("SHOOTING IN ALL DIRECTIONS TESTS");
		

		//shoots left
		scrap.setFrame(90);
		int lastSpin = BattleBotArena.FIREUP;
		
		if (scrap.spinning(lastSpin) == BattleBotArena.FIRELEFT)
			System.out.println("Fire above test passed.");
		else
			System.out.println("Fire above test failed.");


		//shoots right
		lastSpin = BattleBotArena.FIREDOWN;
		if (scrap.spinning(lastSpin) == BattleBotArena.FIRERIGHT)
			System.out.println("Fire above test passed.");
		else
			System.out.println("Fire above test failed.");
		
		
		//shoots above
		lastSpin = BattleBotArena.FIRERIGHT;
		if (scrap.spinning(lastSpin) == BattleBotArena.FIREUP)
			System.out.println("Fire above test passed.");
		else
			System.out.println("Fire above test failed.");
		
		
		//shoots below
		lastSpin = BattleBotArena.FIRELEFT;
		if (scrap.spinning(lastSpin) == BattleBotArena.FIREDOWN)
			System.out.println("Fire above test passed.");
		else
			System.out.println("Fire above test failed.");
	
		//helper method tests

		//test checkRadiusX
		System.out.println("Check radius X test");
		
		Bullet bullet = new Bullet(10, 5, 0, 0);
		me = new BotInfo(5, 5, 0, null);
		System.out.println("Expected result: true, actual results: " + scrap.checkRadiusX(me, bullet));

		bullet = new Bullet(200, 5, 0, 0);
		me = new BotInfo(5, 5, 0, null);
		System.out.println("Expected result: false, actual results: " + scrap.checkRadiusX(me, bullet));

		bullet = new Bullet(14, 5, 0, 0);
		me = new BotInfo(5, 5, 0, null);
		System.out.println("Expected result: true, actual results: " + scrap.checkRadiusX(me, bullet));

		bullet = new Bullet(2, 5, 0, 0);
		me = new BotInfo(5, 5, 0, null);
		System.out.println("Expected result: true, actual results: " + scrap.checkRadiusX(me, bullet));
		
        
		//test checkRadiusY
		System.out.println("Check radius Y test");

		bullet = new Bullet(20, 9, 0, 0);
		me = new BotInfo(20, 5, 0, null);
		System.out.println("Expected result: true, actual results: " + scrap.checkRadiusY(me, bullet));


        bullet = new Bullet(45, 20, 0, 0);
		me = new BotInfo(20, 5, 0, null);
		System.out.println("Expected result: false, actual results: " + scrap.checkRadiusY(me, bullet));


        bullet = new Bullet(5, 15, 0, 0);
		me = new BotInfo(20, 5, 0, null);
		System.out.println("Expected result: false, actual results: " + scrap.checkRadiusY(me, bullet));


        bullet = new Bullet(20, 6, 0, 0);
		me = new BotInfo(20, 5, 0, null);
		System.out.println("Expected result: true, actual results: " + scrap.checkRadiusY(me, bullet));

		//test checkBulletSpeed
		System.out.println("checkBulletSpeed tests");

		System.out.println("Expected result: false, actual results: " + scrap.checkBulletSpeed(0));

		System.out.println("Expected result: true, actual results: " + scrap.checkBulletSpeed(3));

		System.out.println("Expected result: true, actual results: " + scrap.checkBulletSpeed(5));

		System.out.println("Expected result: true, actual results: " + scrap.checkBulletSpeed(7));

	}
}
