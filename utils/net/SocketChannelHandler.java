package utils.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.data.DataHandler;

/**
 *
 * @author Edoardo Zanoni
 */
public class SocketChannelHandler {
    
    public enum Type {
        CLIENT, SERVER
    }
    
    private Type type;
    private Selector selector;
    private SocketChannel socketChannel;
    private ServerSocketChannel serverSocketChannel;
    private InetSocketAddress inetSocketAddress;
    
    public SocketChannelHandler(SocketChannel socketChannel) {
        
        this.socketChannel = socketChannel;
        try {
            this.inetSocketAddress = (InetSocketAddress) this.socketChannel.getLocalAddress();
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile ottenere IP del client", ex);
        }
    }
    
    public SocketChannelHandler(InetAddress inetAddress, int port) {
        
        this.inetSocketAddress = new InetSocketAddress(inetAddress, port);
        
        try {
            this.socketChannel = SocketChannel.open(this.inetSocketAddress);
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile aprire il SocketChannel", ex);
        }
        
        this.type = Type.CLIENT;
    }
    
    public SocketChannelHandler(int port) {
        
        try {
            this.selector = Selector.open();
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile aprire il Selector", ex);
        }
                
        this.inetSocketAddress = new InetSocketAddress(port);
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile aprire il ServerSocketChannel", ex);
        }
        
        try {
            this.serverSocketChannel.bind(this.inetSocketAddress);
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile agganciare l'InetSocketAddress al ServerSocketChannel", ex);
        }
        
        try {
            this.serverSocketChannel.configureBlocking(false);
        } catch (IOException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile configurare il blocking del ServerSocketChannel", ex);
        }
        
        int operations = this.serverSocketChannel.validOps();
        try {
            this.serverSocketChannel.register(this.selector, operations, null);
        } catch (ClosedChannelException ex) {
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile registrare questo ServerSocketChannel", ex);
        }
        
        this.type = Type.SERVER;
    }

    public Selector getSelector() {
        
        return this.selector;
    }
    
    public SocketChannel getSocket() {
        
        return this.socketChannel;
    }

    public ServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }
    
    public Set<SelectionKey> select() {
        
        if (this.type.equals(Type.SERVER)) {
            
            try {
                this.selector.select();
                return this.selector.selectedKeys();
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile trovare una chiave selezionabile", ex);
                return null;
            }
        } else {
            
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Tipologia di Handler errata (Type.{0}), verificare costruttore", this.type);
            return null;
        }
    }
    public boolean accept() {
        
        if (this.type.equals(Type.SERVER)) {
            
            SocketChannel tempSocketChannel = null;
            try {
                tempSocketChannel = this.serverSocketChannel.accept();
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile accettare la connessione", ex);
                return false;
            }

            try {
                tempSocketChannel.configureBlocking(false);
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile configurare il blocking del ServerSocketChannel", ex);
                return false;
            }

            try {
                tempSocketChannel.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE);
            } catch (ClosedChannelException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile registrare il client al Selector", ex);
                return false;
            }
        }
        
        return true;
    }
        
    public void pushToChannel(Object target) {
        
        if (this.type.equals(Type.CLIENT)) {
            
            ByteBuffer buffer = DataHandler.objectToByteBuffer(target);

            try {
                this.socketChannel.write(buffer);
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile scrivere sul SocketChannel", ex);
            }

            buffer.clear();
        } else {
            
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Tipologia di Handler errata (Type.{0}), verificare costruttore", this.type);
        }
    }
    
    public void pushToChannel(SelectionKey selectedKey, Object target) {
        
        if (this.type.equals(Type.SERVER)) {
        
            SocketChannel tempSocketChannel = (SocketChannel) selectedKey.channel();
            ByteBuffer buffer = DataHandler.objectToByteBuffer(target);

            try {
                tempSocketChannel.write(buffer);
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile scrivere sul SocketChannel", ex);
            }

            buffer.clear();
        } else {
            
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Tipologia di Handler errata (Type.{0}), verificare costruttore", this.type);
        }
    }
    
    public Object pullFromChannel() {        
        
        if (this.type.equals(Type.CLIENT)) {
            
            ByteBuffer buffer = ByteBuffer.allocate(2048);

            try {
                this.socketChannel.read(buffer);
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile leggere dal SocketChannel", ex);
            }

            try {
                return DataHandler.byteBufferToObject(buffer);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "I dati ricevuti non fanno parte di un oggetto", ex);
                return null;
            }
        } else {
            
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Tipologia di Handler errata (Type.{0}), verificare costruttore", this.type);
            return null;
        }
    }
    
    public Object pullFromChannel(SelectionKey selectedKey) {        
        
        if (this.type.equals(Type.SERVER)) {
            
            SocketChannel tempSocketChannel = (SocketChannel) selectedKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(2048);

            try {
                tempSocketChannel.read(buffer);
            } catch (IOException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile leggere dal SocketChannel", ex);
            }

            try {
                return DataHandler.byteBufferToObject(buffer);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "I dati ricevuti non fanno parte di un oggetto", ex);
                return null;
            }
        } else {
            
            Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Tipologia di Handler errata (Type.{0}), verificare costruttore", this.type);
            return null;
        }
    }
    
    public void close() {
        
        switch(this.type) {
            case CLIENT:        
                try {
                    this.socketChannel.close();
                } catch (IOException ex) {
                    Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile chiudere il SocketChannel", ex);
                }
                break;
            case SERVER:
                try {
                    this.selector.close();
                } catch (IOException ex) {
                    Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile chiudere il selector", ex);
                }
        
                try {
                    this.serverSocketChannel.close();
                } catch (IOException ex) {
                    Logger.getLogger(SocketChannelHandler.class.getName()).log(Level.SEVERE, "Impossibile chiudere il ServerSocketChannel", ex);
                }
                break;
        }
    }
}
