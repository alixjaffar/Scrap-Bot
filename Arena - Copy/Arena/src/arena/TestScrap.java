
package arena;

import bots.ScrapBot;

public class TestScrap {

	
	public static void main(String[] args) {
		
	// 	BotInfo me = new BotInfo(300, 300, 0, "Scrap");
		
	// 	BotInfo[] liveBots = new BotInfo[1];
	// 	BotInfo info = new BotInfo(300, 200, 0, "Scrap");
	// 	liveBots[0] = info;
		
	// 	BotInfo[] deadBots = new BotInfo[0];
		
	// 	Bullet[] bullets = new Bullet[4];
		
		
	// 	Scrap bot = new Scrap();
	// 	Sentry bot = new Sentry();
		
	// //focus on enemy bot when in range
	// 	if (bot.getMove(me, true, liveBots, deadBots, bullets) == focusBot()){
	// 		System.out.println("uh-oh enemy bot detected.");
    //     }else{
    //         System.out.println("phew-- no enemy bot detected.");}
	// }
	ScrapBot scrap = new ScrapBot();
	Bullet[] bullets = new Bullet[1];
	bullets[0] = new Bullet(50,5,0,0);
	BotInfo me = new BotInfo(5, 6, 0, null);
	//up = 1
	//down = 2
	//left = 3
	//right = 4

	System.out.println("bullet on right side of bot test:" + scrap.dodgeBullets(me, bullets));

	bullets[0] = new Bullet(5,50,0,0);
	me = new BotInfo(5, 5, 0, null);

	System.out.println("below"+scrap.dodgeBullets(me, bullets));
}


//dodge bullets

	// if (bot.getMove(me, true, liveBots, deadBots, bullets) == dodgeBullet()){
	// 	System.out.println("Hooray! Scrap dodged a bullet!");
	// }
	// else{
	// 	System.out.println("Scrap's time has come to an end.");
	// }
}





