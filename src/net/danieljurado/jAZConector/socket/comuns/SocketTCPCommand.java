package net.danieljurado.jAZConector.socket.comuns;

import java.net.Socket;
import java.util.HashMap;

public class SocketTCPCommand extends SocketImplementation {

	protected HashMap<String, ITCPCommandProccess> validCommands = new HashMap<String, ITCPCommandProccess>();

	public SocketTCPCommand(Socket conexao) {
		super(conexao);
	}

	@Override
	protected void commandProccess(String message) throws EInvalidTCPCommand {
		TCPCommand comando = new TCPCommand(message);
		if (!validCommands.containsKey(comando.getCommand().toUpperCase())) {
			throw new EInvalidTCPCommand(message);
		}

		ITCPCommandProccess commandProcessor = validCommands.get(comando
				.getCommand().toUpperCase());
		commandProcessor.commandProccess(this, comando);
	}

}
