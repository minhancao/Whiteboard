import java.awt.Graphics;

public class DOval extends DShape {
	DShapeModel pointer = new DOvalModel();
	
	public void draw(Graphics g)
	{
		g.setColor(pointer.color);
		g.fillOval(pointer.getX(), pointer.getY(), pointer.getWidth(), pointer.getHeight());
	}

}
