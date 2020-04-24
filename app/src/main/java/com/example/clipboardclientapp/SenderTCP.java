package com.example.clipboardclientapp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anakim
 */
public class SenderTCP {

    String RECEPTOR_IP;
    int RECEPTOR_PORT;
    private final int LOCAL_PORT;
    public static boolean isSending;
    private Socket client;
    private messageHandler caller;

    public SenderTCP(String rIP, int rPORT, messageHandler caller){
        this.LOCAL_PORT = 8081;
        this.RECEPTOR_IP = rIP;
        this.RECEPTOR_PORT = rPORT;
        this.caller = caller;
        SenderTCP.isSending = false;
    }


    public void send(String type, Object load){
        if(client == null || client.isClosed())
            caller.failedToSendMessage(client, (byte[])load);
        try {
            ObjectOutputStream os =  new ObjectOutputStream(client.getOutputStream());
            os.write((type + "/").getBytes());
            os.flush();
            sendHandler(type, load, os);
        } catch (IOException ex) {
            Logger.getLogger(SenderTCP.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendHandler(String type, Object load, ObjectOutputStream os) throws IOException{
        switch(type){
            case "TEXT":
                os.write(load.toString().getBytes());
                os.flush();
                break;
            case "FILE":

                break;
            case "IMG":

                break;
            default:
                System.out.println("trash");
        }
    }

    public boolean SYNC(){
        if(!isSocketAlive(RECEPTOR_IP, LOCAL_PORT)){
            return false;
        }
        String HEADER = "";
        try {
            client = new Socket(RECEPTOR_IP, RECEPTOR_PORT);
            ObjectOutputStream OS =  new ObjectOutputStream(client.getOutputStream());
            OS.write("SYNC/".getBytes());
            OS.flush();
            ObjectInputStream IS = new ObjectInputStream(client.getInputStream());
            //HEADER =  new String(IS.readAllBytes()).split("/")[0];
            IS.close();
            OS.close();
        } catch (IOException ex) {
            Logger.getLogger(SenderTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return HEADER.equals("SYNC");

    }

    public static boolean isSocketAlive(String hostName, int port) {
        boolean isAlive = false;

        // Creates a socket
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 2000;
        try {
            socket.connect(new InetSocketAddress(hostName, port), timeout);
            socket.close();
            isAlive = true;

        } catch (SocketTimeoutException exception) {
            System.out.println("SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
        }
        return isAlive;
    }


    public void close(){
        try {
            this.client.close();
            client = null;
        } catch (IOException ex) {
            Logger.getLogger(SenderTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
