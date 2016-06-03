/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryther
 */
public class DataHandler {
    
    public static ByteBuffer objectToByteBuffer (Object object) {
        
        byte[] objectBytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = null;
        try {
            objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject(object);
            objectBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(DataHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objectOutput.close();
                byteArrayOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(DataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ByteBuffer result = ByteBuffer.wrap(objectBytes);
        return result;
    }
    
    public static Object byteBufferToObject (ByteBuffer byteBuffer) throws ClassNotFoundException {
        
        byte[] message = byteBuffer.array();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
        ObjectInput objectInput = null;
        Object result = null;
        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            result = objectInput.readObject();
        } catch (IOException ex) {
            Logger.getLogger(DataHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                objectInput.close();
                byteArrayInputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(DataHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return result;
    }
}
