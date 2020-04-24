package com.example.clipboardclientapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anakim
 */
public class ReceptorTCP extends Thread {

    private String LOCAL_IP;
    private String LOCAL_HOST_NAME;
    private boolean isEnabled;
    private ServerSocket serverSocket;
    private int PORT;
    private String TEXT;
    private messageHandler caller;
    public ReceptorTCP(int port, messageHandler caller) throws UnknownHostException {
        this.LOCAL_IP = "";
        this.LOCAL_HOST_NAME = "";
        this.isEnabled = false;
        this.PORT = port;
        this.caller = caller;
    }

    public void enableReception(){
        isEnabled = true;
        this.start();
    }
    public void disableReception(){
        isEnabled = false;
    }

    @Override
    public void run(){
        try {
            this.serverSocket=new ServerSocket(PORT);
            while(isEnabled){
                try (Socket receivedSocket = serverSocket.accept()) {
                    ObjectInputStream OIS = new ObjectInputStream(receivedSocket.getInputStream());

                    switch(new String(OIS.readUTF().split("/")[0])){
                        case "TEXT":
                            String load = OIS.readUTF();
                            caller.TextMessageReceiveFromClient(receivedSocket, load);
                            break;
                        case "FILE":

                            break;
                        case "IMG":

                            break;
                        default:
                            //System.out.println("trash");

                    }
                    OIS.close();
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(ReceptorTCP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public String getLOCAL_IP() {
        return LOCAL_IP;
    }

    public String getLOCAL_HOST_NAME() {
        return LOCAL_HOST_NAME;
    }

    public String getReceivedText(){
        return TEXT;
    }


}