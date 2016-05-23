package utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class SocketHandler {
    
    public enum Type {
        CLIENT, SERVER
    }
    
    private Type type;
    private Socket socket;
    private ServerSocket serverSocket;
    private InetAddress inetAddress;
    private final int port;
    
    public SocketHandler(InetAddress inetAddress, int port) {
        
        this.inetAddress = inetAddress;
        this.port = port;
        
        try {
            this.socket = new Socket(this.inetAddress, this.port);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.type = Type.CLIENT;
    }
    
    public SocketHandler(int port) {
        
        this.port = port;
        
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.type = Type.SERVER;
    }

    public Socket getSocket() {
        
        return socket;
    }
    
    public boolean acceptTimeout(int time) {
        
        try {
            this.serverSocket.setSoTimeout(time);
        } catch (SocketException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[SocketHandler deve essere di tipo server]");
            return false;
        }
        return true;
    }
    
    public boolean accept() {
        
        if (this.type.equals(Type.SERVER)) {
            
            try {
                this.socket = this.serverSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[SocketHandler deve essere di tipo server]");
            return false;
        }
        
        return true;
    }
    
    public boolean accept(int time) {
        
        if (!this.acceptTimeout(time)) {
            return false;
        }
        
        if (this.accept()) {
            this.acceptTimeout(0);
            return true;
        } else {
            return false;
        }
    }
}
