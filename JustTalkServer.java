import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.*;

public class JustTalkServer {

    public static Map<String,Client> Clients;

    public static void main(String[] args) {

        Print("JustTalk Server: START");

        Clients = new HashMap<String,Client>();

        try {

            ServerSocket server = new ServerSocket(2000);

            while ( true ) {

                Print("Waiting for change:");
                Socket nClient = server.accept();
                Print("New client connected.");

                if ( nClient.isConnected() ) {

                    final Socket newClient = nClient;

                    Print("Client connected: Run New Thread");

                    new Thread(new Runnable() {
                        public void run() {
                            Client newp = new Client(newClient);
                            String last_message = "";
                         //   newp.sendMessage("PING");
                            while ( newp.isConnected() ) {
                                last_message = newp.getMessage();

                                //Print("Server GetMessage:" + last_message);
                                if ( !last_message.equals("") ) {
                                    String[] lista = last_message.split(":");

                                    if ( lista.length > 1 ){
                                        if (lista[0].equals("CD_WAIT") && lista.length > 1) {
                                           // if ( !Clients.containsKey(lista[1]) ) {
                                                newp.login = lista[1];
                                                newp.doit = "WAIT";
                                                Clients.put(newp.login, newp);
                                                for (Map.Entry<String, Client> next : Clients.entrySet()) {

                                                    if (next.getValue().doit.equals("WAIT") && !next.getValue().login.equals(newp.login)) {
                                                        newp.Opponent = next.getValue();
                                                        newp.doit = "TALK";
                                                        next.getValue().Opponent = newp;
                                                        next.getValue().doit = "TALK";
                                                        Print("Server: Client " + next.getValue().login + " talk with client " + newp.login);
                                                        newp.sendMessage("TALK");
                                                        newp.Opponent.sendMessage("TALK");
                                                    }

                                                }
                                                Print(String.valueOf("Clients count: " + Clients.size()));
                                           // } else {
                                            //    if ( Clients.get(lista[1]).Opponent != null ) {
                                            //        Clients.get(lista[1]).sendMessage("CD_CONTINUE");
                                            //    }
                                            //}


                                        }
                                        if (lista[0].equals("CD_END") && lista.length > 1) {
                                            try {
                                                newp.Opponent.sendMessage("CD_END");
                                                newp.Opponent.Disconnect();
                                                newp.Disconnect();
                                                Clients.remove(newp.Opponent.login);
                                                Clients.remove(newp.login);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        if (lista[0].equals("CD_TALK") && lista.length > 1) {
                                            try {
                                                if ( newp.Opponent != null ) {
                                                    if (!newp.Opponent.login.equals("")) {
                                                        newp.Opponent.sendMessage(lista[1]);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }

                                }

                            }
                            Print("Client disconnected.");
                            try {
                                if ( newp.Opponent != null ) {
                                    if (!newp.Opponent.login.equals("")) {
                                        newp.Opponent.sendMessage("CD_END");
                                        newp.Opponent.Disconnect();
                                    }
                                    Clients.remove(newp.Opponent.login);
                                    Clients.remove(newp.login);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            newp = null;
                            Print(String.valueOf("Clients count: " + Clients.size()));
                            try {
                                this.finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();
                }

            }

        }
        catch(Exception e) {
            Print("Whoops! It didn't work!\n");
        }

    }

    public static void Print(String tekst) {
        System.out.println(tekst);
    }

}
