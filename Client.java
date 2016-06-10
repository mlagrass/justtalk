import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;

public class Client {

    Socket soc;
    Integer id;
    String login;
    String session_id;
    String doit = "CD_WAIT";
    Boolean waiting = true;

    BufferedReader reader;
    BufferedWriter writer;

    Boolean isLogged = false;

    ///talk
    Client Opponent;
    ///

    public Client(Socket player_socket) {
        this.soc = player_socket;
        try {
            reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean sendMessage(String message) {
        Print("Client.sendMessage try: "+message);
        if (this.soc.isConnected()) {
            try {

                writer.write(message.toString()+"|");
                writer.flush();
                return true;
            } catch (Exception e) {
                Print(e.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    public String getMessage() {

        if (this.isConnected() == true) {
            StringBuffer instr = new StringBuffer("");
            try {
                if (reader.ready()) {
                    // Print("Player.getMessage: try");
                    instr.setLength(0);
                    while (reader.ready()) {
                        char ll = (char) reader.read();
                        instr.append(ll);
                    }
                    Print("Client.getMessage: " + instr.toString());
                    return instr.toString();
                }
            } catch (IOException e) {
                Print("Client.getMessage: false");
                e.printStackTrace();
            }
        } else {
            Print("Client.getMessage: Client disconnected");
            return "";
        }
        return "";
    }

    public static void Print(String tekst) {
        System.out.println(tekst);
    }

    public Boolean isConnected() {
        try {
            if (reader.read() == -1) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void Disconnect() {
        try {
            Print("Try: Client disconnected.");
            soc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
