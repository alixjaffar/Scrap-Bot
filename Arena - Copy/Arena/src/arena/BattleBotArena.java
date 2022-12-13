package arena;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import bots.*;

/**
 * <b>Introduction</b>
 * <br><br>
 * This class implements a multi-agent competitive game application. Players contribute a
 * single Java class that defines a "Bot", and these Bots battle each other in a
 * multiple round tournament under the control of a BattleBotsArena object. For instructions
 * on how to create a Bot, see the documentation in
 * the class named <i>Bot</i>. For instructions on how to add Bots to the arena,
 * see the documentation for the <i>fullReset()</i> method in this class.<br><br>
 *
 * <b>The Game Engine</b><br><br>
 *
 * The Arena attempts to run at 30 frames per second, but the actual frame rate may be
 * lower on slower systems. At each frame, the arena does the following for each
 * Bot b that is still alive and not <i>overheated</i>:
 *
 *<ol><li>Gets the team name using <i>b.getTeamName()</i></li>
 *
 * 	  <li>Gets the next move using <i>b.getMove(BotInfo, boolean, BotInfo[], BotInfo[], Bullet[])</i></li>
 *
 * 	  <li>Processes the move only if it is legal (i.e. moves are allowed only if no collisions; bullets and messages are allowed only if the max number of bullets is not exceeded)</li>
 *
 * 	  <li>If the move was SEND_MESSAGE, calls <i>b.outGoingMessage()</i> to get the message from the Bot, then broadcasts it to all live Bots using <i>b.incomingMessage(int, msg)</i></li>
 *
 * 	  <li>Draws each Bot using <i>b.draw(Graphics)</i></li></ol>
 * <br>
 * <b>Timing</b><br><br>
 *
 * The clock records real time in seconds regardless of the actual number of frames
 * that have been processed. The clock will run faster when the play is sped up to x2,
 * x4, or x8 (do this by mousing over the clock and using the scroll wheel). However, depending
 * on the speed of your computer, you may not get 2x, 4x, or 8x as many frames in that time,
 * so you should test your Bots at regular speed whenever possible. <br><br>
 *
 * <b>Bot Movement</b> <br><br>
 *
 * The arena allows each Bot to move vertically or horizontally at a set speed,
 * to fire bullets horizontally or vertically, and to send messages. The speeds
 * of the Bots and Bullets are configurable using static constants. The number
 * of messages that can be sent is capped, as is the number of bullets that each
 * Bot can have on screen at a time (Bot's names show in red when they are
 * unable to fire, and a message is broadcast by the referee when a Bot's
 * messaging is capped).<br><br>
 *
 * <b>Info Passed to the Bots</b><br><br>
 *
 * When asking for a Bot's move, the arena passes an array of Bullets, arrays
 * of info concerning live and dead Bots, a boolean indicating whether the Bot
 * is currently able to shoot, and a single object containing the public
 * information about the Bot itself (see the abstract class Bot for more info
 * on all this). No Bot is ever given any access to any internal variables or
 * data structures of the arena, the Bullets, or other Bots. <br><br>
 *
 * <b>Collisions</b> <br><br>
 *
 * The size of a Bot is defined by the constant <i>Bot.RADIUS</i>. The centre point
 * of each Bot is defined as <i>(x+Bot.Radius, y+Bot.RADIUS)</i>, where x and y are
 * the top left corner of a square in which the bot is inscribed. The width
 * and height of each Bot is <i>Bot.RADIUS * 2</i>. Each Bot has a circular collision
 * mask in a radius of Bot.RADIUS from this centre point. Bullets have a single
 * point collision mask (the pixel at the front of the Bullet) and are created one
 * pixel over from the edge of the Bot that fired them, in the middle of the side
 * from which they were fired (i.e. vertical bullets have an x coordinate of
 * <i>x+Bot.Radius</i> and horizontal bullets have a y coordinate of <i>y+Bot.RADIUS</i>).
 * Two bots have collided if the euclidean distance between their centre points is
 * equal to <i>Bot.RADIUS*2</i> or less).<br><br>
 *
 * <b>Bot CPU Usage</b> <br><br>
 *
 * Processor time is monitored using calls to <i>System.nanoTime()</i> any time
 * a Bot method is used (i.e. for drawing, getting the next move, getting the bot's
 * name and team name, message processing, etc.) This is not perfect, but it does
 * give an approximate estimate of how
 * much CPU each Bot is consuming. A small number of points per round are awarded
 * for low CPU usage (see <i>Scoring</i> below). If the cumulative CPU time for any
 * Bot exceeds 2 seconds (configurable using the static constants), the Bot will
 * <i>overheat</i> and become disabled. At this point, the Bot is replaced by a stock image
 * symbol and there will be no more method calls to that particular Bot for
 * the remainder of the round.<br><br>
 *
 * <b>Buggy Bots</b><br><br>
 *
 * When a Bot throws an exception, the exception is caught by the system and a
 * scoring penalty is applied (defined by static constants in this class).<br><br>
 *
 * <b>Dead Bots</b><br><br>
 *
 * When a Bot is destroyed, it is replaced with a "dead bot" icon and becomes
 * an obstacle on the course. There will be no more method calls to that
 * particular Bot for the remainder of the round.<br><br>
 *
 * <b>Scoring</b><br><br>
 *
 * Bots are awarded points for each kill (whether or not they are alive when their bullet
 * hits), and points for each second they stay alive in the round. The round ends
 * after a set time, or after there are 1 or fewer Bots left. If a Bot is left at the
 * end of the round, it is awarded time points based for the entire length the round
 * would have been if it had continued. Point values are increased each each round.<br><br>
 *
 * In addition to these base points, there are penalties for each exception thrown, and
 * there is a small bonus for low CPU equal to two points minus the number of seconds of
 * CPU time used. This bonus allows ties to be broken (e.g. if two Bots each kill four
 * other Bots and survive to the end of the round, it is usually possible to declare a
 * winner based on CPU time). <br><br>
 *
 * Five Bots are dropped after each round, until six or fewer Bots are left. So if the
 * game starts with 16 Bots(the default) there will be 3 rounds of play. (16 bots, 11 bots,
 * and 6 bots respectively).<br><br>
 *
 * All scoring values and other numbers mentioned in this section can be configured using
 * the static constants of this class. In addition, the game can
 * be played so that the winning Bot is the one with the highest cumulative score at the
 * end of multiple rounds, or it can be the Bot that wins the final round.<br><br>
 *
 * <b>Debugging Features</b><br><br>
 *
 * The arena contains a number of features to aid debugging. The screen that comes up
 * when the Arena is first run is a "Test Mode" screen that allows you to track CPU time
 * and exceptions, view the name and team of each Bot, and check that drawing and
 * movement is within the allowed limits. There is also a DEBUG flag that can be
 * set to TRUE to view statistics while the game is on. The game can be sped up
 * to make it easier to view the outcome of each test match quickly (see the
 * notes under <i>Timing</i> above), and pausing the game provides just over one
 * second of instant replay to watch your Bots' actions in more detail. Finally,
 * there is a "HumanBot" character that you can control with the keyboard to further
 * test your Bots' performance.<br><br>
 *
 *
 * @version 1.0 (March 3, 2011) - Initial Release
 * @version <br>1.1 (March 10, 2011) - Added correction factor for system.nanoTime(), fixed bug in messaging (was cutting off last character of every message)
 * @version <br>1.2 (March 24, 2011) - Added ready flag used in paint and paintBuffer to avoid exceptions from a race condition on startup
 * @version <br>1.3 (March 28, 2011) - Load starting team names at beginning of match, icons updated (thanks to Mike Stuart for the new dead bot icon)
 * @version <br>1.4 (March 28, 2011) - Improvements in how info is passed to bots: a. Only temp arrays are passed (so bots can't sabotage them); b. A deep copy
 *                                     of the BotInfo array for live bots is passed, so that all Bots get the exact same snapshot of where the Bots are (thanks
 *                                     to Zong Li for helping uncover the latter issue)
 * @version <br>1.5 (March 31, 2011) - Moved bullet processing out of the Bot loop -- now bots are moved first, then bullets are moved (thanks again to Zong Li
 * 									   for pointing out this issue)
 * @version <br>1.6 (May 30, 2011)   - Shuts off sound on stop/destroy now
 * @version <br>2.0 (August 9, 2011) - Converted to an application that can be JAR'ed -- the mouse wheel was not working well when embedded in a web page
 * @version <br>2.1 (November 30, 2011) - Fixed audio bug
 * @author Sam Scott
 *
 */
public class BattleBotArena extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener, ActionListener, Runnable {

	/**
	 * Set to TRUE for debugging output
	 */
	public static final boolean DEBUG = false;

