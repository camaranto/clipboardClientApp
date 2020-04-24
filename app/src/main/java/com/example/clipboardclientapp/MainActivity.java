package com.example.clipboardclientapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements messageHandler{

    private final int PORT = 8080;
    EditText editText2;
    ReceptorTCP receptor;
    SenderTCP sender;
    Switch switch1;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.editText2 = (EditText)findViewById(R.id.editText2);
        this.switch1 = (Switch)findViewById(R.id.switch1);
        this.textView = (TextView)findViewById(R.id.textView);
        try {
            this.receptor = new ReceptorTCP(PORT, this);
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
        // Do something in response to button
    }
    public void sync(View view){
        Toast.makeText(getApplicationContext(), "SYNC", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(getApplicationContext(), clipboard.getPrimaryClip().getItemAt(0).getText(), Toast.LENGTH_SHORT).show();
    }
    public void setVisible(View view){
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    receptor.enableReception();
                    Toast("visible", Toast.LENGTH_SHORT);
                }else{
                    receptor.disableReception();
                }
            }
        });

    }

    @Override
    public void TextMessageReceiveFromClient(Socket clientSocket, String data) {
        editText2.setText((CharSequence)data, TextView.BufferType.NORMAL);
        setStringToClipboard(data);
        Toast("received from " + clientSocket.getInetAddress(), Toast.LENGTH_SHORT);
    }

    @Override
    public void FileReceiveFromClient(Socket clientSocket, byte[] data) {

    }

    @Override
    public void ImgReceivedFromClient(Socket clientSocket, byte[] data) {

    }

    @Override
    public void failedToSendMessage(Socket clientSocket, byte[] data) {

    }
}
