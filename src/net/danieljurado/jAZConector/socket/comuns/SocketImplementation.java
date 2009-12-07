package net.danieljurado.jAZConector.socket.comuns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SocketImplementation extends Observable implements
		Runnable {

	protected interface IMessageFilter {
		public boolean exibe(String message);
	}

	private Socket connection;
	private boolean connected = true;
	private BufferedReader bufferedreader;
	private PrintWriter printwriter;

	private IMessageFilter messageFiter = null;

	public SocketImplementation(Socket conexao) {
		super();
		this.connection = conexao;
		try {
			bufferedreader = new BufferedReader(new InputStreamReader(
					this.connection.getInputStream()));
			printwriter = new PrintWriter(this.connection.getOutputStream(),
					true);
		} catch (IOException e) {
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
		}
	}

	protected abstract void commandProccess(String mensagem)
			throws EInvalidTCPCommand;

	public void disconnect() {
		try {
			connection.close();
			connection = null;
		} catch (IOException e) {
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public void run() {
		try {
			// envia("bdGateway()");
			do {
				String s = bufferedreader.readLine();
				try {

					if (messageFiter == null || messageFiter.exibe(s)) {
						Logger.getLogger(getClass().getSimpleName()).info(
								"Recebido: " + s);
					}

					commandProccess(s);
				} catch (EInvalidTCPCommand e) {
					Logger.getLogger(getClass().getSimpleName()).warning(
							"Comando inválido " + e.getMessage());
				} catch (Exception e) {
					Logger.getLogger(getClass().getSimpleName()).log(
							Level.SEVERE, e.getMessage(), e);
				}
			} while (Thread.currentThread().isAlive());
		} catch (Exception e) {
			connected = false;
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
		}

	}

	public void send(String message) {
		if (messageFiter == null || messageFiter.exibe(message)) {
			Logger.getLogger(getClass().getSimpleName()).info(
					"Enviando: " + message);
		}
		try {
			printwriter.print(message + "\r\n");
			printwriter.flush();
		} catch (Exception e) {
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
		}
	}

	public void setMessageFilter(IMessageFilter messageFilter) {
		this.messageFiter = messageFilter;
	}
}
