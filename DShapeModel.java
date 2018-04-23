import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.Serializable;
import java.util.ArrayList;

public class DShapeModel implements Serializable {
	int id = 0;
	int x = 0;
	int y = 0;
	int height = 0;
	int width = 0;
	Color color = new Color(0);
	Rectangle rectangle = new Rectangle();
	ArrayList<ModelListener> listeners = new ArrayList();
	private int red;
    private int green;
    private int blue;

	public DShapeModel() {
		rectangle.x = 0;
		rectangle.y = 0;
		rectangle.width = 0;
		rectangle.height = 0;
		color = Color.GRAY;
	}

    public void mimic(DShapeModel other) {
        this.setX(other.getX());
        this.setY(other.getY());
        this.setWidth(other.getWidth());
        this.setHeight(other.getHeight());
        this.setColor(other.getColor());
        this.remColors();
        this.setId(other.getId());
    }

	public Rectangle getBounds() {
		return rectangle;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Color getColor() {
		return color;
	}

	public void setBounds(int w, int h) {
		rectangle.width = w;
		rectangle.height = h;
	}

	public void setRectangle(Rectangle r) {
		rectangle = r;
		x = r.x;
		y = r.y;
		height = r.height;
		width = r.width;
		this.notifyModelChanged();
	}

	public void setColor(Color c) {
		color = c;
		this.notifyModelChanged();
	}

	public void notifyModelChanged() {
		for (ModelListener listener : listeners) {
			listener.modelChanged(this);
		}
	}

	public void addModelListener(ModelListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    public void removeModelListener(ModelListener listener) {
        this.listeners.remove((Object)listener);
    }

    public void deleteAllListeners() {
        this.listeners.clear();
    }

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setX(int x) {
		this.x = x;
		this.rectangle.x = x;
		this.notifyModelChanged();
	}

	public int getX() {
		return this.x;
	}

	public void setY(int y) {
		this.y = y;
		this.rectangle.y = y;
		this.notifyModelChanged();
	}

	public int getY() {
		return this.y;
	}

	public void setHeight(int height) {
		this.height = height;
		this.rectangle.height = height;
		this.notifyModelChanged();
	}

	public int getHeight() {
		return this.height;
	}

	public void setWidth(int width) {
		this.width = width;
		this.rectangle.width = width;
		this.notifyModelChanged();
	}

	public int getWidth() {
		return this.width;
	}
	
	public void reset()
	{
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		rectangle.x = 0;
		rectangle.y = 0;
		rectangle.width = 0;
		rectangle.height = 0;
	}
	
	public int getRed() {
        return this.red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return this.green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return this.blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void remColors() {
        this.red = this.color.getRed();
        this.green = this.color.getGreen();
        this.blue = this.color.getBlue();
    }

    public void setColorRGB() {
        this.color = new Color(this.red, this.green, this.blue);
    }
}
