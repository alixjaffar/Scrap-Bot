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

    public static void main(String[] args){}

    @Override
    public void newRound() {
        // TDO Auto-generated method stub
        
    }

    
    @Override
    public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets) {
        
        int move = BattleBotArena.STAY;
        if (shotOK) {
            move = focusShooting(me, liveBots, deadBots, bullets);
        }
        else {
            move = movingAway(me, liveBots, deadBots, bullets);
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


    @Override
    public void draw(Graphics g, int x, int y) {
        // TOO Auto-generated method stub
        
    }

    @Override
    public String getName() {
        // TOO Auto-generated method stub
        return null;
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
    public String[] imageNames() {
        // TOO Auto-generated method stub
        return null;
    }

    @Override
    public void loadedImages(Image[] images) {
        // TOO Auto-generated method stub
        
    }
}


    