package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.StringTokenizer;


import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

public class ScrapBot extends Bot {
;
    private double closeBotY = 0;
    private double closeBotX = 0;
    int count = 0;

    private Image image = null;
    private int lastSpinMove = BattleBotArena.FIREUP;
    private int frameCount = 0;

    
    
   

    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        int dodgeMove = dodgeBullets(me, bullets);
        if(dodgeMove != 0){
            return dodgeMove;
        }
        
        int move = BattleBotArena.STAY;
        if (shotOK) {
            move = focusShooting(me, liveBots, deadBots, bullets);
        }
        else {
            move = movingAway(me, liveBots, deadBots, bullets);
        }
        
    
        frameCount++;
        if(frameCount > 90){
            int spinning = spinning(lastSpinMove);
            frameCount = 0;
            return spinning;
        }

        return move;
         
    }

     /*
     * Method for the bot to move away from the closest bots.
     * @author Ali Jaffar
     * @param me is the current bot
     * @param liveBots is an array of all the live bots
     * @return the direction the bot should move
     */
    public int movingAway (BotInfo me, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        double xPos = me.getX();
        double yPos = me.getY();

        BotInfo closeBot = findClosestBot(me, liveBots);
        closeBotX = closeBot.getX();
        closeBotY = closeBot.getY();
        count++;

        double botDistance = calcDistance(xPos, yPos, closeBotX, closeBotY);

        if(botDistance < 100 && botDistance > 0){
            if(closeBotX < xPos){                   
                return BattleBotArena.RIGHT;
            }
            else if(closeBotX > xPos){              
                return BattleBotArena.LEFT;
            }
            if(closeBotY < yPos){                   
                return BattleBotArena.DOWN;
            }
            if(closeBotY > yPos){                   
                return BattleBotArena.UP;
            }
        }
        return BattleBotArena.STAY;
    }

    /*
     * Method for the bot to shoot at the closest bot.
     * @author Ali Jaffar
     * @param me is the current bot
     * @param liveBots is an array of all the live bots
     * @return the direction the bot should shoot
     */
    public int focusShooting(BotInfo me, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets){
        double xPos = me.getX();
        double yPos = me.getY();

        
        BotInfo closeBot = findClosestBot(me, liveBots);
        closeBotX = closeBot.getX();
        closeBotY = closeBot.getY();
        count++;

        if (((xPos + 70) > closeBotX) && ((xPos + 70) < (closeBotX + 80))) {
            if ((closeBotY > yPos) && (me.getLastMove()!=BattleBotArena.FIREDOWN)&& (count%5 == 0)) {
               
                return BattleBotArena.FIREDOWN;
                
            }   
            else if((closeBotY < yPos) && (me.getLastMove()!=BattleBotArena.FIREUP) && (count%5 == 0)){
                return BattleBotArena.FIREUP;
            }
        
        }
        else if (((yPos + 70) > closeBotY && ((yPos + 70) < (closeBotY + 80)))) {
            if ((closeBotX > xPos) && (me.getLastMove()!=BattleBotArena.FIRERIGHT) && (count%5 == 0)){
                return BattleBotArena.FIRERIGHT;
            }
            else if ((closeBotX < xPos) && (me.getLastMove()!=BattleBotArena.FIRELEFT) && (count%5 == 0)) {
                return BattleBotArena.FIRELEFT;
            }
            
        }
        return BattleBotArena.STAY;
    }
    
    

    
    /**
     * Method for the bot to dodge bullets 100 pixels away.
     * @author Michelle Vuong
     * @param me The bot info of our bot. 
     * @param bullets The array of bullets in the arena.
     * @return The move required to dodge bullets. Returns 0 if there is no bullets to dodge.
     */
    public int dodgeBullets(BotInfo me, Bullet[] bullets){
		for(int i = 0; i<bullets.length; i++){
	
			if(me.getX() + 100 > bullets[i].getX() && bullets[i].getX() > me.getX() && checkRadiusY(me, bullets[i]) && checkBulletSpeed(bullets[i].getXSpeed()) ){    

                return BattleBotArena.UP;
			
			}else if(me.getX() - 100 < bullets[i].getX() && bullets[i].getX() < me.getX() && checkRadiusY(me, bullets[i]) && checkBulletSpeed(bullets[i].getXSpeed()) ){
				
				return BattleBotArena.DOWN;
	
			}else if(me.getY() + 100 > bullets[i].getY() && bullets[i].getY() > me.getY() &&checkRadiusX(me, bullets[i]) && checkBulletSpeed(bullets[i].getYSpeed())){
				
				return BattleBotArena.LEFT;

			}else if(me.getY() - 100 < bullets[i].getY() && bullets[i].getY() < me.getY() && checkRadiusX(me, bullets[i]) && checkBulletSpeed(bullets[i].getYSpeed())){

				return BattleBotArena.RIGHT;
			}
			
		}
		return 0;
	
       }
    
    
    /**
     * Method for the bot to shoot in all directions. 
     * @author Michelle Vuong
     * @return Move required for Scrap to shoot bullets in all directions.
     */
    public int spinning(int lastSpin){
        switch(lastSpin){
            case BattleBotArena.FIREUP:
                lastSpinMove = BattleBotArena.FIRELEFT;
                return BattleBotArena.FIRELEFT;

            case BattleBotArena.FIRELEFT:
                lastSpinMove = BattleBotArena.FIREDOWN;
                return BattleBotArena.FIREDOWN;

            case BattleBotArena.FIREDOWN:
                lastSpinMove = BattleBotArena.FIRERIGHT;
                return BattleBotArena.FIRERIGHT;
            
            case BattleBotArena.FIRERIGHT:
                lastSpinMove = BattleBotArena.FIREUP;
                return BattleBotArena.FIREUP;
            default:
                return 0;
        }
    }


    /**
     * Helper method to check if a bullet is coming into the bot radius vertically.
     * @author Michelle Vuong
     * @param me The bot info of our bot.
     * @param bullet A single bullet in the arena.
     * @return The Y radius that the bot needs to detect in order to know its own hitbox.
     */
    public boolean checkRadiusY(BotInfo me, Bullet bullet){
        if(bullet.getY() < me.getY() + Bot.RADIUS && bullet.getY() > me.getY() - Bot.RADIUS){
            return true;
        }
        return false;
    }

    /**
     * Helper method to check if a bullet is coming into the bot radius horizontally.
     * @author Michelle Vuong
     * @param me The bot info of our bot.
     * @param bullet A single bullet in the arena.
     * @return Returns true if there is a bullet that intersect with the bot horizontally.
     */
    public boolean checkRadiusX(BotInfo me, Bullet bullet){
        if(bullet.getX() < me.getX() + Bot.RADIUS && bullet.getX() > me.getX() - Bot.RADIUS){
            return true;
        }
        return false;
    }

    /**
     * Helper method to check if a bullet is approaching the bot.
     * @author Michelle Vuong
     * @param speed Takes in speed
     * @return Returns true if there is a bullet that is approaching (speed is not 0).
     */
    public boolean checkBulletSpeed(double speed){
        if(speed !=0){
            return true;
        }
        return false;
    }

    /**
     * Helper method to set the frame. Used for testing.
     * @author Michelle Vuong
     * @param newFrame Takes in a new frame number.
     */

    public void setFrame(int newFrame){
        frameCount = newFrame;
    }

    
    /*
     * Helper method to calculate the closest bot to the current bot.
     * @param me is the current bot
     * @param liveBots is an array of all the live bots
     * @return the closest bot to the current bot
     * @author Ali Jaffar
     */
    private BotInfo findClosestBot(BotInfo me, BotInfo[] liveBots) {
        // TODO Auto-generated method stub
        double xPos = me.getX();
        double yPos = me.getY();
        double closeBotX2 = 0;
        double closeBotY2 = 0;
        double closeBotDistance = 0;
        double botDistance = 0;
        BotInfo closeBot = null;
        for (int i = 0; i < liveBots.length; i++) {
            closeBotX2 = liveBots[i].getX();
            closeBotY2 = liveBots[i].getY();
            botDistance = calcDistance(xPos, yPos, closeBotX2, closeBotY2);
            if (botDistance < closeBotDistance || closeBot == null) {
                closeBotDistance = botDistance;
                closeBot = liveBots[i];
            }
        }
        return closeBot;

    }

    /*
     * Helper method to calculate the distance between two points.
     * @param xPos is the x position of the current bot
     * @param yPos is the y position of the current bot
     * @param closeBotX2 is the x position of the closest bot
     * @param closeBotY2 is the y position of the closest bot
     * @return the distance between the two points
     * @author Ali Jaffar
     */
    private double calcDistance(double xPos, double yPos, double closeBotX2, double closeBotY2) {
        // TODO Auto-generated method stub
        double xDist = xPos - closeBotX2;
        double yDist = yPos - closeBotY2;
        double distance = Math.sqrt((xDist * xDist) + (yDist * yDist));
        return distance;
    }


    public String[] imageNames(){
        String[] paths = {"scrap.png"};
        return paths;
    }

    public void loadedImages(Image[] images)
	{
		if (images != null && images.length > 0)
			image = images[0];
    }

    @Override
    public void draw(Graphics g, int x, int y) {

        if (image != null){
            g.drawImage(image, x,y,Bot.RADIUS*2, Bot.RADIUS*2, null);
        }else{
            g.setColor(Color.gray);
            g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
        }

    }


    @Override
    public String getName() {
        return "Scrap";
    }

    @Override
    public String getTeamName() {
        // TDO Auto-generated method stub
        return null;
    }

    @Override
    public String outgoingMessage() {
        // TOD Auto-generated method stub
        return null;
    }

    @Override
    public void incomingMessage(int botNum, String msg) {
        // TDO Auto-generated method stub
        
    }

    @Override
    public void newRound() {
        // TODO Auto-generated method stub
        
    }

}


    