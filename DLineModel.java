import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class DLineModel extends DShapeModel{
	Point p1 = new Point();
	Point p2 = new Point();
	public DLineModel() {
		super();
	}
	
	public Rectangle getBounds() {
        return new Rectangle(Math.min(this.p1.x, this.p2.x), Math.min(this.p1.y, this.p2.y), Math.abs(this.p1.x - this.p2.x), Math.abs(this.p1.y - this.p2.y));
    }
	
	public void setRectangle(Rectangle r) {
		rectangle = r;
		x = r.x;
		y = r.y;
		height = r.height;
		width = r.width;
		p1.x = r.x;
		p1.y = r.y;
		p2.x = r.width;
		p2.y = r.height;
		this.notifyModelChanged();
	}
	
	public void setXForLine(int x) {
		int beforeX = rectangle.x;
		this.x = x;
		this.rectangle.x = x;
		int afterX = rectangle.x - beforeX;
		if (this instanceof DLineModel) {
			width += afterX;
			rectangle.width += afterX;
		}
		this.notifyModelChanged();
	}

	public void setYForLine(int y) {
		int beforeY = rectangle.y;
		this.y = y;
		this.rectangle.y = y;
		int afterY = rectangle.y - beforeY;
		if (this instanceof DLineModel) {
			height += afterY;
			rectangle.height += afterY;
		}
		this.notifyModelChanged();
	}
	
	public Point getP1() {
        return this.p1;
    }

    public int getP1X() {
        return this.p1.x;
    }

    public void setP1X(int x) {
        this.p1.x = x;
        this.x = x;
        this.rectangle.x = x;
        this.notifyModelChanged();
    }

    public int getP1Y() {
        return this.p1.y;
    }

    public void setP1Y(int y) {
        this.p1.y = y;
        this.y = y;
        this.rectangle.y = y;
        this.notifyModelChanged();
    }

    public void setP1(Point point) {
        this.p1 = point;
        this.x = point.x;
        this.y = point.y;
        this.rectangle.x = point.x;
        this.rectangle.y = point.y;
        this.notifyModelChanged();
    }

    public Point getP2() {
        return this.p2;
    }

    public int getP2X() {
        return this.p2.x;
    }

    public void setP2X(int x) {
    	this.p2.x = x;
        this.width = x;
        this.rectangle.width = x;
        this.notifyModelChanged();
    }

    public int getP2Y() {
        return this.p2.y;
    }

    public void setP2Y(int y) {
    	this.p2.y = y;
        this.height = y;
        this.rectangle.height = y;
        this.notifyModelChanged();
    }

    public void setP2(Point point) {
    	this.p2 = point;
        this.width = point.x;
        this.height = point.y;
        this.rectangle.width = point.x;
        this.rectangle.height = point.y;
        this.notifyModelChanged();
    }

}
