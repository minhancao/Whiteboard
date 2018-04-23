import java.awt.Color;
import java.awt.Graphics;

public class DRect extends DShape {
	DShapeModel pointer = new DRectModel();
	
	public void draw(Graphics g)
	{
		g.setColor(pointer.color);
		g.fillRect(pointer.rectangle.x,  pointer.rectangle.y, pointer.rectangle.width, pointer.rectangle.height);
	}

}
