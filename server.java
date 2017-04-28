/*
	Sean Winegar
	14169746
	4/27/2017

	Server program to open a socket server on port 19746 and forward client messages back to client
*/
import java.io.*;
import java.net.*;
import java.util.*;
//import java.lang.Object;

//This is essentially written in c with a little help from java classes
//Have fun reading it ¯\_(ツ)_/¯
class server{
	public static void main(String[] args){	
	while(true){
	
		//Yes I realize I should have used a list Array instead of a String Array but i dont care anymore.
		String[] users = new String[10];//{"Tom","David","Beth"}; //ignore this, i was trying to remember java arrays
		String[] passw = new String[10];//{"Tom11","David22","Beth33"};
		loadFiles(users,passw);
		//ArrayList<String> users = new ArrayList<String>();
		//ArrayList<String> passw = new ArrayList<String>();
		//String line = null;
		//this is the most sloppiest/non-secure way of reading users and passwords but idrgaf
/*		try {
			//read users file into array
			FileReader fileReader = new FileReader("users.txt"); // read user file
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			while((line = bufferedReader.readLine()) != null) {
				users[i] = line;
//				System.out.println(users[i]);
				i++;
			}
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex){
			System.out.println("Could not find users file");
		}
		catch(IOException ex) {
			System.out.println("Error reading file");
		}
		try {
			//read password file and store to array
			FileReader fileReader = new FileReader("pass.txt"); //read pass file
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			while((line = bufferedReader.readLine()) != null) {
				passw[i] = line;
//				System.out.println(passw[i]);
				i++;
			}
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex){
			System.out.println("Could not find pass file");
		}
		catch(IOException ex) {
			System.out.println("Error reading file");
		}
*/
//This entire block of code became my load function, but it has sentimental value to my fingers as I took hours writing it in this spot so it stays


		//now the fun part. open a socket and start communicating with the client
		try{
		
			ServerSocket ss = new ServerSocket(19746); //open socket on port 19746 
			System.out.println("The chat server is running.");
//			System.out.println(Arrays.toString(users)); //testing to see if the user array is populated

			Socket s = ss.accept();
			DataInputStream din = new DataInputStream(s.getInputStream());
			DataOutputStream dout = new DataOutputStream(s.getOutputStream()); //establish IO stream
			System.out.println("Client Connected");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));// create buffer to read

			String msgin="",msgout="";
			dout.writeUTF(msgout);
			dout.flush();
			
			boolean session = false; // session variable to determine if a user is logged in. I tried instantiating a user object from a user class to store session and name but ¯\_(ツ)_/¯ it didnt work.
			String user = ""; //user input for username
			String newuser = "";
			String pass = ""; //user input for password
			String send = ""; //place holder string to send back to client after modification
			int upIndex=999; //user/password index
			int exists=0; //int to check if input for user or pass exists in user or pass string arrays
			int usersize = users.length; //size of users, also size of passwords
			
			//Main control block. Im lazy so its a bit messy stick with me here.
			while(!msgin.equals("end")){
				
				msgin = din.readUTF();
//login code starts here
				if(msgin.equals("login")){ //if user sends login command
					if(session == true){ //If user is logged in, they cant login again, duh
						dout.writeUTF("Server: You are already logged in."); //send message to client
						dout.flush(); //flush message buffer
					}
					else{ //well they aint logged in so...
						dout.writeUTF("User: ");
						dout.flush();
						user = din.readUTF(); //read in client username
						dout.writeUTF("Pass: ");
						dout.flush();
						pass = din.readUTF(); //read in password
						//exists = users.indexOf(user);
						for(int j=0; j<usersize;j++){
							if(users[j] == null){ //break out of loop or it breaks if null
								break;
							}
							if(users[j].equals(user)){ //find user in user file
								exists = 1; //
								upIndex=j; //index of user
								break;
							}
						}
						if(passw[upIndex].equals(pass)){ //check for password and login
							session = true;
							System.out.println(user + " login");
							dout.writeUTF("Server: " + user +" joins");
							dout.flush();
						}
						else{ //you dun messed up A-a-ron
							dout.writeUTF("Server: Username or Password Incorrect. Try again:");
							dout.flush(); //flush message buffer
						}
					}
					br.close();
				}
//send code starts here
				else if(msgin.equals("send")){
					if(session == false){ //if not logged in
						dout.writeUTF("Server: Denied. Please login first: ");
						dout.flush();
					}
					else{
						dout.writeUTF("Message: "); //Write on client side
						dout.flush(); //flush stream
						send = din.readUTF(); //create send string
						System.out.println(user + ": " + send); //print username + send string serverside
						dout.writeUTF(user + ": " + send); //send message back to client
						dout.flush(); //flush stream
					}
				}
				
//newuser code starts here
				else if(msgin.equals("newuser")){
					dout.writeUTF("Enter new user: ");
					dout.flush();
					newuser = din.readUTF(); //new username

					do{ //loop until name doesnt exist. Also only allow less than 32 chars
						while(user.length() >= 32){ //loop until a proper name length is given
							dout.writeUTF("Username must be less than 32 characters:  ");
							dout.flush();
							newuser = din.readUTF();
						}
						exists=0;
						usersize = users.length; //get number of users
						for(int j=0; j<usersize;j++){  // check to see if username exists
							if(users[j] == null){ //break out of loop or it breaks if null
								break;
							}
							if(users[j].equals(newuser)){ //user exists already
								exists = 1; //set flag to exists
								//upIndex=j;
								break;
							}
						}
						if(exists == 1){
							dout.writeUTF("Server: Username already exists. Choose new username: ");
							dout.flush();
							newuser = din.readUTF(); //reread new username
						}
					}while(exists == 1); //keep goin til its a name that doesnt exist already
					
					dout.writeUTF("Password: ");
					dout.flush();
					pass = din.readUTF(); //read in new password
					while(pass.length() < 4 || pass.length() > 8){ //check for proper length
						dout.writeUTF("Server: Password must be between 4 and 8.\nPassword: ");
						dout.flush();
						pass = din.readUTF(); //reread new password
					}
					try {
						//write new username to users.txt
						FileWriter fileWriter = new FileWriter("users.txt",true);
						BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
						bufferedWriter.newLine();
						bufferedWriter.write(newuser);
						bufferedWriter.close();         
					}
					catch(FileNotFoundException ex){
						System.out.println("Could not find users file");
					}
					catch(IOException ex) {
						System.out.println("Error reading users file");
					}
					try {
						//write new password to pass.txt
						FileWriter fileWriter = new FileWriter("pass.txt",true);
						BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
						bufferedWriter.newLine();
						bufferedWriter.write(pass);
						bufferedWriter.close();         
					}
					catch(FileNotFoundException ex){
						System.out.println("Could not find pass file");
					}
					catch(IOException ex) {
						System.out.println("Error reading pass file");
					}
					dout.writeUTF("User " + newuser + " created."); //hooray
					dout.flush();
					System.out.println("User " + newuser + " created");
					loadFiles(users,passw);
				}
				else if(msgin.equals("logout2")){
					session = false;
					System.out.println(user + " logout");
					dout.writeUTF("Server: " + user + " left");
					dout.flush();
					user = "";
				}
//default case
				else{
					dout.writeUTF("Invalid Command. Try again:");	//Without these two lines of code, you crash when you type an invalid command
					dout.flush();									//Behold the power of the code
				}
			}
			s.close();
			
		}catch(Exception e){
			//System.out.println("Danger Will Robinson! Danger! Chat Server is broken");
		}
	}
	}
	public static void loadFiles(String[] users, String[] passw){
		String line = null;
		//this is the most sloppiest/non-secure way of reading users and passwords but idrgaf
		try {
			//read users file into array
			FileReader fileReader = new FileReader("users.txt"); // read user file
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			while((line = bufferedReader.readLine()) != null) {
				users[i] = line;
//				System.out.println(users[i]);
				i++;
			}
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex){
			System.out.println("Could not find users file");
		}
		catch(IOException ex) {
			System.out.println("Error reading file");
		}
		try {
			//read password file and store to array
			FileReader fileReader = new FileReader("pass.txt"); //read pass file
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			while((line = bufferedReader.readLine()) != null) {
				passw[i] = line;
//				System.out.println(passw[i]);
				i++;
			}
			bufferedReader.close();         
		}
		catch(FileNotFoundException ex){
			System.out.println("Could not find pass file");
		}
		catch(IOException ex) {
			System.out.println("Error reading file");
		}
	}
}