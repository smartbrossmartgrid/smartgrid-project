package thesmartbros.sagilbe.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

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
			message = SignatureSAGILBE.getInstance().GenSig(message);
			PrinterTools.printJSON(message);
			message = SymmetricEncrypt.getInstance().encryptData(message);
			PrinterTools.printEJSON(message);
			out.print(message);
			return true;
		} catch (UnknownHostException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} finally {
			try {
				out.close();
				outstream.close();
				socket.close();
				PrinterTools.socketLog(socket + " has been closed");
			} catch (IOException e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
			} catch (Exception e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
			}
		}
		return false;
	}

	public static synchronized boolean sendClean(String IP, int port, String message) {
		Socket socket = null;
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			socket = new Socket(IP, port);
			if (socket.isClosed())
				return false;
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			PrinterTools.printJSON(message);
			out.print(message);
			return true;
		} catch (UnknownHostException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} finally {
			try {
				out.close();
				outstream.close();
				socket.close();
				PrinterTools.socketLog(socket + " has been closed");
			} catch (IOException e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
			} catch (Exception e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
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
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		}
		return null;
	}

	public static synchronized boolean sendSynchronized(Socket socket, String message) {
		OutputStream outstream = null;
		PrintWriter out = null;
		try {
			outstream = socket.getOutputStream();
			out = new PrintWriter(outstream);
			message = SignatureSAGILBE.getInstance().GenSig(message);
			PrinterTools.printJSON(message);
			message = SymmetricEncrypt.getInstance().encryptData(message);
			PrinterTools.printEJSON(message);
			out.print(message);
			if (socket.isClosed())
				PrinterTools.socketLog(socket + " has closed");
			return true;
		} catch (UnknownHostException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} finally {
			try {
				out.close();
				outstream.close();
				PrinterTools.socketLog(socket + " has been closed");
			} catch (IOException e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
			} catch (Exception e) {
				PrinterTools.errorsLog("ERROR: " + e.getMessage());
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
			String message = sb.toString();
			message = SymmetricEncrypt.getInstance().decryptData(message);
			JSONObject jsonObject = new JSONObject(message);
			String signature = jsonObject.getString("signature");
			SignatureSAGILBE.getInstance().VerSig(message, signature);
			return message;
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (Exception e) {
			//PrinterTools.errorsLog("ERROR: " + e.getMessage());
		}
		return sb.toString();
	}

	public static String getJSONClean(Socket socket) {
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			if (socket.isClosed())
				return "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while ((line = reader.readLine()) != null)
				sb.append(line).append("\n");
			//socket.close();
			return sb.toString();
		} catch (IOException e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		} catch (Exception e) {
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
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
			PrinterTools.errorsLog("ERROR: " + e.getMessage());
		}
		return line;
	}
}
