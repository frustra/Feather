package org.frustra.feather.server.votifer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

public class VotifierLite extends Thread {
	public static List<VoteListener> listeners = new ArrayList<VoteListener>();
	public static PrivateKey privateKey;
	
	public boolean running = false;
	
	public void init() {
		try {
			File folder = new File("votifier");
			if (!folder.exists()) {
				folder.mkdir();

				KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
				RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);
				keygen.initialize(spec);
				
				KeyPair keyPair = keygen.generateKeyPair();

				FileOutputStream out = new FileOutputStream("votifier/public.key");
				out.write(DatatypeConverter.printBase64Binary(keyPair.getPublic().getEncoded()).getBytes());
				out.close();

				privateKey = keyPair.getPrivate();
				out = new FileOutputStream("votifier/private.key");
				out.write(DatatypeConverter.printBase64Binary(privateKey.getEncoded()).getBytes());
				out.close();
			} else {
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				
				File privateKeyFile = new File("votifier/private.key");
				FileInputStream in = new FileInputStream(privateKeyFile);
				byte[] buf = new byte[(int) privateKeyFile.length()];
				in.read(buf);
				in.close();

				buf = DatatypeConverter.parseBase64Binary(new String(buf));
				privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(buf));
			}
		} catch (Exception e) {
			System.err.println("Error reading RSA key");
			e.printStackTrace();
			return;
		}
	}

	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(8192);
			running = true;
			
			while (running) {
				try {
					Socket socket = server.accept();
					socket.setSoTimeout(5000);
					PrintStream out = null;
					InputStream in = null;
					
					try {
						out = new PrintStream(socket.getOutputStream());
						out.println("VOTIFIER 1.9");
						out.flush();
	
						in = socket.getInputStream();
						byte[] buf = new byte[256];
						in.read(buf, 0, buf.length);
	
						Cipher cipher = Cipher.getInstance("RSA");
						cipher.init(Cipher.DECRYPT_MODE, privateKey);
						buf = cipher.doFinal(buf);
						Scanner scan = new Scanner(new ByteArrayInputStream(buf));
						String action = scan.nextLine();
						if (action.equals("VOTE")) {
							String service = scan.nextLine();
							String username = scan.nextLine();
							String address = scan.nextLine();
							String timestamp = scan.nextLine();
							
							for (VoteListener listener : listeners) {
								listener.voteReceived(service, username, address, timestamp);
							}
						}
						scan.close();
					} finally {
						if (in != null) in.close();
						if (out != null) out.close();
						socket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.err.println("Error listening on port 8192");
			e.printStackTrace();
		} finally {
			try {
				if (server != null) server.close();
			} catch (IOException e) {}
		}
	}
	
	public static void addVoteListener(VoteListener listener) {
		listeners.add(listener);
	}
	
	public static void removeVoteListener(VoteListener listener) {
		listeners.remove(listener);
	}
}
