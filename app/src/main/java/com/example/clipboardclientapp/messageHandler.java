package com.example.clipboardclientapp;

import java.net.Socket;

/**
 *
 * @author anakim
 */
public interface messageHandler {
    public void TextMessageReceiveFromClient(Socket clientSocket, String data);
    public void FileReceiveFromClient(Socket clientSocket, byte[] data);
    public void ImgReceivedFromClient(Socket clientSocket, byte[] data);
    public void failedToSendMessage(Socket clientSocket, byte[] data);
}