	//***********************************************
	// MAIN SET OF CONSTANTS AVAILABLE TO THE BOTS...
	//***********************************************
	/**
	 * For bot to request a move up
	 */
	public static final int UP = 1;
	/**
	 * For bot to request a move down
	 */
	public static final int DOWN = 2;
	/**
	 * For bot to request a move left
	 */
	public static final int LEFT = 3;
	/**
	 * For bot to request a move right
	 */
	public static final int RIGHT = 4;
	/**
	 * For bot to request a bullet fired up
	 */
	public static final int FIREUP = 5;
	/**
	 * For bot to request a bullet fired down
	 */
	public static final int FIREDOWN = 6;
	/**
	 * For bot to request a bullet fired left
	 */
	public static final int FIRELEFT = 7;
	/**
	 * For bot to request a bullet fired right
	 */
	public static final int FIRERIGHT = 8;
	/**
	 * For bot to request a null move
	 */
	public static final int STAY = 9;
	/**
	 * For bot to request a message send. If allowed, the arena will respond with a call to the bot's outGoingMessage() method.
	 */
	public static final int SEND_MESSAGE = 10;
	/**
	 * Right edge of the screen
	 */
	public static final int RIGHT_EDGE = 700; // also arena panel width
	/**
	 * Bottom edge of the screen
	 */
	public static final int BOTTOM_EDGE = 500; // arena panel height is this constant + TEXT_BUFFER
	/**
	 * Left edge of the screen
	 */
	public static final int LEFT_EDGE = 0;
	/**
	 * Top edge of the screen
	 */
	public static final int TOP_EDGE = 10;
	/**
	 * The "bot id" that indicates a system message
	 */
	public static final int SYSTEM_MSG = -1;

	//*****************************************
	// GAME CONFIGURATION - CHANGE WITH CAUTION
	// BOTS ALSO HAVE ACCESS TO THESE CONSTANTS
	//*****************************************
	/**
	 * points per kill (multiplied by round number)
	 */
	public static final int 	KILL_SCORE = 5;
	/**
	 * survival points (multiplied by round number)
	 */
	public static final double 	POINTS_PER_SECOND = 0.1;
	/**
	 * points per unused second of processor time (mostly for breaking ties)
	 */
	public static final int 	EFFICIENCY_BONUS = 1;
	/**
	 * points off per exception caught
	 */
	public static final int 	ERROR_PENALTY = 5;
	/**
	 * true = scores between rounds are cumulative
	 * false = highest scoring Bot in last round is declared the winner
	 */
	public static final boolean CUMULATIVE_SCORING = true;
	/**
	 * Number of bots to drop out per round
	 */
	public static final int	 	ELIMINATIONS_PER_ROUND = 5;
	/**
	 * Round time, in seconds
	 */
	public static final int 	TIME_LIMIT = 90;
	/**
	 * TIME_LIMIT / SECS_PER_MSG = Number of messages allowed per round
	 */
	public static final double 	SECS_PER_MSG = 5;
	/**
	 * CPU limit per Bot per round
	 */
	public static final double 	PROCESSOR_LIMIT = 2.0;
	/**
	 * Total number of Bots in round 1 (if you have fewer than this, the rest of the spots
	 * in the array will be filled with Drones, RandBots, and Sentries).
	 */
	public static final int 	NUM_BOTS = 16;
	/**
	 * Number of bullets on screen at once for each bot
	 */
	public static final int 	NUM_BULLETS = 4;
	/**
	 * Bot speed in pixels/frame
	 */
	public static final double 	BOT_SPEED = 1.5;
	/**
	 * Bullet speed in pixels/frame
	 */
	public static final double 	BULLET_SPEED = 4;
	/**
	 * Maximum message length
	 */
	public static final int MAX_MESSAGE_LENGTH = 200;

	//**************************************
	// OTHER ARENA CONSTANTS -- DON'T CHANGE
	//**************************************
	/**
	 * Size of message area at bottom of screen.
	 */
	private static final int TEXT_BUFFER = 100;
	/**
	 * How fast the clock flashes when game paused
	 */
	private final int PAUSE_FLASH_TIME = 8;
	/**
	 * How fast the red circles flash in test mode
	 */
	private final int FLASH_TIME = 10;
	/**
	 * State constant to signal we are between rounds
	 */
	private final int WAIT_TO_START = 1;
	/**
	 * State constant to signal that the game is on
	 */
	private final int GAME_ON = 2;
	/**
	 * State constant to signal that we are between rounds
	 */
	private final int GAME_OVER = 3;
	/**
	 * State constant to signal that the game is paused
	 */
	private final int GAME_PAUSED = 4;
	/**
	 * State constant to signal game over and winner declared
	 */
	private final int WINNER = 5;
	/**
	 * State constant to signal we are in test mode (starts in this mode)
	 */
	private final int TEST_MODE = 6;
	/**
	 * Size of the bot names
	 */
	private final int NAME_FONT = 10;
	/**
	 * Size of the stats font (stats displayed at end of each round)
	 */
	private final int STATS_FONT = 15;
	/**
	 * Number of frames in the buffer for instant replay (limited by heap size)
	 */
	private final int NUM_FRAMES = 40;
	/**
	 * Ticks per frame in replay mode. Higher for a slower replay.
	 */
	private final int REPLAY_SPEED = 2;
	/**
	 * How many frames to hold on the last frame before restarting the instant replay.
	 */
	private final int END_FRAME_COUNT = 15;
	/**
	 * File name for fanfare sound (plays at start of round)
	 */
	private final String fanfareSoundFile = "FightLikeARobot.wav";
	/**
	 * File name for shot sound
	 */
	private final String shotSoundFile = "Shot.wav";
	/**
	 * File name for robot death sound
	 */
	private final String deathSoundFile = "Death.wav";
	/**
	 * File name for drone sound during game
	 */
	private final String droneSoundFile = "def_star2.wav";
	/**
	 * File name for opening sound (plays during opening screen)
	 */
	private final String openSoundFile = "crystalcastles.wav";
	/**
	 * File name for stop sound (plays when pausing game)
	 */
	private final String stopSoundFile = "qix.wav";
	/**
	 * File name for game over sound
	 */
	private final String gameOverSoundFile = "GameOver.wav";
	/**
	 * File name for overheat sound
	 */
	private final String overheatSoundFile = "dp_frogger_squash.wav";

	//**************************************
	// OTHER ARENA VARIABLES -- DON'T CHANGE
	//**************************************
	/**
	 * If set to true, the arena will display Bot names during the game.
	 */
	private boolean showNames = true;
	/**
	 * If set to true, the arena will display Bot scores during the game.
	 */
	private boolean showScores = false;
	/**
	 * If set to true, the arena will display Bot team names during the game.
	 */
	private boolean showTeams = false;
	/**
	 * Toggles sound effects on and off
	 */
	private boolean soundOn = true;
	/**
	 * The current speed multiplier
	 */
	private int speed = 1;
	/**
	 * Controls the flashing if the game is paused
	 */
	private int pauseCount = 0;
	/**
	 * The winner of the game
	 */
	private int winnerID = -1;
	/**
	 * Countdown to the start of the game ("Fight like a robot!")
	 */
	private int countDown = -1;
	/**
	 * Counter for flashing the clock in pause mode
	 */
	private int flashCounter = 0;
	/**
	 * The bot we are dragging (test mode)
	 */
	private int gotBot = -1;
	/**
	 * For dragging a bot (test mode)
	 */
	private int forceBotX, forceBotY, mouseInitialX, mouseInitialY;
	/**
	 * The main state variable - controls what phase of the game we are at (see the State constants)
	 */
	private int state = WAIT_TO_START; // state variable
	/**
	 * Used when going into test mode - false while bots are being set up, constructors called, etc.
	 */
	private boolean ready = false;
	/**
	 * The current round
	 */
	private int round = 0;
	/**
	 * which message is displayed first - for scrolling messages
	 */
	private int firstMessage = 0;
	/**
	 * Double-buffering
	 */
	private Image buffer;
	/**
	 * Dead Bot image
	 */
	private Image deadBot;
	/**
	 * Overheated Bot image
	 */
	private Image overheated;
	/**
	 * For timing the game length
	 */
	private long startTime;
	/**
	 * For continuing correct timing after a pause
	 */
	private long pauseTime;
	/**
	 * On some machines, System.nanoTime() returns incorrect results. For example, on one machine
	 * System.currentTimeMillis() shows 10 seconds elapsed while System.nanoTime() consistently shows
	 * 4.5 seconds elapsed for the same time period. The more reliable millisecond timing is used
	 * for the game clock, however for timing CPU usage of the Bots, we need a higher grain than 1ms.
	 * So System.nanoTime() is used, but a correction factor is computed in a thread spawned at
	 * startup time, and this becomes a multiplier for the number of ns that System.nanoTime() reports
	 * has elapsed.
	 */
	private double nanoTimeCorrection = 1;
	/**
	 * Total time played
	 */
	private double timePlayed = 0;
	/**
	 * Object for formatting decimals
	 */
	private DecimalFormat df = new DecimalFormat("0.0"), df2 = new DecimalFormat("0.000");
	/**
	 * The main game engine timer
	 */
	private Timer gameTimer;
	/**
	 * Main array of Bot objects. Note that bots, botsInfo, and bullets are related arrays - bullets[i]
	 * gives the array of bullets owned by bot[i], and botsInfo[i] gives the public info
	 * for bot[i].
	 */
	private Bot[] bots = new Bot[NUM_BOTS];
	/**
	 * Array of public info regarding the Bots - this is how information is passed to
	 * the Bots in getMove(). Done this way so the Bots don't have access to each others'
	 * internal states. Note that bots, botsInfo, and bullets are related arrays - bullets[i]
	 * gives the array of bullets owned by bot[i], and botsInfo[i] gives the public info
	 * for bot[i].
	 */
	private BotInfo[] botsInfo = new BotInfo[NUM_BOTS];
	/**
	 * The bullets. Note that bots, botsInfo, and bullets are related arrays - bullets[i]
	 * gives the array of bullets owned by bot[i], and botsInfo[i] gives the public info
	 * for bot[i].
	 */
	private Bullet[][] bullets = new Bullet[NUM_BOTS][NUM_BULLETS];
	/**
	 * Number of bots remaining in the round.
	 */
	private int botsLeft = NUM_BOTS;
	/**
	 * Message buffer
	 */
	private LinkedList<String> messages = new LinkedList<String>();
	/**
	 * The images to use in instant replay. This is a circular buffer.
	 */
	private Image[] replayImages = new Image[NUM_FRAMES];
	/**
	 * The latest frame. The head pointer of the circular buffer called replayImages.
	 */
	private int replayEndFrame = 0;
	/**
	 * In instant replay mode, this is the frame we are currently presenting.
	 */
	private int replayCurrentFrame = 0;
	/**
	 * Counter for holding on the last frame before resetting the instant replay.
	 */
	private int endFrameCounter = 0;
	/**
	 * Counter for deciding when to advance the frame during an instant replay.
	 */
	private int replayCounter = 0;
	/**
	 * This is a buffer that holds the images the Bots are requesting to load.
	 * Since bots don't have access to the arena as an image observer, the images
	 * in this list are painted off screen until g.drawImage() returns true. This
	 * ensures that all images get loaded ASAP whether the Bots are using them
	 * or not, and that callbacks happen and trigger a repaint when images are
	 * loaded.
	 */
	private LinkedList<Image> imagesToLoad = new LinkedList<Image>();
	/**
	 * Holds an audioclip for arena sound.
	 */
	AudioClip death, fanfare, shot, drone, open, stop, gameOver, overheat;

