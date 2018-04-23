import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class Whiteboard extends JFrame {
	boolean serverOn = false;
	TCPServer server = null;
	private Canvas canvas;
	private ArrayList<Container> boxes = new ArrayList<Container>(0);
	protected JTextField inputField;
	protected JComboBox<String> fontPicker;
	protected Font font;

	public Whiteboard() {
	}

	public void enableTextButtons() {
		this.inputField.setEnabled(true);
		this.fontPicker.setEnabled(true);
	}

	public void disableTextButtons() {
		this.inputField.setEnabled(false);
		this.fontPicker.setEnabled(false);
	}
	
	public void disableAllButtons() {
        for (Container b : this.boxes) {
            Component[] arrcomponent = b.getComponents();
            int n = arrcomponent.length;
            int n2 = 0;
            while (n2 < n) {
                Component c = arrcomponent[n2];
                c.setEnabled(false);
                ++n2;
            }
        }
    }

	public static void main(String[] args) {
		Whiteboard whiteBoardFrame = new Whiteboard(); // will hold all the components
		whiteBoardFrame.setTitle("Whiteboard");
		whiteBoardFrame.setPreferredSize(new Dimension(1000, 600));
		whiteBoardFrame.setLayout(new BorderLayout());

		whiteBoardFrame.canvas = new Canvas(whiteBoardFrame);
		whiteBoardFrame.canvas.setBackground(Color.WHITE);
		whiteBoardFrame.canvas.setSize(new Dimension(400, 400));

		whiteBoardFrame.add(whiteBoardFrame.canvas, BorderLayout.CENTER); // sets canvas to center of whiteboard

		JLabel label = new JLabel("Add");
		JButton addRect = new JButton("Rect");
		JButton addOval = new JButton("Oval");
		JButton addLine = new JButton("Line");
		JButton addText = new JButton("Text");

		addRect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whiteBoardFrame.disableTextButtons();
				DRectModel rect = new DRectModel();
				Rectangle r = new Rectangle();
				r.height = 20;
				r.width = 20;
				r.x = 10;
				r.y = 10;
				rect.setRectangle(r);
				whiteBoardFrame.canvas.addShape(rect);
				whiteBoardFrame.disableTextButtons();
	            if (whiteBoardFrame.serverOn) {
	            	whiteBoardFrame.server.notifyAll("add", rect);
	            }
			}
		});

		addOval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whiteBoardFrame.disableTextButtons();
				DOvalModel oval = new DOvalModel();
				Rectangle r = new Rectangle();
				r.height = 20;
				r.width = 20;
				r.x = 10;
				r.y = 10;
				oval.setRectangle(r);
				whiteBoardFrame.canvas.addShape(oval);
				whiteBoardFrame.disableTextButtons();
	            if (whiteBoardFrame.serverOn) {
	            	whiteBoardFrame.server.notifyAll("add", oval);
	            }
			}
		});

		addLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whiteBoardFrame.disableTextButtons();
				DLineModel line = new DLineModel();
				Rectangle r = new Rectangle();
				r.height = 40;
				r.width = 40;
				r.x = 10;
				r.y = 10;
				line.setRectangle(r);
				whiteBoardFrame.canvas.addShape(line);
				whiteBoardFrame.disableTextButtons();
	            if (whiteBoardFrame.serverOn) {
	            	whiteBoardFrame.server.notifyAll("add", line);
	            }
			}
		});

		addText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				whiteBoardFrame.enableTextButtons();
				DTextModel text = new DTextModel();
				Rectangle r = new Rectangle();
				r.height = 20;
				r.width = 20;
				r.x = 10;
				r.y = 10;
				text.setRectangle(r);
				text.setText("Hello");
				text.setFontName("Dialog");
				whiteBoardFrame.canvas.addShape(text);
	            if (whiteBoardFrame.serverOn) {
	            	whiteBoardFrame.server.notifyAll("add", text);
	            }
			}
		});

		whiteBoardFrame.inputField = new JTextField("Hello");
		whiteBoardFrame.inputField.setPreferredSize(new Dimension(100, 14));
		String[] fontList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		whiteBoardFrame.fontPicker = new JComboBox<String>(fontList);
		whiteBoardFrame.fontPicker.setPreferredSize(new Dimension(150, 20));
		whiteBoardFrame.inputField.setEnabled(false);
		whiteBoardFrame.fontPicker.setEnabled(false);
		whiteBoardFrame.fontPicker.addActionListener(e -> {
			if (whiteBoardFrame.canvas.getSelectedShape() == null) {
				return;
			}
			if (whiteBoardFrame.canvas.getSelectedShape() instanceof DText) {
				DTextModel model = (DTextModel) whiteBoardFrame.canvas.getSelectedShape().getModel();
				model.setFontName((String) whiteBoardFrame.fontPicker.getSelectedItem());
				int index = whiteBoardFrame.fontPicker.getSelectedIndex();
				model.setIndex(index);
				whiteBoardFrame.canvas.repaint();
			}
		});

		whiteBoardFrame.inputField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				if (whiteBoardFrame.canvas.getSelectedShape() != null
						&& whiteBoardFrame.canvas.getSelectedShape() instanceof DText) {
					((DTextModel) whiteBoardFrame.canvas.getSelectedShape().getModel())
							.setText(whiteBoardFrame.inputField.getText());
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
			}

		});

		Box boxEditText = Box.createHorizontalBox();
		boxEditText.add(whiteBoardFrame.inputField);
		boxEditText.add(whiteBoardFrame.fontPicker);
		boxEditText.setMaximumSize(new Dimension(500, 30));

		JButton setColor = new JButton("Set Color");
		setColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (whiteBoardFrame.canvas.current != null) {
					Color newColor = JColorChooser.showDialog(null, "Choose a color",
							whiteBoardFrame.canvas.current.pointer.color);
					if (newColor == null) {
						newColor = whiteBoardFrame.canvas.current.pointer.color;
					}
					whiteBoardFrame.canvas.current.getModel().setColor(newColor);
				}
			}
		});
		Box box1 = Box.createHorizontalBox();
		box1.add(setColor);

		JButton deleteShape = new JButton("Delete Shape");
		deleteShape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (whiteBoardFrame.canvas.current != null) {
					if (whiteBoardFrame.serverOn) {
						whiteBoardFrame.server.notifyAll("remove", whiteBoardFrame.canvas.getSelectedShape().getModel());
		            }
					whiteBoardFrame.canvas.deleteSelectedShape();
				}
			}
		});
		JButton moveFront = new JButton("Move Front");
		moveFront.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (whiteBoardFrame.canvas.current != null) {
					whiteBoardFrame.canvas.sendSelectedToFront();
					if (whiteBoardFrame.serverOn) {
						whiteBoardFrame.server.notifyAll("front", whiteBoardFrame.canvas.getSelectedShape().getModel());
		            }
				}
			}
		});
		JButton moveBack = new JButton("Move Back");
		moveBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (whiteBoardFrame.canvas.current != null) {
					whiteBoardFrame.canvas.sendSelectedToBack();
					if (whiteBoardFrame.serverOn) {
						whiteBoardFrame.server.notifyAll("back", whiteBoardFrame.canvas.getSelectedShape().getModel());
		            }
				}
			}
		});
		Box boxEdits = Box.createHorizontalBox();
		boxEdits.add(deleteShape);
		boxEdits.add(moveFront);
		boxEdits.add(moveBack);
		boxEdits.setPreferredSize(new Dimension(600, 60));

		Box vBox = Box.createVerticalBox(); // will hold every hbox

		Box hBox = Box.createHorizontalBox();
		hBox.add(label);
		hBox.add(addRect);
		hBox.add(addOval);
		hBox.add(addLine);
		hBox.add(addText);

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> {
			String path = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText("Save");
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int returnVal = fileChooser.showSaveDialog(whiteBoardFrame);
			if (returnVal == 0) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
			}
			try {
				whiteBoardFrame.canvas.save(path);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(e -> {
			String path = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText("Save");
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int returnVal = fileChooser.showSaveDialog(whiteBoardFrame);
			if (returnVal == 0) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
			}
			whiteBoardFrame.canvas.load(path);
		});

		JButton saveAsPNG = new JButton("Save as PNG");
		saveAsPNG.addActionListener(e -> {
			String path = null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText("Save");
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			int returnVal = fileChooser.showSaveDialog(whiteBoardFrame);
			if (returnVal == 0) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
			}
			if (path != null) {
				if (!path.endsWith(".png")) {
					path = String.valueOf(path) + ".png";
				}
				whiteBoardFrame.canvas.saveImage(path, "png");
			}
		});
		Box hBox2 = Box.createHorizontalBox();
		hBox2.setPreferredSize(new Dimension(0, 40));
		hBox2.add(saveButton);
		hBox2.add(loadButton);
		hBox2.add(saveAsPNG);
		
		JLabel statusLabel = new JLabel("");
		JButton startServer = new JButton("Start Server");
		JButton startClient = new JButton("Start Client");
		startServer.addActionListener(e -> {
            Integer s = 39587;
            String result = JOptionPane.showInputDialog(whiteBoardFrame, (Object)"Enter ip and port number you want to use (default is 39587):");
            System.out.println(result);
            if (result != null) {
                if (result.equals("")) {
                    whiteBoardFrame.serverOn = true;
                } else {
                    try {
                        s = Integer.parseInt(result);
                        if (s < 65536) {
                        	whiteBoardFrame.serverOn = true;
                        }
                    }
                    catch (Exception e1) {
                        System.out.println("bad");
                    }
                }
                whiteBoardFrame.server = new TCPServer(s, whiteBoardFrame.canvas);
                whiteBoardFrame.canvas.setServer(whiteBoardFrame.server);
                Thread t = new Thread(whiteBoardFrame.server);
                t.start();
            }
            if (whiteBoardFrame.serverOn) {
            	startServer.setEnabled(false);
                startClient.setEnabled(false);
                statusLabel.setText(" Server mode, port " + s);
            }
        }
        );
		startClient.addActionListener(e -> {
            Integer i = 39587;
            String host = "localhost";
            String result = JOptionPane.showInputDialog(whiteBoardFrame, (Object)"Enter port number you want to use (default is 39587):");
            if (result != null) {
                try {
                    if (!result.equals("")) {
                        String[] ar = result.split(":");
                        if (!ar[0].equals("")) {
                            host = ar[0];
                        }
                        if (!ar[1].equals("")) {
                            i = Integer.parseInt(ar[1]);
                        }
                    }
                    if (i < 65536) {
                        statusLabel.setText("Client mode, port " + i);
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    TCPClient client = new TCPClient(host, i, whiteBoardFrame.canvas);
                    whiteBoardFrame.canvas.deleteAllShapes();
                    whiteBoardFrame.canvas.turnOnClientMode();
                    whiteBoardFrame.disableAllButtons();
                    Thread t = new Thread(client);
                    t.start();
                }
                catch (UnknownHostException exception) {
                    statusLabel.setText("Server connection failed!");
                }
                catch (IOException exception) {
                    statusLabel.setText("Server connection failed!");
                }
                catch (Exception exception) {
                    System.out.println("Something went wrong!");
                }
            }
        }
        );
		
		Box hBox3 = Box.createHorizontalBox();
		hBox3.add(startServer);
		hBox3.add(startClient);
		hBox3.add(statusLabel);

		JScrollPane tableScrollPane = new JScrollPane(whiteBoardFrame.getTable());

		whiteBoardFrame.boxes.add(hBox);
		whiteBoardFrame.boxes.add(boxEditText);
		whiteBoardFrame.boxes.add(box1);
		whiteBoardFrame.boxes.add(boxEdits);
		whiteBoardFrame.boxes.add(hBox2);
		whiteBoardFrame.boxes.add(hBox3);
		whiteBoardFrame.boxes.add(tableScrollPane);
		
		vBox.add(hBox);
		vBox.add(Box.createRigidArea(new Dimension(5, 20)));
		vBox.add(boxEditText);
		vBox.add(Box.createRigidArea(new Dimension(5, 20)));
		vBox.add(box1);
		vBox.add(Box.createRigidArea(new Dimension(5, 10)));
		vBox.add(boxEdits);
		vBox.add(Box.createRigidArea(new Dimension(5, 0)));
		vBox.add(hBox2);
		vBox.add(Box.createRigidArea(new Dimension(5, 20)));
		vBox.add(hBox3);
		vBox.add(Box.createRigidArea(new Dimension(5, 20)));
		vBox.add(tableScrollPane);

		whiteBoardFrame.add(vBox, BorderLayout.WEST);

		for (Component comp : vBox.getComponents()) {
			((JComponent) comp).setAlignmentX(Box.LEFT_ALIGNMENT);
		}
		whiteBoardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		whiteBoardFrame.pack();
		whiteBoardFrame.setVisible(true);

	}

	private JTable getTable() {
		JTable table = new JTable(this.canvas.getTable());
		table.setFillsViewportHeight(true);
		return table;
	}
}