/*
---------------------------------------------
  VERY SIMPLE MAIL TRANSFER PROTOCOL (VSMTP)
---------------------------------------------
    Subject: Xarxa de Dades (Data Network)
    Authors: Enyu Lin
             Marc Bové
             Dani Díez
    Date:    December/2017
---------------------------------------------
 */
package Types;

import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isRead;
    private String content;
    private String from;
    private String destination;

    public Message(String content, String from, String destination) {
        this.content = content;
        this.from = from;
        this.destination = destination;
        isRead = false;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getContent() {
        return content;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString()
    {
        return "\n Message: \t " + content + ".\n"
                + " From: \t " + from + ".\n Destination: \t " + destination
                + " \n Was Read: \t " + isRead + "\n";
    }
}
