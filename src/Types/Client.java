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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Client implements Serializable {
    private String user;
    private String password;
    private List<Message> messages = Collections.synchronizedList(new LinkedList<>());
    private List<Client> group = Collections.synchronizedList(new LinkedList<>());

    public String getPassword() {
        return password;
    }

    public String getUser() {
        return user;
    }

    public Client(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void send(Message m) {
        messages.add(m);
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setReadMessages(List<Message> messages) {
        int index;
        for (Message m : messages) {
            if (this.messages.contains(m)) {
                index = this.messages.indexOf(m);
                this.messages.get(index).setRead(true);
            }
        }
    }

    @Override
    public String toString() {
        return "Client: " + user + ".\n";
    }

    public boolean equals(Client c) {
        return (c.getUser().equals(getUser()) && c.getPassword().equals(getPassword()));
    }

    public List<Client> getGroup() {
        return group;
    }
}
