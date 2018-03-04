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

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Server implements Serializable {
    private Map<String, Client> users = Collections.synchronizedMap(new HashMap<>());

    private static Server ourInstance = new Server();

    public static Server getInstance() {
        return ourInstance;
    }

    private Server() {
    }

    // Method for a user to register in Server ("Donar-se d'alta al servidor")
    public boolean register(Client c) {
        if (!users.containsKey(c.getUser())) {
            users.put(c.getUser(), c);
            return true;
        } else
            return false;
    }

    // Method for a user to unregister in Server ("Donar-se de baixa al servidor")
    public boolean unregister(Client c) {
        if (users.containsKey(c.getUser())) {
            users.remove(c.getUser());
            return true;
        } else
            return false;
    }

    public boolean loging(Client c) {
        return (users.containsKey(c.getUser()) && users.get(c.getUser()).equals(c));

    }

    public boolean verifyUser(String name){
        return (users.containsKey(name));
    }

    public List<String> showUsers() {
        List<String> result = Collections.synchronizedList(new LinkedList<>());
        for (String s : users.keySet())
            result.add(s);
        return result;
    }

    public void addMessage(Message m) {
        users.get(m.getDestination()).send(m);
    }

    public void addMessage(Message m, List<Client> lc) {
        synchronized (lc){
            for (Client c: lc) {
                users.get(c.getUser()).send(m);
            }
        }
    }

    public List<Message> getMessages(String name) {
        return Collections.synchronizedList(users.get(name).getMessages().stream().collect(Collectors.toList()));
    }

    public List<Message> getMessages(String name, int numb) {
        // Esto devuelve los últimos numb mensajes; si no tuviese, devolvera entre 0 y el máximo que tenga ese momento
        return Collections.synchronizedList(users.get(name).getMessages().stream().skip(Math.max(0, users.get(name).getMessages().size() - numb)).collect(Collectors.toList()));
    }

    public void updateMessages(String name, List<Message> messages) {
        users.get(name).setReadMessages(messages);
    }

    public Map<String, Client> getUsers() {
        return users;
    }
}
