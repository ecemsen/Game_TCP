import java.net.*;
import java.io.*;


public class Server extends Calculation {


    public static void main(String[] args) throws IOException {

        Server obj = new Server();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(5555);
        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Server started. Waiting for the connection...");
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept(); // bağlantı gelene kadar burada bekler
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        System.out.println(clientSocket.getLocalAddress() + " baglandi.");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine,outputLine;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        while ((inputLine = in.readLine()) != null) { // istemciden gelen string okunuyor... //inputline clientdan gelen
            if (inputLine.equals("You win!")) {
                System.out.println("You Win Congratulations!");
                break;
            }

            obj.addToUsedWords(inputLine); //clientdan gelen inputu arraye attım
            String lastTwoClient = inputLine.substring(inputLine.length() - 2, inputLine.length());

            System.out.println("WORD FROM CLIENT: " + inputLine + " ENTER THE WORD THAT START WITH THESE WORDS: " + lastTwoClient);
            System.out.println("YOUR TURN!");
            System.out.println("USED WORDS: " + obj.usedWords);
            long start = System.currentTimeMillis();
            while ((System.currentTimeMillis() - start < 10000) && !stdIn.ready()) {
            }

            if (stdIn.ready()) {
                outputLine = stdIn.readLine();  //satırda yazılanı göndermek için outputline a eşitledik
                if (!lastTwoClient.equals(outputLine.substring(0, 2))) {
                    out.println("You win!");
                    System.out.println("Game over! Client wins!");
                    break;
                }

                if (obj.usedWords.contains(outputLine)) {
                    System.out.println("This word is used.Try another one.");
                    outputLine = stdIn.readLine();
                }

                long finish1 = System.currentTimeMillis();
                long timeElapsed1 = finish1 - start;
                if (timeElapsed1 > 10000) {
                    out.println("You win!");
                    System.out.println("Time's up.You lost.");
                }
                out.println(outputLine); // serverdan clienta gönderilen cevap
                obj.addToUsedWords(outputLine);// girilen inputu daha sonra karşılaştırmak için arraye ekle
            }else {
                out.println("You win!");
                System.out.println("Game over.You lost");
            }
        }
        System.out.println(clientSocket.getLocalSocketAddress() + " connection stopped.");// stream ve socketleri kapat.
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}