package com.ctp.ctpchat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * Created by CTP on 2/19/2017.
 */

public class Client {
    // for I/O
    private static final String CLIENT_TAG="ClientClass";
    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;

    // if I use a GUI or not
    private Context cg;

    // the server, the port and the username
    private String server, username;
    private int port;

    /*
     *  Constructor called by console mode
     *  server: the server address
     *  port: the port number
     *  username: the username
     */
    Client(String server, int port, String username) {
        // which calls the common constructor with the GUI set to null
        this(server, port, username, null);
    }

    /*
     * Constructor call when used from a GUI
     * in console mode the ClienGUI parameter is null
     */
    Client(String server, int port, String username, Context cg) {
        this.server = server;
        this.port = port;
        this.username = username;
        // save if we are in GUI mode or not
        this.cg = cg;
    }

    /*
     * To start the dialog
     */
    public void writeMessage(ChatMessage cm){
        MessageSender ms = new MessageSender();
        ms.execute(cm);
    }

    public void execute(){
        GetSocketConnection socketConnection = new GetSocketConnection();
        socketConnection.execute();
    }
    private boolean start() {
        // try to connect to the server

        try {
            InetAddress serverAddress = InetAddress.getByName(server);
            socket = new Socket(serverAddress, port);
            Log.d(CLIENT_TAG,"Im connected");
        }
        // if it failed not much I can so
        catch(Exception ec) {
            Log.d(CLIENT_TAG,"Error Connecting to server");
            ec.printStackTrace();
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

		/* Creating both Data Stream */
        try
        {
            sInput  = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        }
        catch (IOException eIO) {
//            display("Exception creating new Input/output Streams: " + eIO);
            Log.d(CLIENT_TAG,"Exception creating new I/O streams");
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try
        {
            sOutput.writeObject(username);
        }
        catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();

        }

        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console or the GUI
     */
    private void display(String msg) {
        if(cg == null)
            System.out.println(msg);      // println in console mode
        else
            return;
        //TODO: EDIT accordingly
//            cg.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
    }

    /*
     * To send a message to the server
     */


    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {} // not much else I can do
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {} // not much else I can do
        try{
            if(socket != null) socket.close();
        }
        catch(Exception e) {} // not much else I can do

        // inform the GUI
        //TODO:Edit accordingly
//        if(cg != null)
//            cg.connectionFailed();

    }

    private class GetSocketConnection extends AsyncTask<String,Void,Boolean>{


        @Override
        protected Boolean doInBackground(String... params) {
            return start();
        }
    }

    private class MessageSender extends AsyncTask<ChatMessage,Void,Void>{
        @Override
        protected Void doInBackground(ChatMessage... params) {

            ChatMessage temp = params[0];
            Log.d(CLIENT_TAG,"MessageType"+temp.getType());
            Log.d(CLIENT_TAG,"Message is"+temp.getMessage());
            sendMessage(params[0]);

            return null;
        }
    }

    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        }
        catch(IOException e) {
            Log.d(CLIENT_TAG,"Exception writing to server"+e);
            e.printStackTrace();
        }
    }

    /*
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */

//   private class ListenFromServer extends AsyncTask<Void,Void,Void>{
//        @Override
//        protected Void doInBackground(Void... params) {
//            Log.d(CLIENT_TAG,"Im going to run");
//            while(true) {
//                try {
//                    String msg = (String) sInput.readObject();
//                    // if console mode print the message and add back the prompt
//                    if(cg == null) {
//                        System.out.println(msg);
//                        System.out.print("> ");
//                    }
//                    else {
//                        //TODO: Edit accordingly
//                        Log.d(CLIENT_TAG,"The message is"+msg);
////                        cg.append(msg);
//
//                    }
//                }
//                catch(IOException e) {
////                    display("Server has close the connection: " + e);
//                    if(cg != null) {//TODO:edit accordingly
//                        Log.d(CLIENT_TAG, "Server closed connection" + e);
//                        e.printStackTrace();
//                    }
//
//
//                    break;
//                }
//                // can't happen with a String object but need the catch anyhow
//                catch(ClassNotFoundException e2) {
//                    Log.d(CLIENT_TAG, "Server closed connection" + e2);
//                }
//            }
//            return null;
//        }
//    }
    class ListenFromServer extends Thread {

        public void run() {
            while(true) {
                try {
                    String msg = (String) sInput.readObject();
                    // if console mode print the message and add back the prompt
                    if(cg == null) {
                        System.out.println(msg);
                        System.out.print("> ");
                    }
                    else {
                        //TODO: Edit accordingly
                        Log.d(CLIENT_TAG,msg);
//                        cg.append(msg);

                    }
                }
                catch(IOException e) {
//                    display("Server has close the connection: " + e);
                    if(cg != null) {
                        //TODO:edit accordingly
                        Log.d(CLIENT_TAG, "Server closed connection" + e);
                        e.printStackTrace();
                    }

                   break;
                }
                // can't happen with a String object but need the catch anyhow
                catch(ClassNotFoundException e2) {
                }
            }
        }
    }

}
