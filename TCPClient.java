/*
 * Decompiled with CFR 0_123.
 */
import java.beans.XMLDecoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;

public class TCPClient
implements Runnable {
    static Canvas canvas = null;
    Socket s = null;
    protected BufferedReader stdin;
    protected PrintWriter stdout;
    protected PrintWriter stderr;

    public TCPClient(String host, int port, Canvas canvas) throws Exception {
        TCPClient.canvas = canvas;
        this.s = new Socket(host, port);
        this.stdin = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
    }

    public Canvas getCanvas() {
        return canvas;
    }

    @Override
    public void run() {
        do {
            try {
                do {
                    if (this.s == null) {
                        continue;
                    }
                    TCPClient.decode(this.s.getInputStream());
                } while (true);
            }
            catch (IOException e) {
                System.out.println("Connection faled!");
                continue;
            }
            catch (ClassNotFoundException e) {
                System.out.println("Connection faled!");
                continue;
            }
        } while (true);
    }

    public static void decode(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(inputStream);
        String xmlString = (String)in.readObject();
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
        String action = (String)decoder.readObject();
        DShapeModel mod = (DShapeModel)decoder.readObject();
        mod.setColorRGB();
        decoder.close();
        canvas.handleChanges(action, mod);
    }
}