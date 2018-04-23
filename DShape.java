import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JPanel;

public class DShape implements ModelListener, Serializable{
	DShapeModel pointer = new DShapeModel();
	int priority;
	Canvas canvas;
	
	public DShape() {}
	
	public void draw(Graphics g)
	{
		
	}
	
	public void drawKnobs(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.BLACK);
        ArrayList<Point> knobs = this.getKnobs();
        Point topLeft = knobs.get(0);
        Point topRight = knobs.get(1);
        Point bottomLeft = knobs.get(2);
        Point bottomRight = knobs.get(3);
        Rectangle2D.Double topLeftRect = new Rectangle2D.Double(topLeft.getX() - 4.0, topLeft.getY() - 4.0, 9.0, 9.0);
        Rectangle2D.Double topRightRect = new Rectangle2D.Double(topRight.getX() - 4.0, topRight.getY() - 4.0, 9.0, 9.0);
        Rectangle2D.Double bottomLeftRect = new Rectangle2D.Double(bottomLeft.getX() - 4.0, bottomLeft.getY() - 4.0, 9.0, 9.0);
        Rectangle2D.Double bottomRightRect = new Rectangle2D.Double(bottomRight.getX() - 4.0, bottomRight.getY() - 4.0, 9.0, 9.0);
        g2d.fill(topLeftRect);
        g2d.fill(topRightRect);
        g2d.fill(bottomLeftRect);
        g2d.fill(bottomRightRect);
        this.canvas.repaint();
    }
	
	public void setPointer(DShapeModel d)
	{
		pointer = d;
	}
	
	public void setCanvas(Canvas c)
	{
		canvas = c;
	}
	
	public void modelChanged(DShapeModel model)
	{
		this.canvas.repaint();
	}
	
	public DShapeModel getModel() {
		return this.pointer;
	}
	
	public ArrayList<Point> getKnobs() {
        ArrayList<Point> points = new ArrayList<Point>();
        Point topLeft = new Point(this.pointer.getX(), this.pointer.getY());
        Point bottomRight = new Point(this.pointer.getX() + this.pointer.getWidth(), this.pointer.getY() + this.pointer.getHeight());
        Point bottomLeft = new Point(this.pointer.getX(), this.pointer.getY() + this.pointer.getHeight());
        Point topRight = new Point(this.pointer.getX() + this.pointer.getWidth(), this.pointer.getY());
        points.add(topLeft);
        points.add(topRight);
        points.add(bottomLeft);
        points.add(bottomRight);
        return points;
    }
	
	public Rectangle getBounds() {
        return this.pointer.getBounds();
    }

}
