package net.danieljurado.jAZConector.socket;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import net.danieljurado.jAZConector.JAzConector;
import net.danieljurado.jAZConector.socket.comuns.ITCPCommandProccess;
import net.danieljurado.jAZConector.socket.comuns.SocketTCPCommand;
import net.danieljurado.jAZConector.socket.comuns.TCPCommand;
import net.danieljurado.jAZConector.socket.events.Event;
import net.danieljurado.jAZConector.socket.events.EventCallCleared;
import net.danieljurado.jAZConector.socket.events.EventCallDelivered;
import net.danieljurado.jAZConector.socket.events.EventCallEstablished;
import net.danieljurado.jAZConector.socket.events.EventConnectionCleared;
import net.danieljurado.jAZConector.socket.events.EventMakeCallConf;

public class AzSocket extends SocketTCPCommand {

	private static class AzSocketHolder {
		private static AzSocket instance = null;
	}

	public static AzSocket getInstance() {
		return AzSocketHolder.instance;
	}

	public static AzSocket getInstance(Socket conexao) {
		AzSocketHolder.instance = new AzSocket(conexao);
		return AzSocketHolder.instance;
	}

	public static final int esNotReady = 0;

	public static final int esIdle = 1;

	public static final int esErro = 2;

	public static final int esLogar = 3;

	public static final int esLogarOK = 4;

	public static final int esLogarErro = 5;

	public static final int esLogarConf = 6;

	public static final int esDeslogar = 7;

	public static final int esDeslogarOK = 8;

	public static final int esDeslogarErro = 9;

	public static final int esDeslogarConf = 10;

	public static final int esReady = 11;

	public static final int esReadyErro = 13;

	public static final int esReadyConf = 14;

	public static final int esNotReadyOk = 15;

	public static final int esNotReadyErro = 17;

	public static final int esNotReadyConf = 18;

	public static final int esDesligar = 19;

	public static final int esDesligarErro = 21;

	public static final int esDesligarConf = 22;

	public static final int esAtender = 23;

	public static final int esAtenderErro = 25;

	public static final int esAtenderConf = 26;

	public static final int esDiscar = 30;

	public static final int esDiscarErro = 31;

	public static final int esDiscarConf = 32;

	private static final String COMANDO_DESLIGAR = "DESLIGAR(%s;%d)";
	private static final String COMANDO_LOGAR = "LOGAR(%s;%s;%s;%s)";
	private static final String COMANDO_DESLOGAR = "DESLOGAR(%s;%s;%s)";
	private static final String COMANDO_DISCAR = "DISCAR(%s;%s;%s)";
	private static final String COMANDO_PAUSAR = "PAUSAR(%s;%s;%s;%d)";
	private static final String COMANDO_READY = "READY(%s;%s;%s)";

	public int estadoSocket = esNotReady;

