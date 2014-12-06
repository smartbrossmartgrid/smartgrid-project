package  thesmartbros.sagilbe.classes.agregador;

import java.net.*;
import java.io.*;

public class Agregador {
	public void start(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		boolean listening = true;

		try {
			serverSocket = new ServerSocket(40000);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 40000.");
			System.exit(-1);
		}

		while (listening)
			new MultiServerThread(serverSocket.accept()).start();

		serverSocket.close();
	}

}