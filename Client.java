// Tim Leavey
// Connects to Server.java

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.System;

class Client {
	// Variables available to all methods in Client
	private static int openPort;
	private static int numThreads;
	private static ArrayList<String> passwords;

	// Main
	// Creates a client, stores # of threads
	// Stores number of passwords
	// Stores the port number
	// Creates threads
	public static void main(String[] args) {
		Client client = new Client();
		numThreads = client.threadCount(args);
		//System.out.println("Number of threads: " + numThreads);
		passwords = client.listOfPasswords();
		//System.out.println(passwords);
		openPort = client.getOpenPort();
		System.err.println("Found Open Port: " + openPort);
		client.createThreads(numThreads);
	}

// Creates # of threads based on the command line argument
	private void createThreads(int numThreads) {
		int start;
		int end;
		for (int i = 0; i < numThreads; i++){
			start = passwords.size() / numThreads * i;
			if (i + 1 < numThreads) {
				end = passwords.size() / numThreads * (i + 1);
			} else {
				end = passwords.size();
			}
			int threadID = i;
			MyThread runningThread = new MyThread(threadID, start, end);
			Thread t = new Thread(runningThread);
			t.start();
		}
	}

// Finds the open port number
	private int getOpenPort(){
		for (int port = 5000; port <= 10000; port++) {
			try {
				InetAddress myLocalHost = InetAddress.getLocalHost();
				System.out.println("Testing port " + port);
				Socket openSocket = new Socket(myLocalHost, port);
				int portNumber = openSocket.getPort();
				return portNumber;
			} catch (Exception ex) {
			}
		} 	
		return 1;
	}

	// Makes a list of all potential passwords
	// Takes in words from words.txt
  private ArrayList listOfPasswords(){
    ArrayList<String> words = new ArrayList<String>();
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
    ArrayList<String> allPasswords = new ArrayList<String>();
    for (String word1 : words) {
    	for (String word2 : words) {
    		allPasswords.add(word1 + word2);
    	}
    }
    return allPasswords;
  }

// Stores the thread count argument from the command line in a variable
  private int threadCount(String[] args) {
  	int numThreads = 0;
		if (args.length > 0) {
		    try {
		        numThreads = Integer.parseInt(args[0]);
		        if (numThreads <= 0) {
		        	System.err.println("Thread count must be positive.");
		        	System.exit(1);
		        }
		    } catch (NumberFormatException e) {
		        System.err.println("Argument" + args[0] + " must be an integer.");
		        System.exit(1);
		    }
		} else {
		  System.err.println("Argument must have a thread count.");
		  System.exit(1);			
		}
		return numThreads;
  }

// A single thread.
// Tests passwords against the server.
  private static class MyThread implements Runnable {
  	Integer mThreadID;
  	Integer mStart;
  	Integer mEnd;

  	// constructor
  	public MyThread(int threadID, int start, int end) {
  		mThreadID = threadID;
  		mStart = start;
  		mEnd = end;
  	}

  	// Tests against the server each password
  	// Finds and outputs to stderr the correct password and threadid
    @Override
    public void run(){
      String inputLine;
      try {
      	InetAddress myLocalHost = InetAddress.getLocalHost();
	    	Socket mClient = new Socket(myLocalHost, openPort);
        PrintWriter out = new PrintWriter(mClient.getOutputStream(), true);
        InputStreamReader inputStreamReader = new InputStreamReader(mClient.getInputStream());
        BufferedReader in = new BufferedReader(inputStreamReader);

        // Sends each potential password to the server and sends correct one to stderr
        for (int i = mStart; i < mEnd; i++) {
        	out.println(passwords.get(i));
        	//System.out.println(onePassword);
        	String inputReadLine = in.readLine();
        	// if statement initiates if correct response is returned from server
        	if(inputReadLine.contains("Hack")){
        		System.err.println("Password is " + passwords.get(i));
        		System.err.println("Thread ID is: " + mThreadID);
        		break;
        	}
        }
      } catch(Exception e){
        //do nothing.. 
      }
    }
  }
}