	private class ProcessaUniversalError implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			switch (((AzSocket) socket).getEstadoSocket()) {
			case esAtender:
				((AzSocket) socket).setEstadoSocket(esAtenderErro);
				break;
			case esDesligar:
				((AzSocket) socket).setEstadoSocket(esDesligarErro);
				break;
			case esDeslogar:
				((AzSocket) socket).setEstadoSocket(esDeslogarErro);
				break;
			case esDiscar:
				((AzSocket) socket).setEstadoSocket(esDiscarErro);
				break;
			case esLogar:
				((AzSocket) socket).setEstadoSocket(esLogarErro);
				break;
			case esNotReady:
				((AzSocket) socket).setEstadoSocket(esNotReadyErro);
				break;
			case esReady:
				((AzSocket) socket).setEstadoSocket(esReadyErro);
				break;
			}
		}
	}

	private class ProcessaTSocketManager implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaCtDialer implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esIdle);
		}
	}

	private class ProcessaDiscarOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaDiscarConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			int callId = -1;
			for (int i = 0; i < comando.getParameters().length; i++) {
				switch (i) {
				case 1:
					callId = Integer.parseInt(comando.getParameters()[i]);
					break;
				}
			}
			EventMakeCallConf eventMakeCallConf = new EventMakeCallConf(callId,
					-1);
			addEvent(eventMakeCallConf);
			setChanged();
			notifyObservers(eventMakeCallConf);
			((AzSocket) socket).setEstadoSocket(esDiscarConf);
		}
	}

	private class ProcessaDiscarErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esDiscarErro);
		}
	}

	private class ProcessaDesligarOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaDesligarConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esDesligarConf);
		}
	}

	private class ProcessaDesligarErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esDesligarErro);
		}
	}

	private class ProcessaLogarOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaLogarConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esLogarConf);
		}
	}

	private class ProcessaLogarErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esLogarErro);
		}
	}

	private class ProcessaDeslogarOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaDeslogarConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esDeslogarConf);
		}
	}

	private class ProcessaDeslogarErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esDeslogarErro);
		}
	}

	private class ProcessaNotReadyOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaNotReadyConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esNotReadyConf);
		}
	}

	private class ProcessaNotReadyErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esNotReadyErro);
		}
	}

	private class ProcessaReadyOk implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
		}
	}

	private class ProcessaReadyConf implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esReadyConf);
		}
	}

	private class ProcessaReadyErro implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			((AzSocket) socket).setEstadoSocket(esReadyErro);
		}
	}

	private class ProcessaCallCleared implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			int callId = -1, cause = -1;
			for (int i = 0; i < comando.getParameters().length; i++) {
				switch (i) {
				case 1:
					callId = Integer.parseInt(comando.getParameters()[i]);
					break;
				case 3:
					cause = Integer.parseInt(comando.getParameters()[i]);
					break;
				}
			}
			EventCallCleared eventCallCleared = new EventCallCleared(callId,
					cause);
			addEvent(eventCallCleared);
			setChanged();
			notifyObservers(eventCallCleared);
		}
	}

	private class ProcessaConnectionCleared implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			int callId = -1, cause = -1;
			for (int i = 0; i < comando.getParameters().length; i++) {
				switch (i) {
				case 1:
					callId = Integer.parseInt(comando.getParameters()[i]);
					break;
				case 5:
					cause = Integer.parseInt(comando.getParameters()[i]);
					break;
				}
			}
			EventConnectionCleared eventConnectionCleared = new EventConnectionCleared(
					callId, cause);
			addEvent(eventConnectionCleared);
			setChanged();
			notifyObservers(eventConnectionCleared);
		}
	}

	private class ProcessaCallDelivered implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			int callId = -1;
			String ani = "", userInfo = "", dnis = "";
			for (int i = 0; i < comando.getParameters().length; i++) {
				switch (i) {
				case 1:
					callId = Integer.parseInt(comando.getParameters()[i]);
					break;
				case 4:
					ani = comando.getParameters()[i];
					break;
				case 13:
					userInfo = comando.getParameters()[i];
					break;
				case 5:
					dnis = comando.getParameters()[i];
					break;
				}
			}
			EventCallDelivered eventCallDelivered = new EventCallDelivered(
					callId, ani, userInfo, "", dnis);
			addEvent(eventCallDelivered);
			setChanged();
			notifyObservers(eventCallDelivered);
		}
	}

	private class ProcessaCallEstablished implements ITCPCommandProccess {
		@Override
		public void commandProccess(SocketTCPCommand socket, TCPCommand comando) {
			int callId = -1;
			String ani = "", userInfo = "", dnis = "";
			for (int i = 0; i < comando.getParameters().length; i++) {
				switch (i) {
				case 1:
					callId = Integer.parseInt(comando.getParameters()[i]);
					break;
				case 4:
					ani = comando.getParameters()[i];
					break;
				case 12:
					userInfo = comando.getParameters()[i];
					break;
				case 5:
					dnis = comando.getParameters()[i];
					break;
				}
			}
			EventCallEstablished eventCallEstablished = new EventCallEstablished(
					callId, ani, userInfo, "", dnis);
			addEvent(eventCallEstablished);
			setChanged();
			notifyObservers(eventCallEstablished);
		}
	}

	public AzSocket(Socket conexao) {
		super(conexao);
		validCommands.put("UniversalError".toUpperCase(),
				new ProcessaUniversalError());
		validCommands.put("CallCleared".toUpperCase(),
				new ProcessaCallCleared());
		validCommands.put("ConnectionCleared".toUpperCase(),
				new ProcessaConnectionCleared());
		validCommands.put("CallDelivered".toUpperCase(),
				new ProcessaCallDelivered());
		validCommands.put("CallEstablished".toUpperCase(),
				new ProcessaCallEstablished());
		validCommands.put("TSocketManager".toUpperCase(),
				new ProcessaTSocketManager());
		validCommands.put("CtDialer".toUpperCase(), new ProcessaCtDialer());
		validCommands.put("DesligarOk".toUpperCase(), new ProcessaDesligarOk());
		validCommands.put("DesligarConf".toUpperCase(),
				new ProcessaDesligarConf());
		validCommands.put("DesligarErro".toUpperCase(),
				new ProcessaDesligarErro());
		validCommands.put("DiscarOk".toUpperCase(), new ProcessaDiscarOk());
		validCommands.put("DiscarConf".toUpperCase(), new ProcessaDiscarConf());
		validCommands.put("DiscarErro".toUpperCase(), new ProcessaDiscarErro());
		validCommands.put("LogarOk".toUpperCase(), new ProcessaLogarOk());
		validCommands.put("LogarConf".toUpperCase(), new ProcessaLogarConf());
		validCommands.put("LogarErro".toUpperCase(), new ProcessaLogarErro());
		validCommands.put("DeslogarOk".toUpperCase(), new ProcessaDeslogarOk());
		validCommands.put("DeslogarConf".toUpperCase(),
				new ProcessaDeslogarConf());
		validCommands.put("DeslogarErro".toUpperCase(),
				new ProcessaDeslogarErro());

		validCommands.put("PausarOk".toUpperCase(), new ProcessaNotReadyOk());
		validCommands.put("PausarConf".toUpperCase(),
				new ProcessaNotReadyConf());
		validCommands.put("PausarErro".toUpperCase(),
				new ProcessaNotReadyErro());

		validCommands.put("ReadyOk".toUpperCase(), new ProcessaReadyOk());
		validCommands.put("ReadyConf".toUpperCase(), new ProcessaReadyConf());
		validCommands.put("ReadyErro".toUpperCase(), new ProcessaReadyErro());
	}

	public boolean alternateCall(int activeCallId, int otherCallId,
			String station) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean answerCall(String text, int callId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean clearCall(String station, int callId) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}

		if (station.length() == 0 || callId < 0) {
			return false;
		}

		String comando = String.format(COMANDO_DESLIGAR, station, callId);

		setEstadoSocket(esDesligar);
		try {
			send(comando);
			return true;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean conferenceCall(int heldCallId, int activeCallId,
			String station) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean consultationCall(int activeCallId, String station,
			String destination, String userInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	public int getEstadoSocket() {
		return this.estadoSocket;
	}

	private String lastANI = "";

	public String getLastANI() {
		return lastANI;
	}

	private int lastCallId = -1;

	public int getLastCallId() {
		return lastCallId;
	}

	private int lastCause = -1;

	public int getLastCause() {
		return lastCause;
	}

	private String lastDNIS = "";

	public String getLastDNIS() {
		return lastDNIS;
	}

	private ArrayList<Event> events = new ArrayList<Event>();

	private void setLastANI(String lastANI) {
		this.lastANI = lastANI;
	}

	private void setLastDNIS(String lastDNIS) {
		this.lastDNIS = lastDNIS;
	}

	private String lastUserInfo = "";

	public String getLastUserInfo() {
		return lastUserInfo;
	}

	private void setLastUserInfo(String lastUserInfo) {
		this.lastUserInfo = lastUserInfo;
	}

	private String lastUserDetails = "";

	public String getLastUserDetails() {
		return lastUserDetails;
	}

	private void setLastUserDetails(String lastUserDetails) {
		this.lastUserDetails = lastUserDetails;
	}

	private void setLastCallId(int lastCallId) {
		this.lastCallId = lastCallId;
	}

	private void setLastCause(int lastCause) {
		this.lastCause = lastCause;
	}

	public int getLastEvent() {
		synchronized (events) {
			setLastANI("");
			setLastDNIS("");
			setLastUserInfo("");
			setLastUserDetails("");
			setLastCallId(-1);
			setLastCause(-1);

			if (events.size() == 0) {
				return JAzConector.EV_NONE;
			}

			if (events.get(0) instanceof EventCallDelivered) {
				EventCallDelivered event = (EventCallDelivered) events.get(0);
				setLastCallId(event.getCallId());
				setLastANI(event.getAni());
				setLastDNIS(event.getDnis());
				setLastUserInfo(event.getUserInfo());
				setLastUserDetails(event.getUserDetails());
				events.remove(0);
				Logger.getLogger(getClass().getSimpleName()).info(
						"Entregando evento EV_CALLDELIVERED codigo "
								+ JAzConector.EV_CALLDELIVERED);
				return JAzConector.EV_CALLDELIVERED;
			}

			if (events.get(0) instanceof EventCallEstablished) {
				EventCallEstablished event = (EventCallEstablished) events
						.get(0);
				setLastCallId(event.getCallId());
				setLastANI(event.getAni());
				setLastDNIS(event.getDnis());
				setLastUserInfo(event.getUserInfo());
				setLastUserDetails(event.getUserDetails());
				events.remove(0);
				Logger.getLogger(getClass().getSimpleName()).info(
						"Entregando evento EV_CALLESTABLISHED codigo "
								+ JAzConector.EV_CALLESTABLISHED);
				return JAzConector.EV_CALLESTABLISHED;
			}

			if (events.get(0) instanceof EventConnectionCleared) {
				EventConnectionCleared event = (EventConnectionCleared) events
						.get(0);
				setLastCallId(event.getCallId());
				setLastCause(event.getCause());
				events.remove(0);
				Logger.getLogger(getClass().getSimpleName()).info(
						"Entregando evento EV_CONNECTIONCLEARED codigo "
								+ JAzConector.EV_CONNECTIONCLEARED);
				return JAzConector.EV_CONNECTIONCLEARED;
			}

			if (events.get(0) instanceof EventCallCleared) {
				EventCallCleared event = (EventCallCleared) events.get(0);
				setLastCallId(event.getCallId());
				setLastCause(event.getCause());
				events.remove(0);
				Logger.getLogger(getClass().getSimpleName()).info(
						"Entregando evento EV_CALLCLEARED codigo "
								+ JAzConector.EV_CALLCLEARED);
				return JAzConector.EV_CALLCLEARED;
			}

			if (events.get(0) instanceof EventMakeCallConf) {
				EventMakeCallConf event = (EventMakeCallConf) events.get(0);
				setLastCallId(event.getCallId());
				events.remove(0);
				Logger.getLogger(getClass().getSimpleName()).info(
						"Entregando evento EV_MAKECALLCONF codigo "
								+ JAzConector.EV_MAKECALLCONF);
				return JAzConector.EV_MAKECALLCONF;
			}
		}
		return JAzConector.EV_UNKNOWN;
	}

	public boolean holdCall(int activeCallId, String station) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean logIn(String ramal, String agente, String grupo, String senha) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}

		String comando = String.format(COMANDO_LOGAR, ramal, agente, grupo,
				senha);

		setEstadoSocket(esLogar);
		try {
			send(comando);

			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 10);
			while (estadoSocket != esErro && estadoSocket != esLogarConf
					&& estadoSocket != esLogarErro
					&& timeout.after(Calendar.getInstance())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
			return getEstadoSocket() == esLogarConf;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean logOut(String ramal, String agente, String grupo) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}

		String comando = String.format(COMANDO_DESLOGAR, ramal, agente, grupo);

		setEstadoSocket(esDeslogar);
		try {
			send(comando);

			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 10);
			while (estadoSocket != esErro && estadoSocket != esDeslogarConf
					&& estadoSocket != esDeslogarErro
					&& timeout.after(Calendar.getInstance())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
			return getEstadoSocket() == esDeslogarConf;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean makeCall(String station, String destination, String userInfo) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}
		String comando = String.format(COMANDO_DISCAR, station, destination,
				userInfo);
		setEstadoSocket(esDiscar);
		try {
			send(comando);

			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 10);
			while (estadoSocket != esErro && estadoSocket != esDiscarConf
					&& estadoSocket != esDiscarErro
					&& timeout.after(Calendar.getInstance())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
			return getEstadoSocket() == esDiscarConf;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean notReady(String ramal, String agente, String grupo,
			int motivo) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}

		String comando = String.format(COMANDO_PAUSAR, ramal, agente, grupo,
				motivo);

		setEstadoSocket(esNotReady);
		try {
			send(comando);

			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 10);
			while (estadoSocket != esErro && estadoSocket != esNotReadyConf
					&& estadoSocket != esNotReadyErro
					&& timeout.after(Calendar.getInstance())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
			return getEstadoSocket() == esNotReadyConf;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean ready(String ramal, String agente, String grupo) {
		if (!isConnected() && estadoSocket != esNotReady) {
			return false;
		}

		String comando = String.format(COMANDO_READY, ramal, agente, grupo);

		setEstadoSocket(esReady);
		try {
			send(comando);

			Calendar timeout = Calendar.getInstance();
			timeout.add(Calendar.SECOND, 10);
			while (estadoSocket != esErro && estadoSocket != esReadyConf
					&& estadoSocket != esReadyErro
					&& timeout.after(Calendar.getInstance())) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
			return getEstadoSocket() == esReadyConf;
		} finally {
			setEstadoSocket(esIdle);
		}
	}

	public boolean retrieveCall(int heldCallId, String station) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setEstadoSocket(int estadoSocket) {
		this.estadoSocket = estadoSocket;
	}

	public boolean transferCall(int heldCallId, int activeCallId, String station) {
		// TODO Auto-generated method stub
		return false;
	}

	private void addEvent(Event event) {
		Logger.getLogger(getClass().getSimpleName()).info(
				"Registrando evento " + event.getClass().getSimpleName());
		synchronized (events) {
			while (events.size() > 1000) {
				Logger.getLogger(getClass().getSimpleName()).warning(
						"Removendo evento por limpeza de buffer.");
				events.remove(0);
			}
			events.add(event);
		}
	}
}
