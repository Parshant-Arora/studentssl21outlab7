package bobby;

import java.net.*;
import java.io.*;
import java.util.*;

import java.util.concurrent.Semaphore;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ScotlandYard implements Runnable{

	/*
		this is a wrapper class for the game.
		It just loops, and runs game after game
	*/

	public int port;
	public int gamenumber;

	public ScotlandYard(int port){
		this.port = port;
		this.gamenumber = 0;
	}

	public void run(){
		while (true){
			Thread tau = new Thread(new ScotlandYardGame(this.port, this.gamenumber)); //yeh SCYG k run ko call kr rha hai 
			System.out.println("Formed a thread with "+port+ " " + gamenumber);
			tau.start();
			try{
				System.out.println("Joining the thread");
				tau.join();
			}
			catch (InterruptedException e){
				return;
			}
			this.gamenumber++;
			System.out.println("in side the thread run with game "+ this.gamenumber);
		}
	}

	public class ScotlandYardGame implements Runnable{
		private Board board;
		private ServerSocket server;
		public int port;
		public int gamenumber;
		private ExecutorService threadPool;

		public ScotlandYardGame(int port, int gamenumber){ //yeh upar thread se call ho rha hai
			this.port = port;
			this.board = new Board();
			this.gamenumber = gamenumber;
			try{
				this.server = new ServerSocket(port);
				System.out.println(String.format("Game %d:%d on", port, gamenumber));
				server.setSoTimeout(5000);
			}
			catch (IOException i) {
				return;
			}
			this.threadPool = Executors.newFixedThreadPool(10);
		}


		public void run() {

			try {
			
				//INITIALISATION: get the game going

				Socket socket = null;
				boolean fugitiveIn = false;

				/*
				listen for a client to play fugitive, and spawn the moderator.
				
				here, it is actually ok to edit this.board.dead, because the game hasn't begun
				*/
				
				do {
			    
          try {
						socket = server.accept();
						System.out.println("accepted socket");
						fugitiveIn = true; 
						this.board.dead = false;
					}
					catch (IOException e) {
						continue;
					}
					catch (NullPointerException e){
						continue;
					}         
      
				} while (!fugitiveIn);
				
				System.out.println(this.gamenumber);

				// Spawn a thread to run the Fugitive
                                             
				ServerThread fugitiveThread = new ServerThread(board,-1,socket,port,gamenumber);                         
				threadPool.execute(fugitiveThread);                                

				// Spawn the moderator

				Moderator m = new Moderator(board);
				threadPool.execute(m); 
                                                  
				while (true) { //rounds yha chal rhe hai, detective aaenge/jaenge
					/*
					listen on the server, accept connections
					if there is a timeout, check that the game is still going on, and then listen again!
					*/

					try {
						socket = server.accept();
					} 
					catch (SocketTimeoutException t) {                                
            			if (this.board.dead) {
								break;
							}                                                 
							continue;
					}
					
					
					/*
					acquire thread info lock, and decide whether you can serve the connection at this moment,

					if you can't, drop connection (game full, game dead), continue, or break.

					if you can, spawn a thread, assign an ID, increment the totalThreads

					don't forget to release lock when done!
					*/
					// System.out.println("going to xecuting detetctive therd");                                     
        			this.board.threadInfoProtector.acquire();

						if (this.board.totalThreads == 5) {
							// socket = null;
							socket.close();
							continue;
						}          
						if (this.board.dead) {
							// socket = null;
							socket.close();
							break;
						}      
						
						ServerThread detectiveThread = new ServerThread(board,this.board.totalThreads,socket,port,gamenumber);
						threadPool.execute(detectiveThread); 
						
						
					
					this.board.threadInfoProtector.release();                         

				}

				/*
				reap the moderator thread, close the server, 
				
				kill threadPool (Careless Whispers BGM stops)
				*/

				server.close();   
				threadPool.shutdown();
				                      
    
				System.out.println(String.format("Game %d:%d Over", this.port, this.gamenumber));
				return;
			}
			catch (InterruptedException ex){
				System.err.println("An InterruptedException was caught: " + ex.getMessage());
				ex.printStackTrace();
				return;
			}
			catch (Exception i){
				return;
			}
		}
	}

	public static void main(String[] args) {
		for (int i=0; i<args.length; i++){
			int port = Integer.parseInt(args[i]);
			Thread tau = new Thread(new ScotlandYard(port));
			tau.start();
		}
	}
}