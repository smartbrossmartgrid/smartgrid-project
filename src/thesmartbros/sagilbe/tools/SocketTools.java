package thesmartbros.sagilbe.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTools {

	public static synchronized boolean send(String IP, int port, String message) {
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket(IP, port);
			if (socket.isClosed())
				return false;
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			message = Encrip_Decrip.getInstance().encrypt(message);
			out.print(message);
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				outstream.close();
				socket.close();
				PrinterTools.socketLog(socket + " has been closed");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static synchronized Socket sendSynchronized(String IP, int port, String message) {
		Socket socket = null;
		try {
			socket = new Socket(IP, port);
			sendSynchronized(socket, message);
			return socket;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized boolean sendSynchronized(Socket socket, String message) {
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			out.print(message);
			if (socket.isClosed())
				PrinterTools.socketLog(socket + " has closed");
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
				outstream.close();
				PrinterTools.socketLog(socket + " has been closed");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String getJSON(Socket socket) {
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			if (socket.isClosed())
				return "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			//socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getJSON(Socket socket, boolean close) {
		String line = getJSON(socket);
		try {
			if (close) {
				socket.close();
				PrinterTools.socketLog(socket + " has been closed");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
}
