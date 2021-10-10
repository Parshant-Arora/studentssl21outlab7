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


		public void run(){

			try{
			
				//INITIALISATION: get the game going

				// System.out.print

				Socket socket = null;
				boolean fugitiveIn = false;
				// fugitiveIn = false;
				// PrintWriter outp = new PrintWriter(socket.getOutputStream(), true);
				/*
				listen for a client to play fugitive, and spawn the moderator.
				
				here, it is actually ok to edit this.board.dead, because the game hasn't begun
				*/
				
				do{
			    
					// System.out.println("in the do while");
          			try {
						// System.out.println("in th do while with Game "+gamenumber);
						socket = server.accept();
						System.out.println("accepted socket");
						// PrintWriter outp = new PrintWriter(socket.getOutputStream(), true);
						// BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						fugitiveIn = true; 
					}
					catch (IOException e) {
						// server.setSoTimeout(10000);
						// if(server==null)System.out.println("server null at game "+gamenumber);
						// if(server!=null)System.out.println("non null server with game "+gamenumber);
						// System.out.println("Caught the exception in game "+gamenumber);
					}
					catch(NullPointerException e){

					}
					       
       
                                       
                         //wait for fugitive to come
               
      
				} while (!fugitiveIn);
				
				System.out.println(this.gamenumber);

				// Spawn a thread to run the Fugitive
                                             
				ServerThread fugitiveThread = new ServerThread(board,-1,socket,port,gamenumber);                         
				threadPool.execute(fugitiveThread);
				

				// Thread thread1 = new Thread(fugitiveThread, "Fugitive_Thread");
				// Thread thread2 = new Thread(runnable2, "Thread2");
				
				// thread1.start();
				
				// try
				// { 
				// 	thread1.join();
				// 	// thread2.join();
				// } 
				// catch(Exception ex) 
				// { 
				// 	System.out.println("Exception has been" + 
				// 							" caught" + ex); 
				// }
				// System.out.println("End of:" + Thread.currentThread().getName());
				// outp.println(String.format("in the ",this.port, this.gamenumber));
				                                                                         
                                             

				// Spawn the moderator
                                                  
                System.out.println("In the main file back");
				// while (true){ //rounds yha chal rhe hai, detective aaenge/jaenge
				// 	/*
				// 	listen on the server, accept connections
				// 	if there is a timeout, check that the game is still going on, and then listen again!
				// 	*/

				// 	try {

				// 	} 
				// 	catch (SocketTimeoutException t){
                                               
                            
                                                
             
       
                                               
				// 		continue;
				// 	}
					
					
				// 	/*
				// 	acquire thread info lock, and decide whether you can serve the connection at this moment,

				// 	if you can't, drop connection (game full, game dead), continue, or break.

				// 	if you can, spawn a thread, assign an ID, increment the totalThreads

				// 	don't forget to release lock when done!
				// 	*/
					                                         
                          
                     
                                               
            
      
                                                 
                          
                     
                                               
               
      
     
                                                                                                          
                                  

                                              

				// }

				/*
				reap the moderator thread, close the server, 
				
				kill threadPool (Careless Whispers BGM stops)
				*/
				server.close();
				threadPool.shutdown();
			            
                        
                               
    
				System.out.println(String.format("Game %d:%d Over", this.port, this.gamenumber));
				return;
			}
			// catch (InterruptedException ex){
			// 	System.err.println("An InterruptedException was caught: " + ex.getMessage());
			// 	ex.printStackTrace();
			// 	return;
			// }
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