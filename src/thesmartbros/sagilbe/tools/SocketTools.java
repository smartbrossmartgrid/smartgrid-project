package thesmartbros.sagilbe.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTools {

	public static boolean send(String IP, int port, String message) {
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket(IP, port);
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			out.print(message);
			return true;
		} catch (UnknownHostException e) {
			System.err.print(e);
		} catch (IOException e) {
			System.err.print(e);
		} finally {
			try {
				out.close();
				outstream.close();
				socket.close();
			} catch (IOException e) {
				System.err.print(e);
			} catch (Exception e) {
				System.err.print(e);
			}
		}
		return false;
	}
	
	public static String getJSON(Socket socket) {
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			socket.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		return sb.toString();
	}
}
