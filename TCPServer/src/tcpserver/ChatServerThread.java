/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread
{  private TCPServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private int              pmID      = -1;
   private int              gm        = -1;
   private int              gmID[]    = new int[50];
   private int gmC                    = 0;
   private String           name = null;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;

   public ChatServerThread(TCPServer _server, Socket _socket)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      
   }

    public int getGm() {
        return gm;
    }

    public void setGm(int gm) {
        this.gm = gm;
    }

    public int[] getGmID() {
        return gmID;
    }

    public void setGmID(int[] gmID) {
        this.gmID = gmID;
    }

    public int getGmC() {
        return gmC;
    }

    public void setGmC(int gmC) {
        this.gmC = gmC;
    }
   
   public void setpmID(int id){
       pmID = id;
   }
   public int getpmID(){
       return pmID;
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   public int getID()
   {  return ID;
   }
   public String getname(){
       return name;
   }
   public void setname(String _name){
       name = _name;
   }
   public void run()
   {  System.out.println("Server Thread " + ID + " running.");
      while (true)
      {  try
         {  server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   public void open() throws IOException
   {  streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
}