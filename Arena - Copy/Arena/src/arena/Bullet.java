package arena;
import java.awt.Color;
import java.awt.Graphics;

/**
 * This class defines a Bullet object. Bullets are single points for collision
 * purposes, but are drawn with a tail to make them more visible. The collision
 * point is on the leading side of the Bullet. None of this information is
 * accessible outside of the arena package.
 *
 * @author sam.scott
 * @version 1.0 (March 3, 2011)
 */
public class Bullet {

	/**
	 * Bullet position
	 */
	private double x, y;
	/**
	 * Bullet speed (pixels per time step)
	 */
	private double xSpeed, ySpeed;

	/**
	 * Constructor for Bullet
	 * @param x Initial X position
	 * @param y Initial Y position
	 * @param xSpeed Speed in X direction
	 * @param ySpeed Speed in Y direction
	 */
	public Bullet(double x, double y, double xSpeed, double ySpeed)
	{
		this.x = x;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}

	/**
	 * Deep copy for a Bullet object
	 * @return A new Bullet that is a copy of this one
	 */
	protected Bullet copy()
	{
		Bullet b = new Bullet(x, y, xSpeed, ySpeed);
		return b;
	}

	/**
	 * Advance the bullet one time step
	 */
	protected void moveOneStep()
	{
		x = x + xSpeed;
		y = y + ySpeed;
	}

	/**
	 * Draw the bullet
	 * @param g The Graphics object to draw on
	 */
	protected void draw(Graphics g)
	{
		g.setColor (new Color(128,128,0));
		int xStart = (int)(x+0.5);
		int yStart = (int)(y+0.5);
		g.drawLine(xStart, yStart, (int)(xStart-xSpeed+0.5), (int)(yStart-ySpeed+0.5));
	}

	/**
	 * @return The Bullet's X location
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The Bullet's Y location
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return The Bullet's speed in the X direction (pixels per time step)
	 */
	public double getXSpeed() {
		return xSpeed;
	}

	/**
	 * @return The Bullet's speed in the Y direction (pixels per time step)
	 */
	public double getYSpeed() {
		return ySpeed;
	}
}
