package utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class SocketHandler {
    
    private Socket socket;
    private InetAddress inetAddress;
    private final int port;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    
    public SocketHandler(String address, int port) {
        
        try {
            this.inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.port = port;
    }
    
    public boolean init() {
        
        try {
            this.socket = new Socket(this.inetAddress, this.port);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            
            objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.flush();
        } catch (IOException ex) { 
            
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[Socket non inizializzato]", ex);
            return false;
        }
        
        try {
            
            objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ex) { 
            
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[Socket non inizializzato]", ex);
            return false;
        }
        
        return true;
    }
    
    public <T extends Serializable> void pushToStream(T target) {
        
        try {
            this.objectOutputStream.writeObject(target);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Object pullFromStream() {        
        try {
            return this.objectInputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
