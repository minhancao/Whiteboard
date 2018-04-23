import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPServer
implements Runnable,
ModelListener {
    static int counter = 2;
    protected ServerSocket serverSocket;
    private ArrayList<Socket> clientList = null;
    protected int myPort;
    Canvas canvas = null;

    public void decodeAll() {
        for (Socket sock : this.clientList) {
            try {
                try {
                    TCPClient.decode(sock.getInputStream());
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addClient(Socket s) {
        this.clientList.add(s);
    }

    public TCPServer(int port, Canvas c) {
        try {
            this.clientList = new ArrayList();
            this.myPort = port;
            this.serverSocket = new ServerSocket(this.myPort);
            this.canvas = c;
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            do {
                Socket clientSocket = this.serverSocket.accept();
                this.addClient(clientSocket);
                this.originalNotification(clientSocket, this.canvas.getShapeModelArray());
            } while (true);
        }
        catch (IOException ioe) {
            System.err.println("Failed to accept socket, " + ioe);
            System.exit(1);
            return;
        }
    }

    public void notifyAll(String action, DShapeModel a) {
        for (Socket sock : this.clientList) {
            this.notify(sock, action, a);
        }
    }

    public void loadNotification(ArrayList<DShapeModel> ar) {
        for (Socket s : this.clientList) {
            this.notify(s, "deleteShapes", new DRectModel());
            for (DShapeModel f : ar) {
                this.notify(s, "add", f);
            }
        }
    }

    public void originalNotification(Socket s, ArrayList<DShapeModel> ar) {
        for (DShapeModel f : ar) {
            this.notify(s, "add", f);
        }
    }

    public void notify(Socket sock, String action, DShapeModel a) {
        if (a.getId() == 0) {
            a.setId(counter++);
        }
        a.remColors();
        ObjectOutputStream os = null;
        try {
            ByteArrayOutputStream memStream = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(memStream);
            encoder.writeObject(action);
            encoder.writeObject(a);
            encoder.close();
            String res = memStream.toString();
            os = new ObjectOutputStream(sock.getOutputStream());
            os.writeObject(res);
            os.flush();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void modelChanged(DShapeModel model) {
        this.notifyAll("changed", model);
    }
}