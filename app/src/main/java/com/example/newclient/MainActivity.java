package com.example.newclient;

import android.app.UiAutomation;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private ServerSocket serverSocket;
    Handler UIHandler;
    Thread Thread1 = null;
    //private EditText editText;
    private TextView textView;
    private Button button,button2,clearButton;
    public static String CMD ="0";
    public static final int serverport= 5555;
    public static final String serverip = "192.168.43.96";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView);
        clearButton = (Button)findViewById(R.id.clear);
        textView.setMovementMethod(new ScrollingMovementMethod());
        button = (Button)findViewById(R.id.buttonUp);
        UIHandler = new Handler();
        button2 =(Button)findViewById(R.id.buttonDown);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread1 = new Thread(new  Thread1());
                Thread1.start();
                CMD = "UP";
                Soket_AsyncTask CmdActivity = new Soket_AsyncTask();
                CmdActivity.execute();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread1 = new Thread(new  Thread1());
                Thread1.start();
                CMD = "DOWN";
                Soket_AsyncTask CmdActivityDown = new Soket_AsyncTask();
                CmdActivityDown.execute();
            }
        });


    }
    class Thread1 implements Runnable{

        @Override
        public void run() {
            Socket socket = null;

            try{
                InetAddress serverAddr = InetAddress.getByName(serverip);
                socket = new Socket(serverAddr,serverport);

                Thread2 comThread = new Thread2(socket);
                new Thread(comThread).start();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    class Thread2 implements Runnable{
        private Socket clientSocket;
        private BufferedReader input;
        public Thread2(Socket clientSocket){
            this.clientSocket = clientSocket;
            try{
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
        while(!Thread.currentThread().isInterrupted()){
            try{
                String read= input.readLine();
                if(read != null){
                    UIHandler.post(new updateUIThread(read));

                }else{
                    Thread1 = new  Thread(new Thread1());
                    Thread1.start();
                    return;
                }

            }catch (IOException e){
                e.printStackTrace();
                }
            }
        }
    }
    class updateUIThread implements Runnable{
        private String msg;

        public updateUIThread(String str ){
            this.msg=str;
        }
        @Override
        public void run() {
            //editText.setText("");
            //editText.setText(editText.getText().toString()+"Server Says:\n"+msg);
            textView.setText(textView.getText().toString()+"\nServer Says:"+msg);
        }
    }
    class Soket_AsyncTask extends AsyncTask<Void,Void,Void> {
        Socket socket;
        protected Void doInBackground(Void...params){

            try{
                InetAddress inetAdress = InetAddress.getByName(MainActivity.serverip);
                socket = new java.net.Socket(inetAdress,5555);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(CMD);
                dataOutputStream.close();
                socket.close();
            }catch (UnknownHostException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;

        }


    }
}
