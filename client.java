/*
	Sean Winegar
	14169746
	4/27/2017

	Cleint program to connect to socket server on port 19746 and receive messages sent to server and forwarded back to client
*/
import java.io.*;
import java.net.*;

class client{
	public static void main(String[] args) {
		try{ //Try to connect or print connection terminated
			Socket s = new Socket("127.0.0.1",19746); //server ip and port
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			//successfully connected 
			System.out.println("You have connected. Congrats");
			System.out.println("Please Login: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String msgin="",msgout=""; //msgin = sent message. msgout = received message
			
			
			msgin = br.readLine(); //receive first line from server
			msgout = din.readUTF();
			System.out.println(msgout);
			while(!msgin.equals("logout")){
				//msgout = br.readLine();
				//dout.writeUTF(msgout);
				dout.writeUTF(msgin);
				dout.flush();
				msgout = din.readUTF();
				//msgin = din.readUTF();
				System.out.println(msgout); //printing server msg. Yea it says msgout but its kinda confusing when i already used msgin for input.
				msgin = br.readLine();
			}
		}catch(Exception e){
			System.out.println("Connection terminated");
		}
	}
}











