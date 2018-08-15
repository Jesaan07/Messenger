package tcpserver;
import java.net.*;
import java.io.*;

public class TCPServer implements Runnable
{  private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private int pm = 0,top = 0;
   private int[] pmc = new int[2];
   String fileName = "D:\\Network Programming lab\\MultiChat\\MultiChat\\friend.txt";
   

   public TCPServer (int port)
   {  try
      {  System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + port + ": " + ioe.getMessage()); }
   }
@Override
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()  { 
       if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   public void stop()   { 
   if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   private int findClient(int ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   private int findbyname(String name){
       for (int i = 0; i < clientCount; i++)
         if (clients[i].getname().equals(name))
            return i;
       return -1;
   }
   public synchronized void handle(int ID, String input) throws IOException
   {  if (input.equals(".bye"))
      {  clients[findClient(ID)].send(".bye");
         remove(ID); }
      else if(input.contains(".login")){
          clients[findClient(ID)].setname(input.substring(input.indexOf(".login")+7));
          
      }
      else if(input.contains(".add")){
          String[] parts = input.split(" ");
          
              clients[findbyname(parts[1])].send(clients[findClient(ID)].getname()+" Wants to add you");

      }
      else if(input.contains(".accept")){
          String[] parts = input.split(" ");
              try{
                  FileWriter fileWriter = new FileWriter(fileName,true);
                   BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                   bufferedWriter.newLine();
                   bufferedWriter.write(clients[findbyname(parts[1])].getname()+" "+clients[findClient(ID)].getname());
                   bufferedWriter.close();
              }catch(IOException ex) {
                System.out.println(
                    "Error writing to file '"
                    + fileName + "'");
                // Or we could just do this:
                // ex.printStackTrace();
                }
      }
      else if(input.contains(".pm")){
          String[] parts = input.split(" ");
          String line = null;
          String fileName = "D:\\Network Programming lab\\MultiChat\\MultiChat\\friend.txt";
          try{
              FileReader fl = new FileReader(fileName);
              BufferedReader bufferedReader = new BufferedReader(fl);
              while((line = bufferedReader.readLine()) != null) {
       
                    if(line.equals(parts[1]+" "+clients[findClient(ID)].getname()) || line.equals(clients[findClient(ID)].getname()+" "+parts[1])){
                        clients[findClient(ID)].setpmID(clients[findbyname(parts[1])].getID());
                        clients[findClient(clients[findbyname(parts[1])].getID())].setpmID(ID);
                        pm = 1;
                        top += 2;
                    }
                }
                bufferedReader.close();  
          }catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
            }
      }
      else if(input.contains(".close")){
          clients[findClient(clients[findClient(ID)].getpmID())].setpmID(-1);
          clients[findClient(ID)].setpmID(-1);
      }
      else if(pm == 1 && clients[findClient(ID)].getpmID() != -1){
        clients[findClient(clients[findClient(ID)].getpmID())].send(clients[findClient(ID)].getname() + ":pm: " + input);
          
      }
      else if(input.contains(".gm")){
          String[] parts = input.split(" ");
          
          clients[findClient(ID)].setGm(1);
          clients[findClient(ID)].setGmC(parts.length);
          int gmid[] = new int[50];
          gmid[0] = findClient(ID);
          for(int i = 1;i <parts.length;i++){
              gmid[i] = findbyname(parts[i]);
          }
          for(int j = 0;j < parts.length;j++){
              clients[gmid[j]].setGmID(gmid);
              clients[gmid[j]].setGm(1);
              clients[gmid[j]].setGmC(parts.length);
          }
      }
      else if(input.contains(".leavegm")){
          for(int j = 0;j < clients[findClient(ID)].getGmC();j++){
            for(int i = 0;i < clients[findClient(ID)].getGmC();i++){
                if(clients[findClient(ID)].getGmID()[j] != -1)
                if(clients[clients[findClient(ID)].getGmID()[j]].getGmID()[i] == findClient(ID)){
                    clients[clients[findClient(ID)].getGmID()[j]].getGmID()[i] = -1;
                    break;
                }
            }
          }
          clients[findClient(ID)].setGm(-1);
          clients[findClient(ID)].setGmC(0);
      }
      else if(clients[findClient(ID)].getGm() == 1){
          int gmc = clients[findClient(ID)].getGmC();
          for(int i = 0;i < gmc;i++){
              if(clients[findClient(ID)].getGmID()[i] != -1)
              clients[clients[findClient(ID)].getGmID()[i]].send(clients[findClient(ID)].getname() + ":gm: " + input);
          }
      }
      else if(input.contains(".online")){
          String online = "";
          for(int i = 0;i < clientCount;i++){
              online = online + " "+clients[i].getname();
          }
          clients[findClient(ID)].send(online);
      }
      else
         for (int i = 0; i < clientCount; i++)
            clients[i].send(clients[findClient(ID)].getname() + ": " + input);   
   }
   public synchronized void remove(int ID)
   {  int pos = findClient(ID);
      if (pos >= 0)
      {  ChatServerThread toTerminate = clients[pos];
         System.out.println("Removing client thread " + ID + " at " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  toTerminate.close(); }
         catch(IOException ioe)
         {  System.out.println("Error closing thread: " + ioe); }
         toTerminate.stop(); }
   }
   private void addThread(Socket socket)
   {  if (clientCount < clients.length)
      {  System.out.println("Client accepted: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
          System.out.println("ID: "+ clients[clientCount].getID());
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); } }
      else
         System.out.println("Client refused: maximum " + clients.length + " reached.");
   }
   public static void main(String args[]) { TCPServer server = null;
         server = new TCPServer(2000); }
}