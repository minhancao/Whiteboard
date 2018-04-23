import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DText extends DShape{
	 DTextModel pointer;
	    Rectangle bounds;

	    protected DText(DShapeModel pointer) {
	        this.pointer = (DTextModel)pointer;
	    }

	    private Font computeFont(Graphics g) {
	        double size = 1.0;
	        double previous = 1.0;
	        Font theFont = new Font(this.pointer.getFontName(), 0, (int)size);
	        FontMetrics fontMetrics = g.getFontMetrics(theFont);
	        while (fontMetrics.getHeight() < this.pointer.getHeight()) {
	            previous = size;
	            size = size * 1.1 + 1.0;
	            theFont = new Font(this.pointer.getFontName(), 0, (int)size);
	            fontMetrics = g.getFontMetrics(theFont);
	        }
	        return new Font(this.pointer.getFontName(), 0, (int)previous);
	    }

	    @Override
	    public void draw(Graphics g) {
	        Font f = this.computeFont(g);
	        FontMetrics metrics = g.getFontMetrics(f);
	        Graphics2D g2d = (Graphics2D)g;
	        Shape oldClip = g.getClip();
	        Rectangle rect = new Rectangle(this.pointer.getX(), this.pointer.getY(), this.pointer.getWidth(), this.pointer.getHeight());
	        this.bounds = (Rectangle)oldClip.getBounds().createIntersection(rect.getBounds2D());
	        g.setClip(oldClip.getBounds().createIntersection(rect.getBounds2D()));
	        g.setColor(this.pointer.getColor());
	        g.setFont(f);
	        g.drawString(this.pointer.getText(), this.pointer.getX(), this.pointer.getY() + metrics.getAscent());
	        g.setClip(oldClip);
	    }

	    @Override
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

	    @Override
	    public void drawKnobs(Graphics g) {
	        Graphics2D g2d = (Graphics2D)g;
	        g2d.setColor(Color.BLACK);
	        ArrayList<Point> knobs = this.getKnobs();
	        Point topLeft = knobs.get(0);
	        Point bottomRight = knobs.get(3);
	        Point topRight = knobs.get(1);
	        Point bottomLeft = knobs.get(2);
	        Rectangle2D.Double topLeftRect = new Rectangle2D.Double(topLeft.getX() - 4.0, topLeft.getY() - 4.0, 9.0, 9.0);
	        Rectangle2D.Double topRightRect = new Rectangle2D.Double(topRight.getX() - 4.0, topRight.getY() - 4.0, 9.0, 9.0);
	        Rectangle2D.Double bottomLeftRect = new Rectangle2D.Double(bottomLeft.getX() - 4.0, bottomLeft.getY() - 4.0, 9.0, 9.0);
	        Rectangle2D.Double bottomRightRect = new Rectangle2D.Double(bottomRight.getX() - 4.0, bottomRight.getY() - 4.0, 9.0, 9.0);
	        g2d.fill(topLeftRect);
	        g2d.fill(topRightRect);
	        g2d.fill(bottomLeftRect);
	        g2d.fill(bottomRightRect);
	    }

	    @Override
	    public DShapeModel getModel() {
	        return this.pointer;
	    }

	    @Override
	    public Rectangle getBounds() {
	        return this.bounds;
	    }

}
