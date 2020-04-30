package com.example.clipboardclientapp;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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

    private boolean isEnabled;
    private ServerSocket serverSocket;
    private int PORT;
    private messageHandler caller;
    private Handler h;
    public ReceptorTCP(int port, messageHandler caller) throws UnknownHostException {
        this.isEnabled = false;
        this.PORT = port;
        this.caller = caller;
        this.h = new Handler();
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
                try (final Socket receivedSocket = serverSocket.accept()) {
                    ObjectInputStream OIS = new ObjectInputStream(receivedSocket.getInputStream());
                    final String header = OIS.readUTF().split("/")[0];
                    switch(header){
                        case "TEXT":
                            final String load = OIS.readUTF();
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    caller.TextMessageReceiveFromClient(receivedSocket, load);
                                }
                            });
                             break;
                        case "FILE":

                            break;
                        case "IMGN":

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

    private void post(Runnable r){
        h.post(r);
    }
}