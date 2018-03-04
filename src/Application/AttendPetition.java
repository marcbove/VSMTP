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
package Application;

import Types.*;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AttendPetition extends Thread {
    private Socket socket;
    private Server server;
    final int numOfPermits = 2;
    Semaphore semafor = new Semaphore(numOfPermits, true);


    public AttendPetition(Socket s) {
        this.socket = s;
        this.server = server.getInstance();
    }

    public void run() {
        try {

            Message message;
            int option;

            ObjectInputStream inFromClient = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream outToClient = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                option = (Integer) inFromClient.readObject(); // punt bloquejant, server esperant q li envia alguna cosa;
                System.out.println("option: " + option);
                //---------------------
                switch (option) {
                    case 1:
                        // Registre d'un nou usuari
                        Client client;
                        client = (Client) inFromClient.readObject();
                        System.out.println("Received registration -> " + client);

                        semafor.acquire();
                        boolean isRegOK = server.register(client);
                        semafor.release();

                        String registContent = (isRegOK) ? "Registration accepted!" : "Registration failed! Name existed in the Server.\n Try a new name...";
                        Message okRegister = new ShortMessage(registContent, "Server", client.getUser());
                        outToClient.writeObject(okRegister);
                        break;
                    case 2:
                        // Login de l'usuari
                        Client clientLog;
                        clientLog = (Client) inFromClient.readObject();
                        System.out.println("Received loging -> " + clientLog);

                        semafor.acquire();
                        boolean isLogOK = server.loging(clientLog);
                        semafor.release();

                        String logContent = (isLogOK) ? "Login accepted!" : "Authentication error!";
                        outToClient.writeObject(isLogOK);
                        Message okLoging = new ShortMessage(logContent, "Server", clientLog.getUser());
                        outToClient.writeObject(okLoging);
                        break;
                    case 3:
                        // El server rep un missatge d'un client
                        semafor.acquire();
                        List<String> clients = server.showUsers();
                        semafor.release();

                        int opt = (int)inFromClient.readObject();
                        if(opt == 1)
                        {
                            outToClient.writeObject(clients);
                            String dest = (String)inFromClient.readObject();
                            boolean okDest = server.verifyUser(dest);
                            outToClient.writeObject(okDest);

                            if (okDest) {
                                message = (Message) inFromClient.readObject();
                                System.out.println("Received message ->  " + message.getContent());
                                semafor.acquire();
                                server.addMessage(message);
                                semafor.release();
                            }
                        }
                        else
                        {
                            outToClient.writeObject(clients);
                            client = (Client) inFromClient.readObject();
                            do {
                                String dest = (String)inFromClient.readObject();
                                boolean okDest = server.verifyUser(dest);
                                outToClient.writeObject(okDest);
                                if (!okDest)
                                    System.out.println("User unavailable!");
                                else {
                                    Client c = server.getUsers().get(dest);
                                    client.getGroup().add(c);
                                }
                                opt = (int) inFromClient.readObject();
                            } while(opt != 2);

                            message = (Message) inFromClient.readObject();
                            System.out.println("Received message ->  " + message.getContent());
                            System.out.println("\nGroup created -> " + client.getGroup());

                            semafor.acquire();
                            server.addMessage(message, client.getGroup());
                            semafor.release();
                        }
                        break;
                    case 4:
                        // El server envia els 10 últims missatges al client
                        opt = (int) inFromClient.readObject();
                        String name = (String) inFromClient.readObject();
                        int number = (int) inFromClient.readObject();
                        List<Message> msgNoReads;

                        semafor.acquire();
                        if (opt == 1)
                            msgNoReads = server.getMessages(name);
                        else
                            msgNoReads = server.getMessages(name, number);
                        semafor.release();

                        System.out.println("Sending messages to -> " + name);
                        outToClient.writeObject(msgNoReads);
                        List<Message> msgReads = Collections.synchronizedList((List<Message>) inFromClient.readObject());

                        semafor.acquire();
                        server.updateMessages(name, msgReads);
                        semafor.release();
                        break;
                    case 5:
                        // Donar-se de baixa del server
                        Client clientUnReg = (Client) inFromClient.readObject();

                        semafor.acquire();
                        boolean okUnregister = server.unregister(clientUnReg);
                        semafor.release();

                        String unRegContent = okUnregister ? "Unregister accepted!" : "Unregister failed!";
                        Message megUnreg = new ShortMessage(unRegContent, "Server", clientUnReg.getUser());
                        outToClient.writeObject(megUnreg);
                        System.out.println("Sending unregister message to -> " + clientUnReg.getUser());
                        outToClient.writeObject(okUnregister);
                        break;
                }
            }


            //socket.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

}
