/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;


import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TCPclient implements Runnable
{  private Socket socket              = null;
   private Thread thread              = null;
   private DataInputStream  console   = null;
   private DataOutputStream streamOut = null;
   private ChatClientThread client    = null;

   public TCPclient(String serverName, int serverPort)
   {  System.out.println("Establishing connection. Please wait ...");
      try
      {  socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe)
      {  System.out.println("Host unknown: " + uhe.getMessage()); }
      catch(IOException ioe)
      {  System.out.println("Unexpected exception: " + ioe.getMessage()); }
   }
   public void run()
   {  while (thread != null)
      {  try
         {  streamOut.writeUTF(console.readLine());
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
            stop();
         }
      }
   }
   public void handle(String msg)
   {  if (msg.equals(".bye"))
      {  System.out.println("Good bye. Press RETURN to exit ...");
         stop();
      }
      else
         System.out.println(msg);
   }
   public void start() throws IOException
   {  console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null)
      {  client = new ChatClientThread(this, socket);
         thread = new Thread(this);                   
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread.stop();  
         thread = null;
      }
      try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing ..."); }
      client.close();  
      client.stop();
   }
   public static void main(String args[]) throws IOException
   {
       Scanner input = new Scanner(System.in);
       String line = null;
       String fileName = "D:\\Network Programming lab\\MultiChat\\MultiChat\\Id.txt";
       try{
           FileReader fl = new FileReader(fileName);
           BufferedReader bufferedReader = 
                new BufferedReader(fl);
           String name,pass,com;
            System.out.println("Log in or Create Id");
            com = input.nextLine();
            if(com.equals("login")){
                 System.out.println("Enter name");
                 name = input.nextLine();
                 System.out.println("Enter pass");
                 pass = input.nextLine();
                while((line = bufferedReader.readLine()) != null) {
                    if(line.equals(name+" "+pass)){
                        System.out.println("Successful");
                        TCPclient client = null;
                        client = new TCPclient ("localhost", 2000);
                        
                    }
                }
                bufferedReader.close(); 
            }
            else{
                System.out.println("Enter name");
                name = input.nextLine();
                System.out.println("Enter pass");
                pass = input.nextLine();
                FileWriter fileWriter = new FileWriter(fileName,true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.newLine();
                bufferedWriter.write(name+" "+pass);
                bufferedWriter.close();
                System.out.println("Successful");
                TCPclient client = null;
                client = new TCPclient ("localhost", 2000);
            }
       }
       catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");
       }
   }
}