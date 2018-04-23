import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DLine extends DShape{
	DLineModel pointer = new DLineModel();
	Line2D line = new Line2D.Double();
	
	public void draw(Graphics g)
	{
		g.setColor(pointer.color);
		g.drawLine(pointer.x,  pointer.y, pointer.width, pointer.height);
		line.setLine(pointer.x,  pointer.y, pointer.width, pointer.height);
	}

	public Line2D getLine()
	{
		return line;
	}
	
	public ArrayList<Point> getKnobs() {
        ArrayList<Point> points = new ArrayList<Point>();
        Point left = new Point(this.pointer.getX(), this.pointer.getY());
        Point right = new Point(this.pointer.getWidth(), this.pointer.getHeight());
        points.add(left);
        points.add(right);
        return points;
    }

    public void drawKnobs(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.BLACK);
        ArrayList<Point> knobs = this.getKnobs();
        Point leftKnob = knobs.get(0);
        Point rightKnob = knobs.get(1);
        Rectangle2D.Double left = new Rectangle2D.Double(leftKnob.getX() - 4.0, leftKnob.getY() - 4.0, 9.0, 9.0);
        Rectangle2D.Double right = new Rectangle2D.Double(rightKnob.getX() - 4.0, rightKnob.getY() - 4.0, 9.0, 9.0);
        g2d.fill(left);
        g2d.fill(right);
    }
}
