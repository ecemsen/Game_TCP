import java.io.*;
import java.net.*;

public class Client extends Calculation {
    public static void main(String[] args) throws IOException {
        Client obj = new Client();
        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            socket = new Socket("localhost", 5555); // "localhost" ya da sunucu IP adresi
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Server could not find.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O exception:" + e.getMessage());
            System.exit(1);
        }
        System.out.println("Connected to the server...");

        String inputLine,outputLine;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("ENTER A WORD TO START THE GAME: ");
        inputLine = stdIn.readLine(); //oyuna başlamak için girilen kelime bunu while döngüsünün dışında tutuyoruz
        obj.addToUsedWords(inputLine); // girilen inputu daha sonra karşılaştırmak için arraye ekle
        out.println(inputLine);

        while ((outputLine = in.readLine()) != null)
        {
            if(outputLine.equals("You win!")){
                System.out.println("You Win Congratulations!");
                break;
            }

            obj.addToUsedWords(outputLine); //serveredan gelen outputu arraye attım
            String lastTwoServer = outputLine.substring(outputLine.length()-2,outputLine.length());

            System.out.println("WORD FROM SERVER: " + outputLine + " ENTER THE WORD THAT START WITH THESE WORDS: " + lastTwoServer) ;
            System.out.println("YOUR TURN!");
            System.out.println("USED WORDS: " + obj.usedWords);
            long start = System.currentTimeMillis();
            while((System.currentTimeMillis()-start < 10000) && !stdIn.ready()){
            }
            if(stdIn.ready()) {
                inputLine = stdIn.readLine(); // satırda yazanı göndermek için inputline a eşitledik
                if (obj.usedWords.contains(inputLine)) {
                    System.out.println("This word is used.Try another one.");
                    inputLine = stdIn.readLine();
                }

                if (!lastTwoServer.equals(inputLine.substring(0, 2))) {
                    out.println("You win!");
                    System.out.println("Game over. Server wins!");
                    break;
                }

                obj.addToUsedWords(inputLine);
                out.println(inputLine);
            }else
            {
                out.println("You win!");
                System.out.println("Game over.You lost.");
            }
        }

        System.out.println("Stopping Connection...");
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}
