import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class Canvas extends JPanel implements ModelListener {
	ArrayList<DShape> currentShapes = new ArrayList<DShape>(); // the "document" the user is editing
	// need to have last shape in list appearing "on top" of all other shapes before
	// it
	DShape current;
	JLayeredPane layeredPane = new JLayeredPane();
	boolean canDrag = false;
	boolean canResize = false;
	private Point movingKnob;
	private Point anchorKnob;
	int xMouse;
	int yMouse;
	int xMouseLine;
	int yMouseLine;
	Point clickPoint;
	int caseForResize;
	private Rectangle originalBounds;

	private Table shapeTable;
	private boolean clientMode;
	private TCPServer server;
	private boolean serverOn;
	private int selectedX;
	private int selectedY;
	private Canvas canvas;
	private Whiteboard whiteboard;

	public void turnOnClientMode() {
		this.clientMode = true;
	}

	public void turnOffClientMode() {
		this.clientMode = false;
	}

	public void save(String path) throws IOException {
		DShapeModel[] array = new DShapeModel[currentShapes.size()];
		for (int i = 0; i < currentShapes.size(); i++) {
			array[i] = currentShapes.get(i).pointer;
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + ".xml");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XMLEncoder xmlEncoder = new XMLEncoder(fos);
		xmlEncoder.writeObject(array);
		xmlEncoder.close();
	}

	public void load(String path) {
		FileInputStream fis = null;
		boolean error = false;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: File could not be found!");
			error = true;
		}
		if (!error) {
			currentShapes.clear();

			XMLDecoder xmlDecoder = new XMLDecoder(fis);

			DShapeModel[] array = (DShapeModel[]) xmlDecoder.readObject();
			xmlDecoder.close();
			if (whiteboard.serverOn) {
				whiteboard.server.notifyAll("deleteShapes", array[0]);
			}
			for (int i = 0; i < array.length; i++) {
				addShape(array[i]);
				if (whiteboard.serverOn) {
					whiteboard.server.notifyAll("add", array[i]);
				}
			}
		}
	}

	public void saveImage(String path, String type) {
		this.turnOnClientMode();
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), 1);
		Graphics2D g2 = image.createGraphics();
		this.paint(g2);
		try {
			ImageIO.write((RenderedImage) image, type, new File(path));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.turnOffClientMode();
	}

	public void setServer(TCPServer server) {
		this.server = server;
		this.serverOn = true;
	}

	public void handleChanges(String action, DShapeModel mod) {
		if (action.equals("add")) {
			this.addShape(mod);
		} else if (action.equals("remove")) {
			int id = mod.getId();
			for (DShape shape : this.currentShapes) {
				if (shape.getModel().getId() != id)
					continue;
				this.deleteShape(shape, id);
				break;
			}
		} else if (action.equals("front")) {
			int id = mod.getId();
			for (DShape shape : this.currentShapes) {
				if (shape.getModel().getId() != id)
					continue;
				this.sendSelectedToFront(shape, id);
				break;
			}
		} else if (action.equals("back")) {
			int id = mod.getId();
			for (DShape shape : this.currentShapes) {
				if (shape.getModel().getId() != id)
					continue;
				this.sendSelectedToBack(shape, id);
				break;
			}
		} else if (action.equals("change")) {
			int id = mod.getId();
			for (DShape shape : this.currentShapes) {
				if (shape.getModel().getId() != id)
					continue;
				shape.getModel().mimic(mod);
			}
		} else if (action.equals("deleteShapes")) {
			this.deleteAllShapes();
		} else if (action.equals("current")) {
			int id = mod.getId();
			for (DShape shape : this.currentShapes) {
				if (shape.getModel().getId() != id)
					continue;
				this.setCurrentShape(shape);
			}
		}

	}

	public Canvas(Whiteboard w) {
		this.whiteboard = w;
		this.shapeTable = new Table(this);
		this.clientMode = false;
		this.server = null;
		this.serverOn = false;
		this.canvas = this;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) { // to find current selected shape
				clickPoint = e.getPoint();
				originalBounds = new Rectangle(current.pointer.getBounds());
				int HIT_BOX_SIZE = 4;
				int boxX = e.getX() - HIT_BOX_SIZE / 2;
				int boxY = e.getY() - HIT_BOX_SIZE / 2;

				int width = HIT_BOX_SIZE;
				int height = HIT_BOX_SIZE;
				xMouse = current.pointer.x - e.getX();
				yMouse = current.pointer.y - e.getY();
				xMouseLine = current.pointer.width - e.getX();
				yMouseLine = current.pointer.height - e.getY();
				if (current != null && current instanceof DLine) {
					if (((DLine) current).getLine().intersects(boxX, boxY, width, height)) {
						canDrag = true;
					}
				}
				if (!(current instanceof DLine) && !canDrag && current != null
						&& current.pointer.getRectangle().intersects(boxX, boxY, width, height)) {
					canDrag = true;
				}

				if ((current instanceof DLine)) {
					ArrayList<Point> knobs = current.getKnobs();

					Point topLeft = knobs.get(0);
					Point topRight = knobs.get(1);
					Rectangle2D.Double topLeftRect = new Rectangle2D.Double(topLeft.getX() - 4.0, topLeft.getY() - 4.0,
							9.0, 9.0);
					Rectangle2D.Double topRightRect = new Rectangle2D.Double(topRight.getX() - 4.0,
							topRight.getY() - 4.0, 9.0, 9.0);
					if (!canDrag && topLeftRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = topLeft;
					}

					if (!canDrag && topRightRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = topRight;
					}

					if (movingKnob == topLeft) {
						anchorKnob = topRight;
						caseForResize = 0;
					}
					if (movingKnob == topRight) {
						anchorKnob = topLeft;
						caseForResize = 3;
					}
				}

				if (!(current instanceof DLine)) {
					ArrayList<Point> knobs = current.getKnobs();

					Point topLeft = knobs.get(0);
					Point topRight = knobs.get(1);
					Point bottomLeft = knobs.get(2);
					Point bottomRight = knobs.get(3);
					Rectangle2D.Double topLeftRect = new Rectangle2D.Double(topLeft.getX() - 4.0, topLeft.getY() - 4.0,
							9.0, 9.0);
					Rectangle2D.Double topRightRect = new Rectangle2D.Double(topRight.getX() - 4.0,
							topRight.getY() - 4.0, 9.0, 9.0);
					Rectangle2D.Double bottomLeftRect = new Rectangle2D.Double(bottomLeft.getX() - 4.0,
							bottomLeft.getY() - 4.0, 9.0, 9.0);
					Rectangle2D.Double bottomRightRect = new Rectangle2D.Double(bottomRight.getX() - 4.0,
							bottomRight.getY() - 4.0, 9.0, 9.0);
					if (!canDrag && topLeftRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = topLeft;
					}

					if (!canDrag && topRightRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = topRight;
					}

					if (!canDrag && bottomLeftRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = bottomLeft;
					}

					if (!canDrag && bottomRightRect.intersects(boxX, boxY, width, height)) {
						canResize = true;
						movingKnob = bottomRight;
					}

					if (movingKnob == topLeft) {
						anchorKnob = bottomRight;
						caseForResize = 0;
					}
					if (movingKnob == topRight) {
						anchorKnob = bottomLeft;
						caseForResize = 1;
					}
					if (movingKnob == bottomLeft) {
						anchorKnob = topRight;
						caseForResize = 2;
					}
					if (movingKnob == bottomRight) {
						anchorKnob = topLeft;
						caseForResize = 3;
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) { // to find current selected shape
				if (canDrag) {
					canDrag = false;
				}
				if (canResize) {
					canResize = false;
					canDrag = true; // idontknow what i did but this fixed the problem of resizing the shape to the
									// negative side and not being able to move it afterwards
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) { // to select current selected shape
				System.out.println(e.getX());
				int HIT_BOX_SIZE = 10;
				int boxX = e.getX() - HIT_BOX_SIZE / 2;
				int boxY = e.getY() - HIT_BOX_SIZE / 2;

				int width = HIT_BOX_SIZE;
				int height = HIT_BOX_SIZE;
				boolean setCurrent = false;
				for (DShape d : currentShapes) {
					if (e.getX() >= d.pointer.rectangle.x
							&& e.getX() <= d.pointer.rectangle.x + d.pointer.rectangle.width
							&& e.getY() >= d.pointer.rectangle.y
							&& e.getY() <= d.pointer.rectangle.y + d.pointer.rectangle.height) {
						if (current == null) {
							setCurrent = true;
							setCurrentShape(d);
							if (!(current instanceof DText)) {
								whiteboard.disableTextButtons();
							}
							if (current instanceof DText) {
								whiteboard.enableTextButtons();
							}

							if (whiteboard.serverOn) {
								whiteboard.server.notifyAll("current", current.pointer);
							}

						}
						if (current != null && (d.priority > current.priority
								|| !d.pointer.rectangle.getBounds().intersects(current.pointer.rectangle)))
							;
						{
							setCurrent = true;
							setCurrentShape(d);
							if (whiteboard.serverOn) {
								whiteboard.server.notifyAll("current", current.pointer);
							}
							if (!(current instanceof DText)) {
								whiteboard.disableTextButtons();
							}
							if (current instanceof DText) {
								System.out.println("hi this ran");
								whiteboard.enableTextButtons();
							}
						}
					}

					if (d instanceof DLine && ((DLine) d).line.intersects(boxX, boxY, width, height)) {
						if (current == null) {
							setCurrent = true;
							setCurrentShape(d);
							if (whiteboard.serverOn) {
								whiteboard.server.notifyAll("current", current.pointer);
							}
							if (!(current instanceof DText)) {
								whiteboard.disableTextButtons();
							}
						}
						if (current != null && (d.priority > current.priority
								|| !d.pointer.rectangle.getBounds().intersects(current.pointer.rectangle)))
							;
						{
							setCurrent = true;
							setCurrentShape(d);
							if (whiteboard.serverOn) {
								whiteboard.server.notifyAll("current", current.pointer);
							}
							if (!(current instanceof DText)) {
								whiteboard.disableTextButtons();
							}
						}
					}

					if (current != null) {// this is for so if u click out of the current shape, it'll deselect
						if (!(current instanceof DText)) {
							whiteboard.disableTextButtons();
						}
						ArrayList<Point> knobs = current.getKnobs();

						Point topLeft = knobs.get(0);
						Point topRight = knobs.get(1);

						Rectangle2D.Double topLeftRect = new Rectangle2D.Double(topLeft.getX() - 4.0,
								topLeft.getY() - 4.0, 9.0, 9.0);
						Rectangle2D.Double topRightRect = new Rectangle2D.Double(topRight.getX() - 4.0,
								topRight.getY() - 4.0, 9.0, 9.0);

						if (topLeftRect.intersects(boxX, boxY, width, height)) {
							setCurrent = true;
						}

						if (topRightRect.intersects(boxX, boxY, width, height)) {
							setCurrent = true;
						}

						if (knobs.size() >= 3) {
							Point bottomLeft = knobs.get(2);
							Point bottomRight = knobs.get(3);
							Rectangle2D.Double bottomLeftRect = new Rectangle2D.Double(bottomLeft.getX() - 4.0,
									bottomLeft.getY() - 4.0, 9.0, 9.0);
							Rectangle2D.Double bottomRightRect = new Rectangle2D.Double(bottomRight.getX() - 4.0,
									bottomRight.getY() - 4.0, 9.0, 9.0);
							if (bottomLeftRect.intersects(boxX, boxY, width, height)) {
								setCurrent = true;
							}

							if (bottomRightRect.intersects(boxX, boxY, width, height)) {
								setCurrent = true;
							}
						}
					}

					if (!setCurrent) {
						current = null;
					}
				}
			}
		});
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) { // to find current selected shape
				if (canDrag) {// for moving the shape
					if (current instanceof DLine) {
						((DLineModel) current.pointer).setXForLine(xMouse + e.getX());
						((DLineModel) current.pointer).setYForLine(yMouse + e.getY());
					}

					else {
						current.pointer.setX(xMouse + e.getX());
						current.pointer.setY(yMouse + e.getY());
					}
				}

				if (!canDrag && canResize) {// for resizing the shapes
					if (current instanceof DLine) {
						if (caseForResize == 0) // p1 is gonna move
						{
							resize(e.getPoint(), current.pointer, e.getX(), e.getY(), current.pointer.width,
									current.pointer.height);
						}

						if (caseForResize == 3) // p2 is gonna move
						{
							resize(e.getPoint(), current.pointer, current.pointer.x, current.pointer.y, e.getX(),
									e.getY());
						}

					} else {
						if (caseForResize == 0) {
							int mouseX = e.getX();
							int mouseY = e.getY();

							int xDelta = mouseX - clickPoint.x;
							int yDelta = mouseY - clickPoint.y;

							int shapeX = originalBounds.x + xDelta;
							int shapeY = originalBounds.y + yDelta;

							int shapeWidth = originalBounds.width - xDelta;
							int shapeHeight = originalBounds.height - yDelta;

							System.out.printf("%dx%dx%dx%d%n", shapeX, shapeY, shapeWidth, shapeHeight);

							resize(e.getPoint(), current.pointer, shapeX, shapeY, shapeWidth, shapeHeight);
						}

						if (caseForResize == 1) {
							int mouseX = e.getX();
							int mouseY = e.getY();

							int xDelta = mouseX - clickPoint.x;
							int yDelta = mouseY - clickPoint.y;

							int shapeX = originalBounds.x;
							int shapeY = originalBounds.y + yDelta; // LEAVE IT

							int shapeWidth = originalBounds.width + xDelta;
							int shapeHeight = originalBounds.height - yDelta; // LEAVE IT


							System.out.printf("%dx%dx%dx%d%n", shapeX, shapeY, shapeWidth, shapeHeight);
							resize(e.getPoint(), current.pointer, shapeX, shapeY, shapeWidth, shapeHeight);
						}

						if (caseForResize == 2) {
							int mouseX = e.getX();
							int mouseY = e.getY();

							int xDelta = mouseX - clickPoint.x;
							int yDelta = mouseY - clickPoint.y;

							int shapeX = originalBounds.x + xDelta;
							int shapeY = originalBounds.y;

							int shapeWidth = originalBounds.width - xDelta;
							int shapeHeight = originalBounds.height + yDelta;
							
							System.out.printf("%dx%dx%dx%d%n", shapeX, shapeY, shapeWidth, shapeHeight);
							resize(e.getPoint(), current.pointer, shapeX, shapeY, shapeWidth, shapeHeight);
						}

						if (caseForResize == 3) {
							int mouseX = e.getX();
							int mouseY = e.getY();

							int xDelta = mouseX - clickPoint.x;
							int yDelta = mouseY - clickPoint.y;

							Point p = new Point();
							p.x = mouseX - clickPoint.x;
							p.y = mouseY - clickPoint.y;

							int shapeX = originalBounds.x;
							int shapeY = originalBounds.y;

							int shapeWidth = originalBounds.width + xDelta;
							int shapeHeight = originalBounds.height + yDelta;

							System.out.printf("%dx%dx%dx%d%n", shapeX, shapeY, shapeWidth, shapeHeight);
							resize(e.getPoint(), current.pointer, shapeX, shapeY, shapeWidth, shapeHeight);
						}
					}
				}
			}
		});
	}

	@Override
	public void paintComponent(Graphics g)// loops through all the current shapes in arraylist and draw them
	{
		super.paintComponent(g);
		for (int i = 0; i < currentShapes.size(); i++) {
			currentShapes.get(i).draw(g);
		}

		if (this.current != null) {
			this.current.drawKnobs(g);
		}
		/*
		 * super.paintComponent(g); g.setColor(Color.BLACK); g.fillOval(100, 100, 200,
		 * 200); g.fillRect(0, 0, 200, 200);
		 */
	}

	public void addShape(DShapeModel d) {
		if (d instanceof DRectModel) {
			DRect rect = new DRect();
			rect.pointer = d;
			rect.setPointer(d);
			currentShapes.add(rect);
		}

		if (d instanceof DOvalModel) {
			DOval oval = new DOval();
			oval.pointer = d;
			oval.setPointer(d);
			currentShapes.add(oval);
		}

		if (d instanceof DLineModel) {
			DLine line = new DLine();
			line.pointer = (DLineModel) d;
			line.setPointer(d);
			currentShapes.add(line);
		}

		if (d instanceof DTextModel) {
			DText text = new DText(d);
			text.setPointer(d);
			currentShapes.add(text);
			// System.out.println(currentShapes.get(0).pointer.rectangle.x);
		}
		current = currentShapes.get(currentShapes.size() - 1);
		current.priority = currentShapes.size() - 1;
		currentShapes.get(currentShapes.size() - 1).priority = currentShapes.size() - 1;
		current.getModel().addModelListener((ModelListener) current);
		current.getModel().addModelListener((ModelListener) this.shapeTable);
		current.getModel().addModelListener((ModelListener) this);
		current.setCanvas(this);
		this.repaint();
		this.shapeTable.fireTableDataChanged();

		repaint();
	}

	public void setCurrentShape(DShape c) {
		current = c;
		repaint();
	}

	public DShape getSelectedShape() {
		return current;
	}

	public void resize(Point point, DShapeModel model, int x, int y, int width, int height) {
		if (model instanceof DTextModel) {
			if (width < 0) {
				model.setX(model.getX());
				model.setWidth(0);
			} else {
				model.setX(x);
				model.setWidth(width);
			}
			if (height < 0) {
				model.setY(model.getY());
				model.setHeight(0);
			} else {
				model.setY(y);
				model.setHeight(height);
			}
		}

		if (model instanceof DLineModel) {			
				model.setX(x);
				model.setWidth(width);

				model.setY(y);
				model.setHeight(height);
		}

		else {
			if (width < 0) {
				model.setX(x - Math.abs(width));
				model.setWidth(Math.abs((int) (this.anchorKnob.getX() - (double) point.x)));
			} else { // square if it is resized positively
				model.setX(x);
				model.setWidth(width);
			}
			if (height < 0) {
				model.setY(y - Math.abs(height));
				model.setHeight(Math.abs((int) (this.anchorKnob.getY() - (double) point.y)));
			} else {
				model.setY(y);
				model.setHeight(height);
			}
		}
	}

	public void deleteAllShapes() {
		for (DShape shape : this.currentShapes) {
			shape.getModel().deleteAllListeners();
		}
		this.currentShapes.clear();
		this.setCurrentShape(null);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void deleteShape(DShape shape, int id) {
		if (shape == null) {
			return;
		}
		shape.getModel().deleteAllListeners();
		this.currentShapes.removeIf(e -> id == e.getModel().getId());
		this.setCurrentShape(null);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void deleteSelectedShape() {
		if (this.current == null) {
			return;
		}
		this.current.getModel().deleteAllListeners();
		this.currentShapes.remove((Object) this.current);
		this.setCurrentShape(null);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void sendSelectedToFront(DShape shape, int id) {
		this.currentShapes.removeIf(e -> id == e.getModel().getId());
		this.currentShapes.add(shape);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void sendSelectedToFront() {
		this.currentShapes.remove((Object) this.current);
		this.currentShapes.add(this.current);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void sendSelectedToBack(DShape shape, int id) {
		this.currentShapes.removeIf(e -> id == e.getModel().getId());
		this.currentShapes.add(0, shape);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public void sendSelectedToBack() {
		this.currentShapes.remove((Object) this.current);
		this.currentShapes.add(0, this.current);
		this.repaint();
		this.shapeTable.fireTableDataChanged();
	}

	public ArrayList<DShapeModel> getShapeModelArray() {
		ArrayList<DShapeModel> tempArray = new ArrayList<DShapeModel>();
		for (DShape d : this.currentShapes) {
			tempArray.add(d.getModel());
		}
		return tempArray;
	}

	public Table getTable() {
		return this.shapeTable;
	}

	public void modelChanged(DShapeModel model) {
		if (this.serverOn) {
			this.server.notifyAll("change", model);
		}
	}

	static /* synthetic */ void access$0(Canvas canvas, int n) {
		canvas.selectedX = n;
	}

	static /* synthetic */ void access$1(Canvas canvas, int n) {
		canvas.selectedY = n;
	}

	static /* synthetic */ DShape access$2(Canvas canvas) {
		return canvas.current;
	}

	static /* synthetic */ void access$3(Canvas canvas, Point point) {
		canvas.movingKnob = point;
	}

	static /* synthetic */ Point access$4(Canvas canvas) {
		return canvas.movingKnob;
	}

	static /* synthetic */ void access$5(Canvas canvas, Point point) {
		canvas.anchorKnob = point;
	}

	static /* synthetic */ ArrayList access$6(Canvas canvas) {
		return canvas.currentShapes;
	}

	static /* synthetic */ int access$7(Canvas canvas) {
		return canvas.selectedX;
	}

	static /* synthetic */ int access$8(Canvas canvas) {
		return canvas.selectedY;
	}

	static /* synthetic */ boolean access$9(Canvas canvas) {
		return canvas.clientMode;
	}

	static /* synthetic */ Point access$10(Canvas canvas) {
		return canvas.anchorKnob;
	}

	static /* synthetic */ Canvas access$11(Canvas canvas) {
		return canvas.canvas;
	}
}
