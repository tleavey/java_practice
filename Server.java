// This was written by Dr. Marc Rubin
// NOT BY ME
// This works in tangent with the Client.java file (I wrote Client.java to connect with Marc's Server.java)

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Server {
  Random mRandom; 
  String mPassword;
  int mPort; 

  public static void main(String[] args) {
    Server server = new Server();
    server.generateRandom(args);
    server.generatePassword();
    server.generatePort();
    System.out.println("CNTL-C to exit");
    server.runServer();
  }

  private void runServer(){
    try{
      //start server on random port 
      ServerSocket serverSocket = new ServerSocket(mPort);

      //wait for incoming connections, spawn thread to handle 
      while(true){
        Socket client = serverSocket.accept();
        ServeClient serveClient = new ServeClient(client);
        Thread thread = new Thread(serveClient);
        thread.start();
      }
    } catch (Exception e){
      System.err.println("ERROR: " + e.toString());
      System.exit(1);
    }
  }

  //thread reads socket input 
  protected class ServeClient implements Runnable {
    Socket mClient;
    
    //constructor 
    public ServeClient(Socket client){
      mClient = client;
    }

    @Override
    public void run(){
      String inputLine;
      try {
        PrintWriter out = new PrintWriter(mClient.getOutputStream(), true);
        InputStreamReader inputStreamReader = new InputStreamReader(mClient.getInputStream());
        BufferedReader in = new BufferedReader(inputStreamReader);

        //read socket input, check password, respond accordingly
        while ((inputLine = in.readLine()) != null) {
          if(inputLine.equals(mPassword)){
            out.println("Hack successful!");
          } else {
            out.println("Nope.  Try again.");
          }
        }
      } catch(SocketException se){
        //do nothing.. 
      } catch(Exception e){
        System.err.println("ERROR: " + e.toString());
        System.exit(1);
      }
    }
  }

  //seed Random with cmd line arg or random number
  private void generateRandom(String [] args){
    int seed = 0;
    if(args.length > 1){
      System.err.println("usage: java Server [seed]");
      System.exit(1);
    } else if (args.length == 1){
      try {
        seed = Integer.parseInt(args[0]);
      } catch(Exception e) {
        System.err.println(e.toString());
        System.exit(1);
      }
    } else {
      seed = (new Random()).nextInt(100000);
    }

    mRandom = new Random(seed);
  }

  //generate random password. (read file, choose 2 random words)
  private void generatePassword(){
    ArrayList <String> words = new ArrayList<String>();
    try {
      File file = new File("words.txt");
      FileReader fileReader = new FileReader(file);
      BufferedReader fileIn = new BufferedReader(fileReader);
      String line;
      while((line = fileIn.readLine()) != null){
        line.trim();
        words.add(line);
      }
    } catch(Exception e){
      System.err.println("ERROR: " + e.toString());
      System.exit(1);
    }

    int a, b;
    a = mRandom.nextInt(words.size());
    do {
      b = mRandom.nextInt(words.size());
    } while(b == a);

    mPassword = words.get(a) + words.get(b);
    System.out.println("Password is " + mPassword);
  }

  //generate random port b/t 5000 and 10000
  private void generatePort(){
    mPort = mRandom.nextInt(5001) + 5000;
    System.out.println("port is " + Integer.toString(mPort));
  }
}
