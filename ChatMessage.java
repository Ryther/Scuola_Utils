package utils;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edoardo Zanoni
 */
public class ChatMessage implements Serializable {
    
    private String username;
    private LocalDate date;
    private String message;

    public ChatMessage(String username) {
        
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setDate() {
        
        this.date = LocalDate.now();
    }

    public String getDate() {
        
        try {
            return CalendarUtils.dateToString(this.date, Consts.dateFormat);
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
