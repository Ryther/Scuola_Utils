package utils.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class StreamHandler {
    
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    
    public StreamHandler (Socket socket) {
        
        this.socket = socket;
    }
    
    public boolean init() {
        
        try {
            
            this.objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.objectOutputStream.flush();
        } catch (IOException ex) { 
            
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[Socket non inizializzato]", ex);
            return false;
        }
        
        try {
            
            this.objectInputStream = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException ex) { 
            
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, "[Socket non inizializzato]", ex);
            return false;
        }
        
        return true;
    }
    
    public <T extends Serializable> void pushToStream(T target) {
        
        try {
            this.objectOutputStream.writeObject(target);
            this.objectOutputStream.flush();
            this.objectOutputStream.reset();
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
    
    public boolean closeStream() {
        
        try {
            this.objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try {
            objectInputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(StreamHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
}