	//***************************************
	// METHODS YOU NEED TO CHANGE
	//***************************************
	/**
	 * This method is called at the start of each new game, before the test mode
	 * screen comes up. It creates all the Bots that will participate in the game,
	 * and resets a few game constants.
	 *
	 * NOTE: This is where you add your own bots. See the instructions in the
	 * method below...
	 */
	private void fullReset()
	{
		ready = false; 				// Signals to the paint methods that the Bots are not set up yet
		if (soundOn) open.play();	// Play the fanfare
		state = TEST_MODE;			// We start in test mode
		gameTimer.start();			// start the timer thread if necessary
		bots = new Bot[NUM_BOTS];	// the bots
		round = 0;					// pre-game is round 0

		// *** HUMAN TEST BOT CREATION
		// *** Comment the next two lines out if you don't want to use the
		// *** HumanBot (under user control)
		bots[0] = new HumanBot();
		addKeyListener((HumanBot)bots[0]);
		// ******************************

		// *** INSERT PLAYER BOTS HERE. Use any array numbers you like
		// *** as the bots will be shuffled again later.
		// *** Any empty spots will be filled with standard arena bots.
		//bots[0] = new CloseBot();  // Chris
		//bots[1] = new Falcon();   // David
		//bots[2] = new GotPho(); // Matthew
		//bots[3] = new Harmless(); // Zong
		//bots[4] = new MetaBee();  // Owen
		//bots[5] = new Pitfall();  // Cary
		//bots[6] = new TT21();	  // Tong
		//bots[7] = new CrowBot();  // Sam
		//bots[8] = new CrazyHand();  // Oliver
		//bots[9] = new JuneBot();  // June
		//bots[10] = new Nigel1_1();  // Nigel
		bots[1] = new ScrapBot();


		// *******************************

		// Remaining slots filled with Drones, RandBots, and sentryBots.
		int c = 1;
		for (int i=0; i<NUM_BOTS; i++)
		{
			if (bots[i] == null)
			{
				if (c==1)
					bots[i] = new Drone();
				else if (c==2)
					bots[i] = new RandBot();
				else
				{
					bots[i] = new SentryBot();
					c=0;
				}
				c++;
			}
		}

		reset(); // calls the between-round reset method
	}

	//***************************************
	// METHODS YOU SHOULD *NOT* CHANGE
	//***************************************

