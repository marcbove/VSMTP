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

import Types.Client;
import Types.Message;
import java.io.*;
import java.net.*;
import java.util.*;


class TCPClient {
    public static void main(String argv[]) throws Exception {

        Client client = null;
        String name = "", password, info, destination;

        boolean okLogin = false;
        Socket clientSocket = new Socket("localhost", 6789);

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        ObjectOutputStream sendToServer = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());

        Message message;

        int option = 10;
        do{
            try {
                if (!okLogin)
                    showMenuRegistration();
                else
                    showMenu();

                option = Integer.parseInt(inFromUser.readLine().trim());
            } catch (NumberFormatException nfe) {
                System.out.println("This option is not a Number, try again!");
            }


            if (okLogin && (option != 0))
                option += 2;  // Si ja està logueat, només entrarà en les opcions de 3 o més amunt

            sendToServer.writeObject(option);
            switch (option) {
                case 1:
                    // Register for  Server
                    System.out.println("Please enter a user name:");
                    name = inFromUser.readLine();
                    System.out.println("Please enter a password: ");
                    password = inFromUser.readLine();
                    client = new Client(name, password);
                    sendToServer.writeObject(client);
                    message = (Message) inFromServer.readObject();
                    System.out.println(message);
                    break;

                case 2:
                    // Login
                    System.out.println("Please enter a user name:");
                    name = inFromUser.readLine();
                    System.out.println("Please enter a password: ");
                    password = inFromUser.readLine();
                    client = new Client(name, password);
                    sendToServer.writeObject(client);
                    okLogin = (Boolean) inFromServer.readObject();
                    message = (Message) inFromServer.readObject();
                    System.out.println(message);
                    break;

                case 3:
                    if (okLogin) {
                        // Enviar un missatge de text a un client determinat, que estigui donat d'alta.
                        int opt = 0;
                        do{
                            try {
                                showMenuSendMessage();
                                opt = Integer.parseInt(inFromUser.readLine().trim());
                            } catch (NumberFormatException nfe) {
                                System.out.println("This option is not a Number, try again!");
                            }
                        }while (opt != 1 && opt != 2);
                        sendToServer.writeObject(opt);
                        if(opt == 1)
                        {
                            List<String> users = (List<String>) inFromServer.readObject();
                            System.out.println("Users available: " + users);
                            System.out.println("Please choose a user to send the message: ");
                            destination = inFromUser.readLine().trim();
                            sendToServer.writeObject(destination);

                            boolean okDest = (Boolean) inFromServer.readObject();
                            if (!okDest)
                                System.out.println("User disavailable!");
                            else {
                                System.out.println("Please write the message: ");
                                info = inFromUser.readLine().trim();
                                message = new Message(info, name, destination);
                                sendToServer.writeObject(message);
                            }
                        }
                        else
                        {
                            String destString = "";
                            List<String> users = (List<String>) inFromServer.readObject();
                            sendToServer.writeObject(client);
                            do {
                                System.out.println("Users available: " + users);
                                System.out.println("Please choose a user to add to to the group: ");
                                destination = inFromUser.readLine().trim();
                                sendToServer.writeObject(destination);

                                boolean okDest = (Boolean) inFromServer.readObject();
                                if (!okDest)
                                    System.out.println("User unavailable!");
                                else {
                                    destString = (destString == "") ? destination : destString + ", " + destination;
                                }
                                showMenuSendGroup();
                                opt = Integer.parseInt(inFromUser.readLine().trim());
                                sendToServer.writeObject(opt);
                            } while(opt != 2);

                            System.out.println("Please write the message to the group: ");
                            info = inFromUser.readLine().trim();
                            message = new Message(info, name, destString);
                            sendToServer.writeObject(message);
                        }
                    }
                    break;

                case 4:
                    // Llegir els últims n correus
                    if (okLogin) {
                        int number = 0;
                        int opt = 0;
                        while (opt != 1 && opt != 2) {
                            try {
                                showMenuReadMessage();
                                opt = Integer.parseInt(inFromUser.readLine().trim());
                                if (opt == 2) {
                                    System.out.println("\tHow many?");
                                    number = Integer.parseInt(inFromUser.readLine().trim());
                                }
                            } catch (NumberFormatException nfe) {
                                System.out.println("This option is not a Number, try again!");
                            }
                        }
                        sendToServer.writeObject(opt);
                        sendToServer.writeObject(name);
                        sendToServer.writeObject(number);
                        List<Message> msgNoReads = Collections.synchronizedList((List<Message>) inFromServer.readObject());
                        System.out.println(msgNoReads);
                        synchronized (msgNoReads) {
                            msgNoReads.forEach(a -> a.setRead(true));
                        }
                        sendToServer.writeObject(msgNoReads);
                    }
                    break;

                case 5:
                    // Donar-se de baixa al servidor
                    if (okLogin) {
                        sendToServer.writeObject(client);
                        Message msgResponse = (Message) inFromServer.readObject();
                        System.out.println(msgResponse);
                        Boolean okUnreg = (Boolean) inFromServer.readObject();
                        if (okUnreg) {
                            okLogin = false;
                        }
                    }
                    break;
            }
        }while (option != 0);
        //----------------
        clientSocket.close();
    }

    public static void showMenuRegistration() {
        System.out.println("\n----------- MENU ---------");
        System.out.println("\t 1. Register for  Server.");
        System.out.println("\t 2. Login.");
        System.out.println("\t 0. Exit.");
        System.out.println("--------------------------");
        System.out.println(" Please choose a option:");
    }

    public static void showMenu() {
        System.out.println("\n----------- MENU ---------");
        System.out.println("\t 1. Send a message.");
        System.out.println("\t 2. Read messages.");
        System.out.println("\t 3. Unregister from Server.  ");
        System.out.println("\t 0. Exit.");
        System.out.println("--------------------------");
        System.out.println(" Please choose a option:");
    }

    public static void showMenuSendMessage() {
        System.out.println("------ Send Message ------");
        System.out.println("\t 1. Send a message to a single user.");
        System.out.println("\t 2. Create a group and send a message to all members.");
        System.out.println("--------------------------");
        System.out.println(" Please choose a option:");
    }

    public static void  showMenuReadMessage() {
        System.out.println("------ Read Message ------");
        System.out.println("\t 1. Read all messages recieved.");
        System.out.println("\t 2. Read last N messages recieved.");
        System.out.println("--------------------------");
        System.out.println(" Please choose a option:");
    }

    public static void  showMenuSendGroup() {
        System.out.println("- Do you want to add more members? -");
        System.out.println("\t 1. Yes.");
        System.out.println("\t 2. No.");
        System.out.println("------------------------------------");
        System.out.println(" Please choose a option:");
    }
}