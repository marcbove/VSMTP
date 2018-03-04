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
package Application;

import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerMultiThreading {
    public static void main(String[] args) throws Exception {

        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket coonectionSocket = welcomeSocket.accept();
            new AttendPetition(coonectionSocket).start();
        }

    }
}