	/**
	 * Main method to create and display the arena
	 * @param args unused
	 */
	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		BattleBotArena panel = new BattleBotArena();
		frame.setContentPane(panel);
		frame.pack();
		frame.setTitle("BattleBots");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		panel.init();
		panel.requestFocusInWindow();
	}

	/**
	 * One-time setup for images, sounds, listeners, buffers, and game timer.
	 **/
	public BattleBotArena ()
	{
		// start the calibration timer (see run method below for more info)
		(new Thread(this)).start();
		// create the game thread
		gameTimer = new Timer(1000/30/speed,this);
		// sounds
		URL location = getClass().getClassLoader().getResource("sounds/"+fanfareSoundFile);
		fanfare = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+shotSoundFile);
		shot = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+deathSoundFile);
		death = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+droneSoundFile);
		drone = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+openSoundFile);
		open = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+stopSoundFile);
		stop = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+gameOverSoundFile);
		gameOver = Applet.newAudioClip(location);
		location = getClass().getClassLoader().getResource("sounds/"+overheatSoundFile);
		overheat = Applet.newAudioClip(location);
		// images
		deadBot = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/dead.png"));
		overheated = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/overheated.png"));
		// Listeners for mouse input
		addMouseListener (this);
		addMouseMotionListener (this);
		addMouseWheelListener (this);
		// Set size of panel and make it focusable
		setPreferredSize(new Dimension(700, 600));
		setFocusable(true);
	}

	private void init()
	{
		// Paint buffer and instant replay array
		for (int i = 0; i<NUM_FRAMES; i++)
			replayImages[i] = createImage(RIGHT_EDGE, BOTTOM_EDGE);
		buffer = createImage(RIGHT_EDGE, BOTTOM_EDGE+TEXT_BUFFER);
		// Set up the bots and the game
		fullReset();
	}
	/**
	 * Reset for a new round. Called between rounds, and also after a full reset.
	 */
	private void reset()
	{
		timePlayed = 0;						// reset the clock
		round ++;							// advance the round
		botsLeft = NUM_BOTS;				// put all the bots back in the game
		messages = new LinkedList<String>();// clear the messag buffer

		// shuffle the bots
		for (int i=0; i<NUM_BOTS*10; i++)
		{
			int b1 = (int)(Math.random()*NUM_BOTS);
			int b2 = (int)(Math.random()*NUM_BOTS);
			Bot temp = bots[b1];
			bots[b1] = bots[b2];
			bots[b2] = temp;
			BotInfo temp2 = botsInfo[b1];
			botsInfo[b1] = botsInfo[b2];
			botsInfo[b2] = temp2;
		}

		// Clear the array of public Bot info. (This is the info given to the Bots when making their moves.)
		BotInfo[] newBotsInfo = new BotInfo[NUM_BOTS];

		// Assign starting positions & numbers for bots
		// and also create new array of botsInfo
		if (state == TEST_MODE) // we are restarting. everything is reset
		{
			int xScale = (RIGHT_EDGE-Bot.RADIUS*4)/Math.max(NUM_BOTS-1,1); // this spaces them out so they don't rez on top of each other
			int yScale = (BOTTOM_EDGE-Bot.RADIUS*4)/5;
			int[][] grid = new int[NUM_BOTS][5];
			for (int i = 0; i < NUM_BOTS; i++)
			{
				bots[i].assignNumber(i);  // assign new numbers
				int x = (int)(Math.random()*NUM_BOTS);
				int y = (int)(Math.random()*5);
				newBotsInfo[i] = new BotInfo(x*xScale + Bot.RADIUS, y*yScale + Bot.RADIUS, i, bots[i].getName()); // create new BotInfo object to keep track of bot's stats
				newBotsInfo[i].setTeamName(bots[i].getTeamName()); // get start of game team names
				if (grid[x][y] == 1)
					i--;
				else
					grid[x][y] = 1;
			}
		}
		else
		{
			int offsetx = (RIGHT_EDGE-Bot.RADIUS*4)/Math.max(NUM_BOTS-1,1);
			int offsety = (BOTTOM_EDGE-Bot.RADIUS*4)/Math.max(NUM_BOTS-1,1);
			int x = Bot.RADIUS;
			int y = Bot.RADIUS;
			for (int i = 0; i < NUM_BOTS; i++)
			{
				bots[i].assignNumber(i);  // assign new numbers
				newBotsInfo[i] = new BotInfo(x, y, i, botsInfo[i].getName());
				newBotsInfo[i].setTeamName(bots[i].getTeamName()); // get start of game team names
				if (botsInfo[i] != null && CUMULATIVE_SCORING && round > 1)
					newBotsInfo[i].setCumulativeScore(botsInfo[i].getCumulativeScore()+botsInfo[i].getScore());
				if (botsInfo[i] != null && (botsInfo[i].isOut() || botsInfo[i].isOutNextRound()))
				{
					newBotsInfo[i].knockedOut();
					botsLeft--;
				}
				x += offsetx;
				y += offsety;
				//System.out.println(bots[i].getName()+ " "+newBotsInfo[i].getName()+" "+newBotsInfo[i].isOut());
			}
		}
		botsInfo = newBotsInfo;

		// load the images & call the newRound message for the bots
		for (int i = 0; i < NUM_BOTS; i++)
		{
			loadImages (i);
			// BOT METHOD CALL - timed and exceptions caught
			long startThink = System.nanoTime();
			try {
				bots[i].newRound();
			}
			catch (Exception e)
			{
				botsInfo[i].exceptionThrown(e);
			}
			botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
			// ***********************
		}

		bullets = new Bullet[NUM_BOTS][NUM_BULLETS]; 	// init the bullets array

		ready = true; // tell the paint method we're good to go

		// In test mode, spam the message area with these instructions
		if (state == TEST_MODE)
		{
			sendMessage(-1,"Battle Bots, by Sam Scott (sam.scott@tdsb.on.ca)");
			sendMessage(-1,"------------------------------------------------");
			sendMessage(-1,"Developed in 2011 as a programming challenge for my current and");
			sendMessage(-1,"former grade 12 (ICS4U) students.");
			sendMessage(-1,"    ");
			sendMessage(-1,"Each bot is in its own class, and is under its own control. Bots");
			sendMessage(-1,"declare their names once at the beginning, and can declare and");
			sendMessage(-1,"change their team allegiances throughout the game.");
			sendMessage(-1,"    ");
			sendMessage(-1,"Bots choose their actions 30 times per second. If the action is");
			sendMessage(-1,"legal, the arena allows it. The arena processes all collisions and");
			sendMessage(-1,"handles all the scoring. Bots do not have direct access to the code ");
			sendMessage(-1,"or instance variables of the other bots, the bullets, or the arena.");
			sendMessage(-1,"Bots can send broadcast messages to one another, and periodically");
			sendMessage(-1,"receive messages from the referee. All messaging appears in this");
			sendMessage(-1,"window.");
			sendMessage(-1,"    ");
			sendMessage(-1,"All exceptions are caught and counted, with a scoring penalty for");
			sendMessage(-1,"each one. CPU use is monitored and bots will overheat and become");
			sendMessage(-1,"disabled when they go over the limit. Tie-breaking points are");
			sendMessage(-1,"awarded for low CPU use.");
			sendMessage(-1,"    ");
			sendMessage(-1,"Use the menu buttons on the right to control the view and the sound.");
			sendMessage(-1,"When the game is on, click the clock to pause and view the instant ");
			sendMessage(-1,"replay, or mouse over the clock and use the scroll wheel to speed up ");
			sendMessage(-1,"and slow down the game. Use the scroll wheel in this message window");
			sendMessage(-1,"to view old messages.");
			sendMessage(-1,"    ");
			sendMessage(-1,"HAVE FUN!");
			sendMessage(-1,"------------------------------------------------");
			sendMessage(-1,"    ");


			sendMessage(-1,"Hello. I am your referee.");
			sendMessage(-1,"We are currently in test mode.");
			sendMessage(-1,"Draw test - Each bot should be in a red circle.");
			sendMessage(-1,"Move test - Bots can be dragged with the mouse.");
			sendMessage(-1,"Code test - Numbers show exceptions and processor time.");
			sendMessage(-1,"Scroll up to see more info and credits.");
		}
	}

	/**
	 * Loads images for the bots
	 * @param botNum
	 */
	private void loadImages(int botNum)
	{
		String[] imagePaths = null; // file names
		Image[] images = null; 		// images

		// 1. get the image names
		// BOT METHOD CALL - timed and exceptions caught
		long startThink = System.nanoTime();
		try {
			imagePaths = bots[botNum].imageNames();
		} catch (Exception e) {
			botsInfo[botNum].exceptionThrown(e);
		}
		botsInfo[botNum].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
		// ***********************

		// 2. load the images if there are any to load
		if (imagePaths != null)
		{
			images = new Image[imagePaths.length];
			for (int i=0; i<imagePaths.length; i++)
			{
				try {
					images[i] = Toolkit.getDefaultToolkit ().getImage (getClass().getClassLoader().getResource("images/"+imagePaths[i]));
					imagesToLoad.add(images[i]);
				} catch (Exception e) {
					botsInfo[botNum].exceptionThrown(e);
				}
			}
			// 3. pass the messages to the Bot
			// BOT METHOD CALL - timed and exceptions caught
			startThink = System.nanoTime();
			try {
				bots[botNum].loadedImages(images);
			} catch (Exception e) {
				botsInfo[botNum].exceptionThrown(e);
			}
			botsInfo[botNum].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
			// ***********************
		}
	}

	/**
	 * This method is for the thread computes the correction factor for System.nanoTime(). See
	 * the documentation of the field "nanoTimeCorrection" for more information. The thread is
	 * spawned by the init() method, and should be complete and no longer running after about 20s.
	 */
	public void run()
	{
		//pause for setup
		try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
		//repeatedly test ms and ns timers for 1 second intervals and compute the
		//correction for System.nanoTime() assuming currentTimeMillis() is accurate
		double totalms = 0, totalns = 0;
		for (int i=0; i<10; i++)
		{
			double start = System.currentTimeMillis();
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			totalms += (System.currentTimeMillis()-start)/1000.0;
			//System.out.println("millisecond timer ... "+totalms);
			start = System.nanoTime();
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			totalns += (System.nanoTime()-start)/1000000000.0;
			//System.out.println("nanosecond timer ... "+totalns);
			nanoTimeCorrection = totalms/totalns;
			if (DEBUG)
				System.out.println("nanoTimeCorrection after "+(i+1)+" seconds = "+nanoTimeCorrection);
		}
		requestFocus();
	}

	/**
	 * The main game method - called by the timer. Handles all the
	 * mechanics of the game, the replay mode, and the test mode screen.
	 */
	public void actionPerformed(ActionEvent ace)
	{
		// **** are we moving bots around?
		if (state == GAME_ON && countDown <= 0 || state == TEST_MODE && ready)
		{
			if (state != TEST_MODE) // advance the timer or...
			{
				long timeNow = System.currentTimeMillis();
				timePlayed += (timeNow - startTime)/1000.0*speed;
				startTime = timeNow;
			}
			else // ...flash the red rings around the bots in test mode
			{
				flashCounter--;
				if (flashCounter < 0)
					flashCounter = FLASH_TIME;
			}
			// **** game over?
			if (state != TEST_MODE && (timePlayed >= TIME_LIMIT || botsLeft <= 1))
			{
				state = GAME_OVER;
				resetGameSpeed();
				endFrameCounter = END_FRAME_COUNT; // start the instant replay
				replayCurrentFrame = replayEndFrame;
				drone.stop(); // stop the sound
				if (soundOn)
					gameOver.play();
				if (botsLeft == 1) // if there is a bot left, update its score
					for (int i=0; i<NUM_BOTS; i++)
						if (botsInfo[i].isDead() == false && botsInfo[i].isOut() == false)
						{
							botsInfo[i].setScore(currentScore(i, true));
							break;
						}
				// knock out up to ELIMINATIONS_PER_ROUND bots
				int knockedOut = 0;
				int totalOut = 0;
				BotInfo[] sortedBots = sortedBotInfoArray(false);
				for (int i=0; i<NUM_BOTS && knockedOut<ELIMINATIONS_PER_ROUND; i++)
				{
					if (!sortedBots[i].isOut())
					{
						sortedBots[i].outNextRound();
						knockedOut++;
					}
					totalOut++;
				}
				// find the winner
				sortedBots = sortedBotInfoArray(true);
				winnerID = sortedBots[0].getBotNumber();
				if (totalOut >= NUM_BOTS-1) // is this the last round?
				{
					sendMessage(-1,"Final round complete. "+sortedBots[0].getName()+" is the winner.");
					state = WINNER;
				}
				else
					if (CUMULATIVE_SCORING) // different message depending on scoring type
						sendMessage(-1,"Round "+round+" complete. "+sortedBots[0].getName()+" is leading.");
					else
						sendMessage(-1,"Round "+round+" complete. "+sortedBots[0].getName()+" is the winner.");
			}
			else //**** GAME IS ON
			{
				// A. increment the circular replay buffer
				if (++replayEndFrame == NUM_FRAMES)
					replayEndFrame = 0;

				// B. create copies of all the bullet and Bot info to pass to each
				// Bot when getting their moves
				LinkedList<Bullet> bulletList = new LinkedList<Bullet>();
				BotInfo[] liveBots = new BotInfo[botsLeft];
				int nextLiveBotIndex = 0;
				BotInfo[] deadBots = new BotInfo[NUM_BOTS-(round-1)*ELIMINATIONS_PER_ROUND-botsLeft];
				int nextDeadBotIndex = 0;
				for (int j=0; j<NUM_BOTS; j++)
				{
					if (!botsInfo[j].isOut())
						if (!botsInfo[j].isDead())
							liveBots[nextLiveBotIndex++] = botsInfo[j];
						else
							deadBots[nextDeadBotIndex++] = botsInfo[j].copy(); // important to deep copy or else some
																			   // bots will get info about the current move
																			   // for some of the other bots
					int bulletCount =0;
					for (int k=0; k<NUM_BULLETS; k++)
						if (bullets[j][k] != null)
						{
							bulletList.add(bullets[j][k]);
							bulletCount++;
						}
				}
				// C. process moves for each bot
				for (int i = 0; i<NUM_BOTS; i++)
				{
					// only  move bot if it's active
					if (!botsInfo[i].isOverheated() && !botsInfo[i].isDead() && !botsInfo[i].isOut())
					{
						// Update Bot's Score
						botsInfo[i].setScore(currentScore(i, false));
						// Check think time to see if over limit
						if (botsInfo[i].getThinkTime() > PROCESSOR_LIMIT && state != TEST_MODE)
						{
							botsInfo[i].overheated();
							if (soundOn)
								overheat.play();
							sendMessage(SYSTEM_MSG, botsInfo[i].getName()+" overheated - CPU limit exceeded.");
						}
						else //bot still alive! Process move
						{
							// 1. Get bot team name
							// BOT METHOD CALL - timed and exceptions caught
							long startThink = System.nanoTime();
							try {
								botsInfo[i].setTeamName(bots[i].getTeamName());
							}
							catch(Exception e)
							{
								botsInfo[i].exceptionThrown(e);
							}
							botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
							// ***********************

							// 2. set up to get the next move
							// 2a. Can the current bot shoot?
							BotInfo currentBot = botsInfo[i]; 	// current Bot
							boolean shotOK = false;				// can shoot?
							for (int j=0; j<NUM_BULLETS; j++)
							{
								if (bullets[i][j] == null)
									shotOK = true;
							}
							// 2b. The bots have to be passed temp arrays of bullets so they can't
							// mess them up (e.g. by setting array entries to null)
							Bullet[] cleanBulletArray = new Bullet[bulletList.size()];
							int cleanBAIndex = 0;
							Iterator<Bullet> it = bulletList.iterator();
							while (it.hasNext())
								cleanBulletArray[cleanBAIndex++] = it.next();
							// 2c. For the same reason, they must get temp arrays of live and dead bots too.
							//     We also remove the current bot from the list of livebots here.
							BotInfo[] cleanLiveBotsArray = new BotInfo[liveBots.length-1];
							int k = 0;
							for (int j=0; j<liveBots.length; j++)
								if (liveBots[j] != currentBot)
									cleanLiveBotsArray[k++] = liveBots[j];
							BotInfo[] cleanDeadBotsArray = new BotInfo[deadBots.length];
							for (int j=0; j<deadBots.length; j++)
								cleanDeadBotsArray[j] = deadBots[j];

							// 3. now, get the move
							int move = -1;
							// BOT METHOD CALL - timed and exceptions caught
							startThink = System.nanoTime();
							try {
								move = bots[i].getMove(currentBot, shotOK, cleanLiveBotsArray, cleanDeadBotsArray, cleanBulletArray);
							}
							catch(Exception e)
							{
								botsInfo[i].exceptionThrown(e);
							}
							botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
							// ***********************

							botsInfo[i].setLastMove(move);

							// 4. Process the move
							switch(move)
							{
							case UP:
								botsInfo[i].setY(botsInfo[i].getY()-BOT_SPEED);
								break;
							case DOWN:
								botsInfo[i].setY(botsInfo[i].getY()+BOT_SPEED);
								break;
							case LEFT:
								botsInfo[i].setX(botsInfo[i].getX()-BOT_SPEED);
								break;
							case RIGHT:
								botsInfo[i].setX(botsInfo[i].getX()+BOT_SPEED);
								break;
							case FIREUP:
								for (int j=0; j<NUM_BULLETS; j++) // looks for the first unused bullet slot
									if (bullets[i][j] == null)
									{
										bullets[i][j] = new Bullet(botsInfo[i].getX()+Bot.RADIUS, botsInfo[i].getY()-1, 0, -BULLET_SPEED);
										if (state != TEST_MODE)
											if (soundOn)
												shot.play();
										break;
									}
								break;
							case FIREDOWN:
								for (int j=0; j<NUM_BULLETS; j++)// looks for the first unused bullet slot
									if (bullets[i][j] == null)
									{
										bullets[i][j] = new Bullet(botsInfo[i].getX()+Bot.RADIUS, botsInfo[i].getY()+Bot.RADIUS * 2 + 1, 0, BULLET_SPEED);
										if (state != TEST_MODE)
											if (soundOn)
												shot.play();
										break;
									}
								break;
							case FIRELEFT:
								for (int j=0; j<NUM_BULLETS; j++)// looks for the first unused bullet slot
									if (bullets[i][j] == null)
									{
										bullets[i][j] = new Bullet(botsInfo[i].getX()-1, botsInfo[i].getY()+Bot.RADIUS, -BULLET_SPEED, 0);
										if (state != TEST_MODE)
											if (soundOn)
												shot.play();
										break;
									}
								break;
							case FIRERIGHT:
								for (int j=0; j<NUM_BULLETS; j++)// looks for the first unused bullet slot
									if (bullets[i][j] == null)
									{
										bullets[i][j] = new Bullet(botsInfo[i].getX()+Bot.RADIUS * 2 + 1, botsInfo[i].getY()+Bot.RADIUS, BULLET_SPEED, 0);
										if (state != TEST_MODE)
											if (soundOn)
												shot.play();
										break;
									}
								break;
							case SEND_MESSAGE:
								String msg = null;
								// get the message
								// BOT METHOD CALL - timed and exceptions caught
								startThink = System.nanoTime();
								try {
									msg = bots[i].outgoingMessage();
									botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
									// make sure they are not over the limit
									if (botsInfo[i].getNumMessages() < TIME_LIMIT/SECS_PER_MSG && state != TEST_MODE)
										sendMessage(i, msg); // send the message
								}
								catch (Exception e)
								{
									botsInfo[i].exceptionThrown(e);
									botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
								}
								// ***********************
								break;
							}
							// 5. Bot collisions
							if (move == UP || move == DOWN || move == LEFT || move == RIGHT) // if a move was made...
							{
								// 5a. other bots
								for (int j=0; j<NUM_BOTS; j++)
								{
									if (j!=i && !botsInfo[i].isOut()) // don't collide with self or bots that are out
									{
										double d = Math.sqrt(Math.pow(botsInfo[i].getX()-botsInfo[j].getX(),2)+Math.pow(botsInfo[i].getY()-botsInfo[j].getY(),2));
										if (d < Bot.RADIUS*2)
										{
											// reverse the previous move on collision
											if (move == UP)
												botsInfo[i].setY(botsInfo[i].getY()+BOT_SPEED);
											else if (move == DOWN)
												botsInfo[i].setY(botsInfo[i].getY()-BOT_SPEED);
											else if (move == LEFT)
												botsInfo[i].setX(botsInfo[i].getX()+BOT_SPEED);
											else if (move == RIGHT)
												botsInfo[i].setX(botsInfo[i].getX()-BOT_SPEED);
											break;
										}
									}
								}
								// 5b. wall collisions - reset the bot to be inside the boundaries
								if (botsInfo[i].getX() < LEFT_EDGE)
									botsInfo[i].setX(LEFT_EDGE);
								if (botsInfo[i].getX() > RIGHT_EDGE-Bot.RADIUS*2)
									botsInfo[i].setX(RIGHT_EDGE-Bot.RADIUS*2);
								if (botsInfo[i].getY() < TOP_EDGE)
									botsInfo[i].setY(TOP_EDGE);
								if (botsInfo[i].getY() > BOTTOM_EDGE-Bot.RADIUS*2)
									botsInfo[i].setY(BOTTOM_EDGE-Bot.RADIUS*2);
							}
						}
					}
					// 6. in test mode, force a bot move
					if (state == TEST_MODE && gotBot == i)
					{
						botsInfo[i].setX(forceBotX);
						botsInfo[i].setY(forceBotY);
					}
				}
				// D. Process the bullet moves/collisions
				for (int i=0; i<NUM_BOTS; i++)
					for (int k=0; k<NUM_BULLETS; k++)
					{
						if (bullets[i][k] != null)
						{
							bullets[i][k].moveOneStep();
							// 6a. destroy bullet if off screen
							if (bullets[i][k].getX() < LEFT_EDGE || bullets[i][k].getX() > RIGHT_EDGE ||
									bullets[i][k].getY() < TOP_EDGE || bullets[i][k].getY() > BOTTOM_EDGE)
							{
								bullets[i][k] = null;
							}
							else // 6b. otherwise, check for bot collisions
							{
								if (state != TEST_MODE) // not if in test mode
								{
									for (int j = 0; j<NUM_BOTS; j++)
									{
										if (!botsInfo[j].isOut() && i != j)
										{
											double d = Math.sqrt(Math.pow(bullets[i][k].getX()-(botsInfo[j].getX()+Bot.RADIUS),2)+Math.pow(bullets[i][k].getY()-(botsInfo[j].getY()+Bot.RADIUS),2));
											if (d < Bot.RADIUS) // hit something
											{
												bullets[i][k] = null; // no more bullet
												if (botsInfo[j].isDead() == false) // kill bot if possible
												{
													if (soundOn)
														death.play();
													botsInfo[i].addKill();
													botsInfo[j].killed(botsInfo[i].getName());
													botsInfo[j].setTimeOfDeath(timePlayed);
													botsInfo[j].setScore(currentScore(j,false)); // final score of dead bot
													//botsInfo[i].setScore(currentScore(i,false));
													botsLeft--;
													sendMessage(SYSTEM_MSG, botsInfo[j].getName()+" destroyed by "+botsInfo[i].getName()+".");
												}
												break; // only one collision per bullet
											}
										}
									}
								}
							}
						}
					}
			}
			// paint the screen
			paintBuffer();
		}
		// *** paused or instant replay mode?
		else if (state == GAME_PAUSED || state == GAME_OVER || state == WINNER)
		{
			if (--pauseCount <= 0)
				pauseCount = PAUSE_FLASH_TIME;
			if (++replayCounter >= this.REPLAY_SPEED)
			{
				replayCounter = 0;
				if (replayCurrentFrame == replayEndFrame && endFrameCounter > 0)
					endFrameCounter--;
				else
				{
					if (++replayCurrentFrame >= NUM_FRAMES)
						replayCurrentFrame = 0;
					if (replayCurrentFrame == replayEndFrame)
						endFrameCounter = END_FRAME_COUNT;
				}
			}
			// paint the screen
			repaint();
		}
		else // countdown to the start
		{
			if (++replayEndFrame >= NUM_FRAMES)
				replayEndFrame = 0;
			countDown--;
			if (countDown == 0)
			{
				startTime = System.currentTimeMillis();
				if (soundOn)
					drone.loop();
			}
			// paint the screen
			paintBuffer();
		}
	}

	/**
	 * Sends a broadcast message to the bots.
	 * @param id Message sender
	 * @param msg Message
	 */
	private void sendMessage(int id, String msg)
	{
		if (msg != null && !msg.equals(""))
		{
			msg = msg.substring(0,Math.min(MAX_MESSAGE_LENGTH,msg.length()));
			// send the message to the bots
			for (int i = 0; i<NUM_BOTS; i++)
				if (!botsInfo[i].isDead() && !botsInfo[i].isOut() && !botsInfo[i].isOverheated())
				{
					// BOT METHOD CALL - timed and exceptions caught
					long startThink = System.nanoTime();
					try {
						bots[i].incomingMessage(id, msg);
					}
					catch(Exception e)
					{
						botsInfo[i].exceptionThrown(e);
					}
					botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
					// ***********************
				}
			// echo the message to the screen
			if (id >= 0)
			{
				botsInfo[id].sentMessage(); // increment messages sent by bot
				messages.addFirst(botsInfo[id].getName()+": "+msg);
				// check if over limit
				if (botsInfo[id].getNumMessages() >= TIME_LIMIT/SECS_PER_MSG)
					sendMessage(-1,"Messages capped for "+botsInfo[id].getName());
			}
			else
				messages.addFirst("Referee: "+msg);
			// reset the scroll every time a message sent
			firstMessage = 0;
		}
	}

	/**
	 * Computes the score for a given bot
	 * @param botNum The bot to compute score for
	 * @param gameOver Whether or not the game is over (full score alloted in this case)
	 * @return The score
	 */
	private double currentScore(int botNum, boolean gameOver)
	{
		double score = KILL_SCORE * botsInfo[botNum].getNumKills() * (round+1.0)/2 - ERROR_PENALTY * botsInfo[botNum].getNumExceptions() + EFFICIENCY_BONUS * (PROCESSOR_LIMIT - botsInfo[botNum].getThinkTime());
		if (score < 0)
			score = 0;
		if (gameOver)
			score += TIME_LIMIT * 0.1 * (round+1.0)/2;
		else
		{
			if (botsInfo[botNum].getTimeOfDeath() > 0)
				score += botsInfo[botNum].getTimeOfDeath()*POINTS_PER_SECOND*(round+1.0)/2;
			else
				score += timePlayed*POINTS_PER_SECOND*(round+1.0)/2;
		}
		return score < 0?0:score;
	}

	/**
	 * Sorts the botInfo array by score.
	 * @param descending If true, sorts in descending order. Ascending otherwise
	 * @return the sorted array
	 */
	private BotInfo[] sortedBotInfoArray(boolean descending)
	{
		// Create a new array for sorting
		BotInfo[] newInfos = new BotInfo[NUM_BOTS];
		for (int i=0; i<NUM_BOTS; i++)
		{
			newInfos[i] = botsInfo[i];
		}
		// Bubblesort. I know, I know...
		for (int i=NUM_BOTS-1; i>0; i--)
			for (int j=1; j<=i; j++)
			{
				double score1 = newInfos[j].getScore()+newInfos[j].getCumulativeScore();
				double score2 = newInfos[j-1].getScore()+newInfos[j-1].getCumulativeScore();

				if (descending && score1 > score2
						|| !descending && score1 < score2
						|| descending && score1 == score2 && !(newInfos[j].isOut()
								|| newInfos[j].isOutNextRound()) && (newInfos[j-1].isOut()||newInfos[j-1].isOutNextRound())
				)
				{
					BotInfo temp = newInfos[j-1];
					newInfos[j-1] = newInfos[j];
					newInfos[j] = temp;
				}
			}
		// return the sorted array
		return newInfos;
	}

	/**
	 * Increases the game speed (
	 */
	private void changeGameSpeed()
	{
		if (speed < 8)
			speed *= 2;
		gameTimer.setDelay(1000/30/speed);
	}

	/**
	 * Decreases the game speed
	 */
	private void changeGameSpeedDown()
	{
		if (speed > 1)
			speed /= 2;
		gameTimer.setDelay(1000/30/speed);
	}

	/**
	 * Resets the game speed
	 */
	private void resetGameSpeed()
	{
		speed = 1;
		gameTimer.setDelay(1000/30/speed);
	}

	/**
	 * This method paints the bots and bullets for the game area into
	 * the instant replay buffer. Then it calls a repaint to trigger
	 * a call to the paint method which displays this buffer to the
	 * screen.
	 */
	private void paintBuffer()
	{
		//System.out.println("painting");
		if (ready) // avoid race condition on startup
		{
			// get the next image from the rotating instant replay buffer
			Graphics g = replayImages[replayEndFrame].getGraphics();

			// a little trick to get imageobserver callbacks when the bot images are loaded
			// may not be necessary any more in 2.0
			if (imagesToLoad.size() > 0)
			{
				LinkedList<Image> newImagesToLoad = new LinkedList<Image>();
				Iterator<Image> i = imagesToLoad.iterator();
				while (i.hasNext())
				{
					Image image = i.next();
					if (!g.drawImage(image,-10000,-10000,this))
						newImagesToLoad.add(image);
				}
				imagesToLoad = newImagesToLoad;
			}

			// clear the screen
			g.setColor(Color.black);
			g.fillRect(0,0,RIGHT_EDGE,BOTTOM_EDGE+TEXT_BUFFER);

			// Draw the bots & their bullets
			for (int i=0; i<NUM_BOTS; i++)
			{
				if (!botsInfo[i].isOut()) // skip bots that are out
					if (botsInfo[i].isDead()) // dead bot
						g.drawImage(deadBot, (int)(botsInfo[i].getX()+0.5), (int)(botsInfo[i].getY()+0.5), Bot.RADIUS*2, Bot.RADIUS*2, this);
					else if (botsInfo[i].isOverheated()) // overheated bot
						g.drawImage(overheated, (int)(botsInfo[i].getX()+0.5), (int)(botsInfo[i].getY()+0.5), Bot.RADIUS*2, Bot.RADIUS*2, this);
					else // active bot
					{
						// BOT METHOD CALL - timed and exceptions caught
						long startThink = System.nanoTime();
						try {
							bots[i].draw(g, (int)(botsInfo[i].getX()+0.5), (int)(botsInfo[i].getY()+0.5));
						}
						catch(Exception e)
						{
							botsInfo[i].exceptionThrown(e);
						}
						botsInfo[i].setThinkTime((System.nanoTime()-startThink)*nanoTimeCorrection);
						// ***********************

						// special test mode output
						if (state == TEST_MODE)
						{
							if (flashCounter < FLASH_TIME/2)
							{
								g.setColor(Color.red);
								g.drawOval((int)(botsInfo[i].getX()+0.5)-1, (int)(botsInfo[i].getY()+0.5)-1, Bot.RADIUS*2+2, Bot.RADIUS*2+2);
							}
							if (state == TEST_MODE)
							{
								g.setFont(new Font("MonoSpaced",Font.PLAIN,NAME_FONT));
								g.setColor(Color.gray);
								g.drawString(""+botsInfo[i].getNumExceptions(), (int)(botsInfo[i].getX()+0.5)+Bot.RADIUS*2+2, (int)(botsInfo[i].getY()+0.5)+Bot.RADIUS);
								g.drawString(""+df2.format(botsInfo[i].getThinkTime()), (int)(botsInfo[i].getX()+0.5)+Bot.RADIUS*2+2, (int)(botsInfo[i].getY()+0.5)+Bot.RADIUS+NAME_FONT);
							}
						}
					}

				// bullets for bot i
				for (int j=0; j<NUM_BULLETS; j++)
				{
					if (bullets[i][j] != null)
						bullets[i][j].draw(g);
				}
			}

			// draw the bot titles
			// these are drawn last so they're on top of the other bots
			for (int i=0; i<NUM_BOTS; i++)
			{
				if (botsInfo[i].isDead() == false && botsInfo[i].isOut() == false)
				{
					g.setFont(new Font("MonoSpaced",Font.PLAIN,NAME_FONT));
					// default is red, but goes to gray if they can take a shot
					g.setColor(new Color (170,42,42));
					if (!botsInfo[i].isOverheated())
						for (int j=0; j<NUM_BULLETS; j++)
							if (bullets[i][j] == null)
							{
								g.setColor(Color.gray);
								break;
							}
					// get and display the bots title
					String title = "";
					if (showNames)
						title = botsInfo[i].getName();
					else if (showScores)
						title = ""+df.format(botsInfo[i].getScore());
					else if (showTeams)
						title = botsInfo[i].getTeamName();

					// x calculation based on x-width of 0.5 font size with a one pixel spacer between letters
					g.drawString(title, (int)(botsInfo[i].getX()+Bot.RADIUS-(title.length()/2.0*(NAME_FONT*0.5+1))+0.5), (int)(botsInfo[i].getY()-1+0.5));
				}
			}
			// trigger a paint event
			repaint();
		}
	}

	/**
	 * This method prints out the stats for each robot in sorted order.
	 * Used at the end of each round, and also during the game when the
	 * DEBUG flag is set.
	 * @param g The Graphics object to draw on
	 */
	private void printStats(Graphics g)
	{
		BotInfo[] newInfos = sortedBotInfoArray(true);

		int xOffset = 5;
		int yOffset = 50;

		if (state != WAIT_TO_START)
		{
			g.setColor(new Color(60,60,60,130));
			g.fillRect(0, yOffset-STATS_FONT-5, RIGHT_EDGE, STATS_FONT*(NUM_BOTS+1)+10+24);

			g.setColor(Color.white);
			g.setFont(new Font("MonoSpaced",Font.BOLD,24));
			g.drawString("Stats for Round "+round, (RIGHT_EDGE+LEFT_EDGE)/2-120, yOffset);
			yOffset += 24;
			g.setFont(new Font("MonoSpaced",Font.PLAIN,STATS_FONT));
			g.drawString("Name     Team     Round  Total  Time  Errors  Messages  Processor  Killed By",xOffset,yOffset);
			for (int i=0; i<NUM_BOTS; i++)
			{
				String output = pad(newInfos[i].getName(), 8, false) + " " + pad(newInfos[i].getTeamName(),8, false)+" ";
				output += (newInfos[i].isOut()?"     ":pad(df.format(newInfos[i].getScore()),5, true))+"  "+pad(df.format(newInfos[i].getScore()+newInfos[i].getCumulativeScore()),5, true)+" ";
				if (!newInfos[i].isOut())
				{
					output += (newInfos[i].isDead()?pad(df.format(newInfos[i].getTimeOfDeath()),5,true):(state == GAME_OVER || state == WINNER?pad(df.format(TIME_LIMIT),5,true):pad(df.format(timePlayed),5,true)))+" ";
					output += pad(""+newInfos[i].getNumExceptions(),6,true)+"    "+pad(""+newInfos[i].getNumMessages(),4,true)+"    ";
					output += pad(df2.format(newInfos[i].getThinkTime()),8, true)+"    "+pad(newInfos[i].getKilledBy(),8,false);
				}

				if (newInfos[i].isDead() && state != GAME_OVER && state != WINNER || newInfos[i].isOut() || state == GAME_OVER && newInfos[i].isOutNextRound() || state == WINNER && i != 0)
					g.setColor(Color.gray);
				else
					g.setColor(Color.lightGray);
				g.drawString(output,xOffset,yOffset+STATS_FONT+i*STATS_FONT);

			}
		}
	}
	/**
	 * Special string padding method for printStats
	 * @param s The string to pad
	 * @param n The target length
	 * @param rightJust Right justify if true, otherwise left justify.
	 * @return The padded string
	 */

	private String pad(String s, int n, boolean rightJust)
	{
		if (s == null)
			s = "";
		int l = s.length();
		for (int i=l; i < n; i++)
			if (rightJust)
				s = " " + s;
			else
				s = s + " ";
		l = s.length();
		if (l > n)
			s = s.substring(0, n);
		return s;
	}

	/**
	 * Paints the screen. Assumes that paintBuffer() has been called recently
	 * to paint the current game state into the instant replay buffer.
	 * @param g The Graphics context
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (ready) // avoid race condition on startup
		{
			// switch g to the offline buffer (double-buffering)
			//Graphics g2 = g;
			//g = buffer.getGraphics();

			// black out hte screen
			g.setColor(Color.black);
			g.fillRect(0,0,getWidth(),getHeight());

			// draw the main window
			if (state == GAME_PAUSED || state == GAME_OVER || state == WINNER)
				g.drawImage(replayImages[replayCurrentFrame], 0, 0, this); // draws from the instant replay buffer
			else
				g.drawImage(replayImages[replayEndFrame], 0, 0, this);     // draws the latest frame if game is on

			// Message bars
			if (state == GAME_PAUSED)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(0, BOTTOM_EDGE - 30, RIGHT_EDGE, 26);
				g.setColor(Color.white);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 20));
				g.drawString("Game Paused. Showing Instant Replay.",10,BOTTOM_EDGE - 10);
			}
			else if (state == GAME_OVER)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(0, BOTTOM_EDGE - 30, RIGHT_EDGE, 26);
				g.setColor(Color.white);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 20));
				if (CUMULATIVE_SCORING)
					g.drawString(botsInfo[winnerID].getName()+" Leading After "+round+" Round"+(round>1?"s":"")+". Click This Bar to Continue.",10,BOTTOM_EDGE - 10);
				else
					g.drawString(botsInfo[winnerID].getName()+" Wins Round "+round+". Click This Bar to Continue.",10,BOTTOM_EDGE - 10);
				printStats(g);
			}
			else if (state == WINNER)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(0, BOTTOM_EDGE - 30, RIGHT_EDGE, 26);
				g.setColor(Color.white);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 20));
				if (CUMULATIVE_SCORING)
					g.drawString(botsInfo[winnerID].getName()+" Wins after "+round+" Round"+(round>1?"s":"")+"! Click This Bar to Restart.",10,BOTTOM_EDGE - 10);
				else
					g.drawString(botsInfo[winnerID].getName()+" Wins the Final Round! Click This Bar to Restart.",10,BOTTOM_EDGE - 10);
				printStats(g);
			}
			else if (state == TEST_MODE)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(0, BOTTOM_EDGE - 30, RIGHT_EDGE, 26);
				g.setColor(Color.white);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 20));
				g.drawString("Welcome to Battle Bots. Click This Bar to Start.",10,BOTTOM_EDGE-10);
			}
			else if (state == WAIT_TO_START)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(0, BOTTOM_EDGE - 30, RIGHT_EDGE, 26);
				g.setColor(Color.white);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 22));
				if (botsLeft <= ELIMINATIONS_PER_ROUND+1)
					g.drawString("Click This Bar to Start the Final Round.",10,BOTTOM_EDGE - 10);
				else
					g.drawString("Click This Bar to Start Round "+round+".",10,BOTTOM_EDGE - 10);

				// display the rules
				g.setColor(Color.white);
				if (round == 1)
				{
					g.setFont(new Font("MonoSpaced",Font.BOLD, 80));
					g.drawString("Battle", RIGHT_EDGE-350, TOP_EDGE+90);
					g.drawString("Bots", RIGHT_EDGE-300, TOP_EDGE+160);
				}
				else
				{
					g.setFont(new Font("MonoSpaced",Font.BOLD, 80));
					if (botsLeft <= ELIMINATIONS_PER_ROUND+1)
					{
						g.drawString("Final", RIGHT_EDGE-300, TOP_EDGE+90);
						g.drawString("Round", RIGHT_EDGE-300, TOP_EDGE+160);
					}
					else
					{
						g.drawString("Round", RIGHT_EDGE-300, TOP_EDGE+90);
						g.drawString("*"+round+"*", RIGHT_EDGE-250, TOP_EDGE+160);
					}
				}

				g.setFont(new Font("MonoSpaced",Font.BOLD, 22));
				int y = (TOP_EDGE+BOTTOM_EDGE)/2;
				g.drawString("The Rules", 10, y);
				g.setColor(Color.lightGray);
				g.setFont(new Font("MonoSpaced",Font.PLAIN, 14));
				y+=16;
				if (round == 1)
					g.drawString("- "+NUM_BOTS+" robots to start",10,y);
				else
					g.drawString("- "+(NUM_BOTS-ELIMINATIONS_PER_ROUND*(round-1))+" robots left",10,y);
				y+=15;
				g.drawString("- each round lasts "+TIME_LIMIT+" seconds",10,y);
				y+=15;
				g.drawString("- "+ELIMINATIONS_PER_ROUND+" robots eliminated each round",10,y);
				y+=15;
				g.drawString("- each robot can have "+NUM_BULLETS+" bullets active at once",10,y);
				y+=15;
				g.drawString("- each robot can send "+(int)(TIME_LIMIT/SECS_PER_MSG)+" messages per round",10,y);
				y+=15;
				g.drawString("- each robot has "+PROCESSOR_LIMIT+" seconds of processor time",10,y);
				y+=26;
				g.setFont(new Font("MonoSpaced",Font.BOLD, 22));
				g.setColor(Color.white);
				g.drawString("Scoring", 10, y);
				g.setFont(new Font("MonoSpaced",Font.PLAIN, 14));
				g.setColor(Color.lightGray);
				y+=16;
				g.drawString("- "+df.format(KILL_SCORE*(round+1.0)/2)+" points per kill, "+df.format(POINTS_PER_SECOND*(round+1.0)/2*10)+" points per 10 seconds of survival",10,y);
				y+=15;
				g.drawString("- "+EFFICIENCY_BONUS+" point bonus for each unused second of processor time",10,y);
				y+=15;
				g.drawString("- "+ERROR_PENALTY+" point penalty for each exception thrown",10,y);
				y+=15;
				if (CUMULATIVE_SCORING)
					g.drawString("- scores accumulate from round to round",10,y);
				else
					g.drawString("- robots' scores are reset between rounds",10,y);
			}

			// the menu
			if (showNames)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(RIGHT_EDGE-125, BOTTOM_EDGE+56, 49, 18);
				g.setColor(new Color(40,40,40,175));
				g.drawRect(RIGHT_EDGE-125, BOTTOM_EDGE+56, 49, 18);
			}
			else if (showTeams)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(RIGHT_EDGE-125, BOTTOM_EDGE+76, 49, 18);
				g.setColor(new Color(40,40,40,175));
				g.drawRect(RIGHT_EDGE-125, BOTTOM_EDGE+76, 49, 18);
			}
			else if (showScores)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(RIGHT_EDGE-74, BOTTOM_EDGE+56, 54, 18);
				g.setColor(new Color(40,40,40,175));
				g.drawRect(RIGHT_EDGE-74, BOTTOM_EDGE+56, 54, 18);
			}
			if (soundOn)
			{
				g.setColor(new Color(60,60,60,175));
				g.fillRect(RIGHT_EDGE-74, BOTTOM_EDGE+76, 54, 18);
				g.setColor(new Color(40,40,40,175));
				g.drawRect(RIGHT_EDGE-74, BOTTOM_EDGE+76, 54, 18);
			}
			g.setColor(Color.gray);
			g.setFont(new Font("MonoSpaced",Font.BOLD, 14));
			g.drawString("Names Scores", RIGHT_EDGE-120, BOTTOM_EDGE+69);
			g.drawString("Teams Sounds", RIGHT_EDGE-120, BOTTOM_EDGE+89);

			// the time clock
			if (state != GAME_PAUSED || pauseCount < PAUSE_FLASH_TIME/2)
			{
				g.setColor(Color.gray);
				g.setFont(new Font("MonoSpaced",Font.BOLD, 45));
				g.drawString(""+pad(df.format(Math.abs(TIME_LIMIT-timePlayed)),5,true),RIGHT_EDGE-152,BOTTOM_EDGE+40);
				if (speed != 1)
				{
					g.setFont(new Font("MonoSpaced",Font.BOLD, 10));
					g.drawString("x"+speed,RIGHT_EDGE-12,BOTTOM_EDGE+40);
				}
			}

			// the message area
			g.setFont(new Font("MonoSpaced",Font.PLAIN, 12));
			int offSet = 14;
			int counter = 0;
			double fade = 1;
			Iterator<String> i = messages.iterator();
			while (i.hasNext() && counter < 6 + firstMessage)
			{
				String msg = i.next();
				if (counter >= firstMessage)
				{
					if (msg.startsWith("Referee"))
						g.setColor(new Color((int)(128*fade),(int)(128*fade),(int)(128*fade)));
					else
						g.setColor(new Color((int)(128*fade),(int)(128*fade),0));

					g.drawString(msg.substring(0,Math.min(77,msg.length())),10,BOTTOM_EDGE+TEXT_BUFFER - offSet);
					offSet += 14;
					//fade /= 1.15;
				}
				counter++;
			}

			// print the stats if in debug mode
			if (DEBUG && state != TEST_MODE && state != GAME_OVER && state != WINNER )
				printStats(g);

			// draw the lines to separate screen areas
			g.setColor(Color.gray);
			g.drawLine(0, BOTTOM_EDGE+1, getWidth(), BOTTOM_EDGE+1);
			g.drawLine(0, TOP_EDGE-1, getWidth(), TOP_EDGE-1);
			g.drawLine(RIGHT_EDGE-145,BOTTOM_EDGE+1,RIGHT_EDGE-145,getHeight());
			g.drawLine(RIGHT_EDGE-145,BOTTOM_EDGE+50,getWidth(),BOTTOM_EDGE+50);

			// dump the offline buffer to the screen (double-buffering)
			//g2.drawImage(buffer,0,0,this);
		}
	}

	/**
	 * Handles user's mouse clicks on the menu buttons, the time clock,
	 * and the "click here" bars.
	 * @param e The MouseEvent
	 */
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) // left button only
		{
			if (e.getY()<BOTTOM_EDGE) // click is on the playing field
			{
				if (state == WAIT_TO_START && e.getY()>BOTTOM_EDGE-30)	// clicked on the wait to start message bar
				{
					if (soundOn)
						fanfare.play();
					countDown = 60;
					startTime = System.currentTimeMillis();
					gameTimer.start();
					if (botsLeft <= ELIMINATIONS_PER_ROUND+1)
						sendMessage(SYSTEM_MSG,"Final Round starting. Good luck!");
					else
						sendMessage(SYSTEM_MSG,"Round "+round+" starting. Good luck!");
					state = GAME_ON;
				}
				else if (state == GAME_OVER && e.getY()>BOTTOM_EDGE-30) // clicked on the click for next round bar
				{
					if (soundOn)
						stop.play();
					gameTimer.stop();
					timePlayed = 0;
					state = WAIT_TO_START;
					reset();
				}
				else if (state == WINNER && e.getY()>BOTTOM_EDGE-30) // clicked on the reset bar
					fullReset();
				else if (state == TEST_MODE) // in test mode
				{
					// check for and process bot grabs
					gotBot = -1;
					for (int i=0; i<NUM_BOTS; i++)
					{
						if (e.getX() > botsInfo[i].getX() && e.getX() < botsInfo[i].getX()+Bot.RADIUS*2 &&
								e.getY() > botsInfo[i].getY() && e.getY() < botsInfo[i].getY()+Bot.RADIUS*2)
						{
							gotBot = i;
							forceBotX = (int)(botsInfo[i].getX()+0.5);
							forceBotY = (int)(botsInfo[i].getY()+0.5);
							mouseInitialX = e.getX();
							mouseInitialY = e.getY();
						}
					}
					// if no bot grab, check if on the "click to start" bar
					if (gotBot == -1 && e.getY()>BOTTOM_EDGE-30)
					{
						if (soundOn)
							stop.play();
						gameTimer.stop();
						state = WAIT_TO_START;
						round = 0;
						reset();
					}
				}
			}
			// click is on the lower message/menu area
			else if(e.getX()>=RIGHT_EDGE-145 && e.getY() < BOTTOM_EDGE+50) // click on clock
			{
				if (state == GAME_PAUSED) // unpause
				{
					if (soundOn)
						drone.loop();
					startTime = System.currentTimeMillis();
					state = GAME_ON;
				}
				else if (state == GAME_ON && countDown <= 0) // pause
				{
					drone.stop();
					if (soundOn)
						stop.play();
					pauseTime = System.nanoTime();
					pauseCount = PAUSE_FLASH_TIME;
					replayCurrentFrame = replayEndFrame;
					endFrameCounter = END_FRAME_COUNT;
					state = GAME_PAUSED;
					resetGameSpeed();
				}
			}
			else if(e.getX()>=RIGHT_EDGE-125 && e.getX()<=RIGHT_EDGE-125+49)// clicked on names or teams button
			{
				if (e.getY()>=BOTTOM_EDGE+56)
					if (e.getY()>=BOTTOM_EDGE+76) // teams button
					{
						if (showTeams)
							showTeams = false;
						else
						{
							showTeams = true;
							showScores = false;
							showNames = false;
						}
					}
					else if (showNames) // names button
						showNames = false;
					else
					{
						showNames = true;
						showScores = false;
						showTeams = false;
					}
			}
			else if(e.getX()>=RIGHT_EDGE-74 && e.getX()<=RIGHT_EDGE-69+55)// clicked on sound or scores button
			{
				if (e.getY()>=BOTTOM_EDGE+56)
					if (e.getY()>=BOTTOM_EDGE+76) // sound
					{
						if (soundOn)
						{
							soundOn = false;
							drone.stop();
						}
						else
						{
							if (state == GAME_ON)
								drone.loop();
							soundOn = true;
						}
					}
					else if (showScores) // scores
						showScores = false;
					else
					{
						showScores = true;
						showNames = false;
						showTeams = false;
					}
			}
			// paint the screen
			paintBuffer();
		}
	}

	/**
	 * When a mouse button is released, release any grabbed bot.
	 * @param e The MouseEvent
	 */
	public void mouseReleased(MouseEvent e) {
		gotBot = -1;
	}

	/**
	 * Scroll event. Scroll the messages or change game speed depending on
	 * location of the mouse.
	 * @param e The MouseWheelEvent
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getY() >= BOTTOM_EDGE && e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL)
		{
			if(e.getX()>=RIGHT_EDGE-145 && e.getY() < BOTTOM_EDGE+50) // change game speed
			{
				if (state == GAME_ON)
					if (e.getWheelRotation() < 0)
						changeGameSpeed();
					else
						changeGameSpeedDown();
			}
			else if (e.getX()< RIGHT_EDGE-145) // message window scroll
			{
				firstMessage -= e.getWheelRotation();
				if (firstMessage < 0)
					firstMessage = 0;
				else if (firstMessage > messages.size()-6)
					firstMessage = Math.max(0,messages.size()-6);
			}
		}
		// paint the screen
		paintBuffer();
	}

	/**
	 * Drag a grabbed bot if there is one. The actual force move is processed in
	 * actionPerformed(). Here we just update the offset that the mouse has moved.
	 * @param e The MouseEvent
	 */
	public void mouseDragged(MouseEvent e) {
		if (state == TEST_MODE)
		{
			forceBotX += e.getX()-mouseInitialX;
			forceBotY += e.getY()-mouseInitialY;
			mouseInitialX = e.getX();
			mouseInitialY = e.getY();
		}
	}


	/**
	 * Unused interface method
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	 * Unused interface method
	 */
	public void mouseEntered(MouseEvent e) {}

	/**
	 * Unused interface mthod
	 */
	public void mouseExited(MouseEvent e) {}

	/**
	 * Unused interface method
	 */
	public void mouseMoved(MouseEvent arg0) {}
}
