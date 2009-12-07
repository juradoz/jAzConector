package net.danieljurado.jAZConector.socket.comuns;

public class TCPCommand {

	public static String getCommand(String message) {
		if (message.indexOf("(") > 0) {
			String s = message.substring(0, message.indexOf("("));
			return s;
		} else {
			return message;
		}
	}

	public static String[] getParameter(String message) {
		String[] s = "".split(";");
		int i = message.indexOf("(");
		int f = message.indexOf(")");
		if (i > 0 && f > 0) {
			String sub = message.substring(i + 1, f);
			s = sub.split(";");
		}
		return s;
	}

	private String message;

	public TCPCommand(String message) {
		this.message = message;
	}

	public String getCommand() {
		return getCommand(getMessage());
	}

	public String getMessage() {
		return message;
	}

	public void getMessage(String message) {
		this.message = message;
	}

	public String[] getParameters() {
		return getParameter(getMessage());
	}
}
