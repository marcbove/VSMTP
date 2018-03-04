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
            Attend Petition
---------------------------------------------
 */
package Types;

public class ShortMessage extends Message
{
    public ShortMessage(String content, String from, String destination) {
        super(content, from, destination);
    }

    public String toString() {
        return ("\n"+super.getContent());
    }
}
