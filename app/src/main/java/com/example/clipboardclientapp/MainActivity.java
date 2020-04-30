package com.example.clipboardclientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements messageHandler{

    private int PORT;
    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    ReceptorTCP receptor;
    SenderTCP sender;
    TextView textView;
    Button button5;
    TextView textView4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.editText2 = (EditText)findViewById(R.id.editText2);
        this.textView = (TextView)findViewById(R.id.textView);
        this.button5 = (Button)findViewById(R.id.button5);
        this.editText3 = (EditText)findViewById(R.id.editText3);
        this.editText = (EditText)findViewById(R.id.editText);
        this.editText4 = (EditText)findViewById(R.id.editText4);
        this.textView4 = (TextView)findViewById(R.id.textView4);
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            int intAddr = wifiManager.getConnectionInfo().getIpAddress();
            byte[] byteaddr = new byte[] { (byte) (intAddr & 0xff), (byte) (intAddr >> 8 & 0xff), (byte) (intAddr >> 16 & 0xff), (byte) (intAddr >> 24 & 0xff) };
            InetAddress result = InetAddress.getByAddress(byteaddr);
            textView.setText("IP: " + result.getHostAddress());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    private void Toast(String text, int length){
        Toast.makeText(getApplicationContext(), text, length).show();
    }

    private CharSequence getStringFromClipboard(ClipboardManager clipboard){
        return clipboard.getPrimaryClip().getItemAt(0).getText();
    }

    public void sendMessage(View view) {
        if(!editText2.getText().toString().isEmpty()) {
            this.sender = new SenderTCP(editText.getText().toString(), Integer.parseInt(editText4.getText().toString()), this);
            sender.sendMessage(editText2.getText().toString());
        }
    }

    public void setStringToClipboard(String text){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Text",text));
    }
    public void copyToClipboard(View view){
        setStringToClipboard(editText2.getText().toString());
        Toast("copied to clipboard", Toast.LENGTH_SHORT);
    }
    public void updateTextArea(View view){
        ClipboardManager clipboard = (ClipboardManager)getSystemService(getApplicationContext().CLIPBOARD_SERVICE);
        if(clipboard.hasPrimaryClip()){
            editText2.setText(getStringFromClipboard(clipboard), TextView.BufferType.NORMAL);
        }
    }
    public void setVisible(View view) throws UnknownHostException {
        if(button5.getText().equals("connect")){
            if(!editText3.getText().toString().isEmpty()){
                receptor = new ReceptorTCP(Integer.parseInt(editText3.getText().toString()), this);
                receptor.enableReception();
                button5.setText("unconnect");
                Toast("listening on port:"+ editText3.getText().toString(), Toast.LENGTH_SHORT);
                textView4.setText("port:" + editText3.getText().toString());
            }
        }else{
            receptor.disableReception();
            textView4.setText("port:");
            button5.setText("connect");
        }
    }

    @Override
    public void TextMessageReceiveFromClient(Socket clientSocket,String data) {
        editText2.setText((CharSequence)data, TextView.BufferType.NORMAL);
        setStringToClipboard(data);
        Toast("msg from ip:" + clientSocket.getInetAddress().getHostAddress(), Toast.LENGTH_SHORT);
    }

    @Override
    public void FileReceiveFromClient(Socket clientSocket, byte[] data) {

    }

    @Override
    public void ImgReceivedFromClient(Socket clientSocket, byte[] data) {

    }

    @Override
    public void failedToSendMessage(Socket clientSocket, byte[] data) {
        Toast("failed to send message to " + clientSocket.getInetAddress().getHostAddress(), Toast.LENGTH_SHORT);
    }

    @Override
    public void clientConnected(Socket clientSocket) {
        Toast("client: " + clientSocket.getInetAddress().getHostAddress(), Toast.LENGTH_SHORT);
    }
}
