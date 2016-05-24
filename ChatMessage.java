package utils;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class ChatMessage implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String username;
    private LocalDateTime dateTime;
    private String message;

    public ChatMessage(String username) {
        
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setDate() {
        
        this.dateTime = LocalDateTime.now();
    }

    public String getDate() {
        
        try {
            return CalendarUtils.dateToString(this.dateTime, Consts.dateFormat);
        } catch (ParseException ex) {
            Logger.getLogger(ChatMessage.class.getName()).log(Level.SEVERE, null, ex);
            return "[ERR] DateError";
        }
    }

    public String getMessage() {
        
        return message;
    }

    public void setMessage(String message) {
        
        this.message = message;
    }
}
