package net.danieljurado.jAZConector;

import java.applet.Applet;
import java.awt.Rectangle;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;

import net.danieljurado.jAZConector.socket.AzSocket;

/**
 * Applet de integração Gennex. Este applet deve ser utilizado para estabelecer
 * uma conexão com o Gennex através de uma página WEB.
 * 
 * @author Daniel Jurado
 * 
 */
public class JAzConector extends Applet {

	private class ConnectionMonitor implements Runnable {

		private JAzConector azConector = null;

		private boolean isStopped = false;

		private ConnectionMonitor() {

		}

		private boolean checkConnection() {
			if (AzSocket.getInstance() != null
					&& AzSocket.getInstance().isConnected()) {
				return true;
			}

			InetAddress addr = null;
			try {
				addr = InetAddress.getByName(this.azConector.getServer());
			} catch (UnknownHostException e) {
				Logger.getLogger(getClass().getSimpleName()).warning(
						"Host not found " + azConector.getServer());

				return false;
			}
			SocketAddress sockaddr = new InetSocketAddress(addr,
					this.azConector.getPort());
			Socket sock = new Socket();
			Logger.getLogger(getClass().getSimpleName()).info(
					String.format("Conectando a %s:%d", this.azConector
							.getServer(), this.azConector.getPort()));

			try {
				sock.connect(sockaddr, 2000);
			} catch (Exception e) {
				Logger.getLogger(getClass().getSimpleName()).warning(
						String.format("Erro na conexao a %s:%d",
								this.azConector.getServer(), this.azConector
										.getPort()));
				return false;
			}

			Logger.getLogger(getClass().getSimpleName()).info(
					String.format("Conectado com sucesso a %s:%d!",
							this.azConector.getServer(), this.azConector
									.getPort()));
			AzSocket.getInstance(sock);
			AzSocket.getInstance().addObserver(getEventObserver());
			new Thread(AzSocket.getInstance()).start();
			return true;
		}

		@Override
		public void run() {
			isStopped = false;
			do {
				try {
					if (!checkConnection()) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {

						}
						continue;
					}
				} finally {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {

					}
				}
			} while (Thread.currentThread().isAlive() && !isStopped);
		}

		void setAzConector(JAzConector azConector) {
			this.azConector = azConector;
		}

		private void stop() {
			isStopped = true;
		}
	}

	private class EventObserver extends Observable implements Observer {

		@Override
		public void update(Observable arg0, Object arg1) {
			setChanged();
			notifyObservers(arg1);
		}

	}

	private EventObserver eventObserver = new EventObserver();

	private static final long serialVersionUID = 1L;

	private String server = null;
	private int port = 0;

	/**
	 * Agente deslogado.
	 */
	public static final int AS_LOGGEDOUT = 0;

	/**
	 * Agente livre para atendimento
	 */
	public static final int AS_READY = 1;

	/**
	 * Agente em pausa.
	 */
	public static final int AS_NOTREADY = 2;
	/**
	 * Agente em After Call Work (Clerical).
	 */
	public static final int AS_ACW = 3;
	/**
	 * Agente com ramal ringando
	 */
	public static final int AS_RINGING = 4;
	/**
	 * Agente em atendimento.
	 */
	public static final int AS_BUSY = 5;
	/**
	 * Agente em atendimento.
	 */
	public static final int AS_HOLD = 6;
	private int agentState = AS_LOGGEDOUT;

	/**
	 * Causa de desligamento indefinida.
	 */
	public static final int CS_UNDEFINED = 0;

	/**
	 * Desligamento por destino ocupado.
	 */
	public static final int CS_BUSY = 2;

	/**
	 * Desligamento por congestionamento na central.
	 */
	public static final int CS_NETWORKCONGESTION = 3;

	/**
	 * Desligamento por erro de sinalização telefônica.
	 */
	public static final int CS_NETWORKERROR = 6;

	/**
	 * Desligamento por número inexistente.
	 */
	public static final int CS_INVALIDNUMBER = 7;

	/**
	 * Desligamento por falta de agentes para encaminhamento da chamada.
	 */
	public static final int CS_NOAGENTS = 8;

	/**
	 * Desligamento por não atendimento do destino.
	 */
	public static final int CS_NOANS = 10;

	JLabel lblServidor;

	private ConnectionMonitor connectionMonitor = null;

	private Calendar ultimaMudancaEstado = Calendar.getInstance();

	/**
	 * Evento desconhecido.
	 */
	public static final int EV_UNKNOWN = -1;

	/**
	 * Nenhum evento recebido.
	 */
	public static final int EV_NONE = 0;

	/**
	 * Nova chamada recebida no ramal.
	 * 
	 * @see #getLastCallId()
	 * @see #getLastANI()
	 * @see #getLastDNIS()
	 * @see #getLastUserInfo()
	 */
	public static final int EV_CALLDELIVERED = 1;

	/**
	 * Chamada no ramal atendida.
	 * 
	 * @see #getLastCallId()
	 * @see #getLastANI()
	 * @see #getLastDNIS()
	 * @see #getLastUserInfo()
	 */
	public static final int EV_CALLESTABLISHED = 2;

	/**
	 * Conexao de chamada encerrada.
	 * 
	 * @see #getLastCallId()
	 * @see #getLastCause()
	 */
	public static final int EV_CONNECTIONCLEARED = 3;

	/**
	 * Chamada encerrada.
	 * 
	 * @see #getLastCallId()
	 * @see #getLastCause()
	 */
	public static final int EV_CALLCLEARED = 4;

	/**
	 * Confirmação de nova chamada realizada.
	 * 
	 * @see #getLastCallId()
	 */
	public static final int EV_MAKECALLCONF = 5;

	/**
	 * Versao atual do componente: 1.0.5
	 */
	public static final String VERSION = "1.0.5";

	private String agentStateToStr(int agentState) {
		switch (agentState) {
		case AS_ACW:
			return "AS_ACW";
		case AS_BUSY:
			return "AS_BUSY";
		case AS_HOLD:
			return "AS_HOLD";
		case AS_LOGGEDOUT:
			return "AS_LOGGEDOUT";
		case AS_NOTREADY:
			return "AS_NOTREADY";
		case AS_READY:
			return "AS_READY";
		case AS_RINGING:
			return "AS_RINGING";
		default:
			return "AS_UNKNOWN";
		}
	}

	/**
	 * <b>Método não implementado</b>. Alterna entre uma chamada ativa e uma
	 * previamente retida.
	 * 
	 * @param activeCallId
	 *            Chamada ativa.
	 * @param otherCallId
	 *            Outra chamada.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @return true em caso de sucesso, false em caso erro na operação.
	 * @see #holdCall(int, String)
	 */
	public boolean alternateCall(int activeCallId, int otherCallId,
			String station) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %d %s",
						"alternateCall", activeCallId, otherCallId, station));
		// assertCallIdParameter(activeCallId, "activeCallId");
		// assertCallIdParameter(otherCallId, "otherCallId");
		// assertStringParameter(station, "station");
		assertConnection();
		if (!AzSocket.getInstance().alternateCall(activeCallId, otherCallId,
				station)) {
			return false;
		}
		setAgentState(AS_BUSY);
		return true;
	}

	// private void assertCallIdParameter(int callId, String parameterName) {
	// if (callId < 0) {
	// RuntimeException e = new InvalidParameterException("invalid "
	// + parameterName);
	// Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
	// e.getMessage(), e);
	// throw e;
	// }
	// }

	/**
	 * <b>Método não implementado</b>. Atende uma determinada chamada ringando
	 * em um determinado ramal.
	 * 
	 * @param station
	 *            Ramal onde ocorrerá o atendimento.
	 * @param callId
	 *            Chamada a ser atendida.
	 * @return true para sucesso, false para erro na operação.
	 * 
	 * @see #EV_CALLDELIVERED
	 */
	public boolean answerCall(String station, int callId) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %d", "answerCall",
						station, callId));
		// assertStringParameter(station, "station");
		// assertCallIdParameter(callId, "callId");
		assertConnection();
		if (!AzSocket.getInstance().answerCall(station, callId)) {
			return false;
		}
		setAgentState(AS_BUSY);
		return true;
	}

	private void assertConnection() {
		if (AzSocket.getInstance() == null
				|| !AzSocket.getInstance().isConnected()) {
			RuntimeException e = new IllegalStateException("must be connected");
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
			throw e;
		}
	}

	// private void assertStringParameter(String string, String parameterName) {
	// if (string.length() == 0) {
	// RuntimeException e = new InvalidParameterException("invalid "
	// + parameterName);
	// Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
	// e.getMessage(), e);
	// throw e;
	// }
	// }

	private void assertDisconnection() {
		if (connectionMonitor != null
				|| (AzSocket.getInstance() != null && AzSocket.getInstance()
						.isConnected())) {
			RuntimeException e = new IllegalStateException(
					"must be disconnected");
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Desliga uma determinada chamada em um determinado ramal.
	 * 
	 * @param station
	 *            Ramal onde ocorrerá o desligamento.
	 * @param callId
	 *            Chamada a ser desligada.
	 * @return true para sucesso, false para erro na operação.
	 * 
	 * @see #answerCall(String, int)
	 * @see #EV_CONNECTIONCLEARED
	 */
	public boolean clearCall(String station, int callId) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %d", "clearCall",
						station, callId));
		// assertStringParameter(station, "station");
		// assertCallIdParameter(callId, "callId");
		assertConnection();
		if (!AzSocket.getInstance().clearCall(station, callId)) {
			return false;
		}
		setAgentState(AS_ACW);
		return true;
	}

	/**
	 * Desconecta o componente do servidor Gennex.
	 * 
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #isConnected()
	 * @see #openStream(String, int)
	 */
	public boolean closeStream() {
		Logger.getLogger(getClass().getSimpleName())
				.info("Entrada closeStream");
		assertConnection();
		connectionMonitor.stop();
		connectionMonitor = null;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Logger.getLogger(getClass().getSimpleName()).log(Level.SEVERE,
					e.getMessage(), e);
		}
		AzSocket.getInstance().disconnect();
		return true;
	}

	/**
	 * <b>Método não implementado</b>. Reune uma chamada no ramal, todas as
	 * partes em conferência.
	 * 
	 * @param heldCallId
	 *            Chamada retida.
	 * @param activeCallId
	 *            Chamada ativa.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #consultationCall(int, String, String, String)
	 * @see #holdCall(int, String)
	 * @see #retrieveCall(int, String)
	 * @see #transferCall(int, int, String)
	 * 
	 */
	public boolean conferenceCall(int heldCallId, int activeCallId,
			String station) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %d %s",
						"conferenceCall", heldCallId, activeCallId, station));
		// assertCallIdParameter(heldCallId, "heldCallId");
		// assertCallIdParameter(activeCallId, "activeCallId");
		// assertStringParameter(station, "station");
		assertConnection();
		if (!AzSocket.getInstance().conferenceCall(heldCallId, activeCallId,
				station)) {
			return false;
		}
		setAgentState(AS_BUSY);
		return true;
	}

	/**
	 * <b>Método não implementado</b>. Retem a chamada atual e realiza uma nova
	 * para outro destino.
	 * 
	 * @param activeCallId
	 *            Chamada atualmente ativa.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @param destination
	 *            Destino da nova chamada.
	 * @param userInfo
	 *            Informação que trafegará pela chamada (Bilhete).
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #holdCall(int, String)
	 * @see #transferCall(int, int, String)
	 * @see #conferenceCall(int, int, String)
	 * @see #alternateCall(int, int, String)
	 * @see #retrieveCall(int, String)
	 */
	public boolean consultationCall(int activeCallId, String station,
			String destination, String userInfo) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %s %s %s",
						"consultationCall", activeCallId, station, destination,
						userInfo));
		// assertCallIdParameter(activeCallId, "activeCallId");
		// assertStringParameter(station, "station");
		// assertStringParameter(destination, "destination");
		assertConnection();
		if (!AzSocket.getInstance().consultationCall(activeCallId, station,
				destination, userInfo)) {
			return false;
		}
		setAgentState(AS_HOLD);
		return true;
	}

	/**
	 * Devolve o estado atual do agente.
	 * 
	 * @return o estado atual do agente.
	 * 
	 * @see #AS_LOGGEDOUT
	 * @see #AS_READY
	 * @see #AS_NOTREADY
	 * @see #AS_ACW
	 * @see #AS_RINGING
	 * @see #AS_BUSY
	 * @see #AS_HOLD
	 */
	public int getAgentState() {
		return this.agentState;
	}

	/**
	 * Objeto utilizado para registro dos eventos de chamada.
	 * 
	 * @return Objeto para obtenção dos eventos de chamada.
	 * 
	 * @see java.util.Observable#addObserver(Observer)
	 * @see java.util.Observer#update(Observable, Object)
	 */
	public EventObserver getEventObserver() {
		return this.eventObserver;
	}

	/**
	 * Exibe o ANI do último evento recebido.
	 * 
	 * @return o ANI recebido pelo último evento.
	 * 
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #getLastEvent()
	 */
	public String getLastANI() {
		return AzSocket.getInstance().getLastANI();
	}

	/**
	 * Exibe o CALLID do último evento recebido.
	 * 
	 * @return o CALLID recebido pelo último evento.
	 * 
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #getLastEvent()
	 */
	public int getLastCallId() {
		return AzSocket.getInstance().getLastCallId();
	}

	/**
	 * Exibe o CAUSE do último evento recebido.
	 * 
	 * @return o CAUSE recebido pelo último evento.
	 * 
	 * @see #EV_CONNECTIONCLEARED
	 * @see #getLastEvent()
	 */
	public int getLastCause() {
		return AzSocket.getInstance().getLastCause();
	}

	/**
	 * Exibe o DNIS do último evento recebido.
	 * 
	 * @return o DNIS recebido pelo último evento.
	 * 
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #getLastEvent()
	 */
	public String getLastDNIS() {
		return AzSocket.getInstance().getLastDNIS();
	}

	/**
	 * Método usado para recebimento de eventos de telefonia através de polling.
	 * Sua chamada deve ser realizada, no mínimo, a cada 1 segundo para garantir
	 * vazão dos eventos. Caso o resultado seja {@link EV_NONE}, nenhum evento
	 * está na fila. Caso contrário, o respectivo evento é retornado e removido
	 * automaticamente da fila. Após o recebimento de um evento deve ser chamada
	 * a respectiva função para obter detalhes sobre ele, como ANI, DNIS, etc.
	 * 
	 * @return o último evento recebido no ramal.
	 * 
	 * @see #EV_UNKNOWN
	 * @see #EV_NONE
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #EV_CONNECTIONCLEARED
	 * @see #getLastANI()
	 * @see #getLastDNIS()
	 * @see #getLastUserInfo()
	 * @see #getLastCallId()
	 * @see #getLastCause()
	 */
	public int getLastEvent() {
		int evento = AzSocket.getInstance().getLastEvent();
		switch (evento) {
		case EV_CALLDELIVERED:
			Logger
					.getLogger(getClass().getSimpleName())
					.info(
							"Enviando mensagem PopUp. A abertura da proposta deve ser aberta agora! Proposta: "
									+ getLastUserInfo());
			setAgentState(AS_RINGING);
			break;
		case EV_CALLESTABLISHED:
			setAgentState(AS_BUSY);
			break;
		case EV_CONNECTIONCLEARED:
			setAgentState(AS_ACW);
			break;
		case EV_CALLCLEARED:
			setAgentState(AS_ACW);
			break;
		case EV_MAKECALLCONF:
			setAgentState(AS_BUSY);
			break;
		}
		return evento;
	}

	/**
	 * Exibe o USERDETAILS do último evento recebido.
	 * 
	 * @return o USERDETAILS recebido pelo último evento.
	 * 
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #getLastEvent()
	 */
	public String getLastUserDetails() {
		return AzSocket.getInstance().getLastUserDetails();
	}

	/**
	 * Exibe o USERINFO do último evento recebido.
	 * 
	 * @return o USERINFO recebido pelo último evento.
	 * 
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #getLastEvent()
	 */
	public String getLastUserInfo() {
		return AzSocket.getInstance().getLastUserInfo();
	}

	private int getPort() {
		return port;
	}

	private String getServer() {
		return server;
	}

	/**
	 * <b>Método não implementado</b>. Retem a chamada ativa, colocando-a em
	 * espera.
	 * 
	 * @param activeCallId
	 *            Chamada ativa.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #alternateCall(int, int, String)
	 * @see #conferenceCall(int, int, String)
	 * @see #retrieveCall(int, String)
	 * @see #transferCall(int, int, String)
	 */
	public boolean holdCall(int activeCallId, String station) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %s", "holdCall",
						activeCallId, station));
		// assertCallIdParameter(activeCallId, "activeCallId");
		// assertStringParameter(station, "station");
		assertConnection();
		if (!AzSocket.getInstance().holdCall(activeCallId, station)) {
			return false;
		}
		setAgentState(AS_HOLD);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.applet.Applet#init()
	 */
	@Override
	public void init() {
		super.init();
		Logger.getLogger(getClass().getSimpleName()).info(
				"Loading " + getClass().getSimpleName() + " " + VERSION);
		setLayout(null);
		setSize(90, 30);
		lblServidor = new JLabel("JAzConector " + VERSION);
		lblServidor.setBounds(new Rectangle(1, 1, 150, 30));
		lblServidor.setEnabled(false);
		add(lblServidor, null);

		new Timer(getClass().getSimpleName()).schedule(new TimerTask() {
			@Override
			public void run() {
				if (getAgentState() == AS_ACW) {
					Calendar referencia = Calendar.getInstance();
					referencia.add(Calendar.MINUTE, -3);
					if (referencia.after(ultimaMudancaEstado)) {
						Logger.getLogger(getClass().getSimpleName()).warning(
								"Tornando agente livre por timeout em ACW!");
						setAgentState(AS_READY);
					}
				}
			}
		}, 0, 1000);

		// XXX

		// openStream("10.1.1.182", 22000);
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// makeCall("1234", "(11)9 390-7180", "Teste");
		// for (;;) {
		// if (getLastEvent() == EV_MAKECALLCONF) {
		// Logger.getLogger(getClass().getName()).info(
		// String.format("CallId: %d", getLastCallId()));
		// break;
		// }
		// }
	}

	/**
	 * Consulta o estado da conexão com o servidor Gennex.
	 * 
	 * @return true caso conectado, false se desconectado.
	 * 
	 * @see #openStream(String, int)
	 * @see #closeStream()
	 */
	public boolean isConnected() {
		return AzSocket.getInstance() != null
				&& AzSocket.getInstance().isConnected();
	}

	/**
	 * Loga um operador em um ramal e um determinado grupo mediante uma senha,
	 * tornando-o disponível para receber chamadas.
	 * 
	 * @param station
	 *            Ramal onde o agente será logado.
	 * @param agentId
	 *            Agente a ser logado.
	 * @param agentGroup
	 *            Grupo onde o agente será logado.
	 * @param agentPassword
	 *            Senha para login do agente.
	 * @return Retorna true para sucesso, false para erro na operação.
	 * 
	 * @see #logOut(String, String, String)
	 */
	public boolean logIn(String station, String agentId, String agentGroup,
			String agentPassword) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s %s", "logIn",
						station, agentId, agentGroup, agentPassword));
		// assertStringParameter(station, "station");
		// assertStringParameter(agentId, "agentId");
		// assertStringParameter(agentGroup, "agentGroup");
		assertConnection();
		if (!AzSocket.getInstance().logIn(station, agentId, agentGroup,
				agentPassword)) {
			return false;
		}
		setAgentState(AS_READY);
		return true;
	}

	/**
	 * Remove o operador do grupo de atendimento.
	 * 
	 * @param station
	 *            Ramal onde o agente está atualmente logado.
	 * @param agentId
	 *            Agente que será deslogado.
	 * @param agentGroup
	 *            Grupo onde o agente está atualmente logado.
	 * @return Retorna true para sucesso, false para erro na operação.
	 * 
	 * @see #logIn(String, String, String, String)
	 */
	public boolean logOut(String station, String agentId, String agentGroup) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s", "logOut",
						station, agentId, agentGroup));
		// assertStringParameter(station, "station");
		// assertStringParameter(agentId, "agentId");
		// assertStringParameter(agentGroup, "agentGroup");
		assertConnection();
		if (!AzSocket.getInstance().logOut(station, agentId, agentGroup)) {
			return false;
		}
		setAgentState(AS_LOGGEDOUT);
		return true;
	}

	/**
	 * Realiza uma ligação e encaminha-a para o ramal do operador caso haja
	 * atendimento. O transcorrer da ligação é obtido através da monitoração dos
	 * eventos associados.
	 * 
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @param destination
	 *            Destino da nova chamada.
	 * @param userInfo
	 *            Informação que trafegará pela chamada (Bilhete).
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #clearCall(String, int)
	 * @see #getLastEvent()
	 * @see #EV_MAKECALLCONF
	 * @see #EV_CALLDELIVERED
	 * @see #EV_CALLESTABLISHED
	 * @see #EV_CALLCLEARED
	 * @see #EV_CONNECTIONCLEARED
	 */
	public boolean makeCall(String station, String destination, String userInfo) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s", "makeCall",
						station, destination, userInfo));
		// assertStringParameter(station, "station");
		// assertStringParameter(destination, "destination");
		assertConnection();
		if (!AzSocket.getInstance().makeCall(station, destination, userInfo)) {
			return false;
		}
		setAgentState(AS_BUSY);
		return true;
	}

	/**
	 * Pausa o operador, informando o motivo, tornando-o temporariamente
	 * indisponível para o recebimento de chamadas.
	 * 
	 * @param station
	 *            Ramal onde o agente está logado.
	 * @param agentId
	 *            Agente que entrará em pausa.
	 * @param agentGroup
	 *            Grupo onde o agente está logado.
	 * @param reasonCode
	 *            Motivo numérico da pausa.
	 * @return Retorna true para sucesso, false para erro na operação.
	 * 
	 * @see #ready(String, String, String)
	 */
	public boolean notReady(String station, String agentId, String agentGroup,
			int reasonCode) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s %d", "notReady",
						station, agentId, agentGroup, reasonCode));
		// assertStringParameter(station, "station");
		// assertStringParameter(agentId, "agentId");
		// assertStringParameter(agentGroup, "agentGroup");
		// assertCallIdParameter(reasonCode, "reasonCode");
		assertConnection();
		if (!AzSocket.getInstance().notReady(station, agentId, agentGroup,
				reasonCode)) {
			return false;
		}
		setAgentState(AS_NOTREADY);
		return true;
	}

	/**
	 * Conecta o componente ao servidor Gennex.
	 * 
	 * @param server
	 *            Servidor para conexão.
	 * @param port
	 *            Porta para conexão.
	 * @return return true em caso de sucesso, false em caso de erro na
	 *         operação.
	 * 
	 * @see #closeStream()
	 * @see #isConnected()
	 */
	public boolean openStream(String server, int port) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %d", "openStream",
						server, port));
		// assertStringParameter(server, "server");
		// assertCallIdParameter(port, "port");
		assertDisconnection();
		setServer(server);
		setPort(port);
		connectionMonitor = new ConnectionMonitor();
		connectionMonitor.setAzConector(this);
		if (!connectionMonitor.checkConnection())
			return false;
		new Thread(connectionMonitor).start();
		return true;
	}

	/**
	 * Despausa um operador, tornando-o livre para o atendimento.
	 * 
	 * @param station
	 *            Ramal onde o agente está logado.
	 * @param agentId
	 *            Agente atualmente em pausa.
	 * @param agentGroup
	 *            Grupo onde o agente está logado.
	 * @return Retorna true para sucesso, false para erro na operação.
	 * 
	 * @see #notReady(String, String, String, int)
	 */
	public boolean ready(String station, String agentId, String agentGroup) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s", "ready",
						station, agentId, agentGroup));
		// assertStringParameter(station, "station");
		// assertStringParameter(agentId, "agentId");
		// assertStringParameter(agentGroup, "agentGroup");
		assertConnection();
		if (!AzSocket.getInstance().ready(station, agentId, agentGroup))
			return false;

		setAgentState(AS_READY);
		return true;
	}

	/**
	 * Coloca um operador em pausa produtiva, tornando-o indisponível para o
	 * atendimento.
	 * 
	 * @param station
	 *            Ramal onde o agente está logado.
	 * @param agentId
	 *            Agente atualmente disponível.
	 * @param agentGroup
	 *            Grupo onde o agente está logado.
	 * @return Retorna true para sucesso, false para erro na operação.
	 * 
	 * @see #ready(String, String, String)
	 */
	public boolean acw(String station, String agentId, String agentGroup) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %s %s %s", "acw", station,
						agentId, agentGroup));
		// assertStringParameter(station, "station");
		// assertStringParameter(agentId, "agentId");
		// assertStringParameter(agentGroup, "agentGroup");
		assertConnection();
		if (!AzSocket.getInstance().acw(station, agentId, agentGroup))
			return false;

		setAgentState(AS_ACW);
		return true;
	}

	/**
	 * <b>Método não implementado</b>. Torna ativa uma determinada ligação
	 * previamente retida.
	 * 
	 * @param heldCallId
	 *            Chamada retida.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #holdCall(int, String)
	 */
	public boolean retrieveCall(int heldCallId, String station) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %s", "retrieveCall",
						heldCallId, station));
		// assertCallIdParameter(heldCallId, "heldCallId");
		// assertStringParameter(station, "station");
		assertConnection();
		if (!AzSocket.getInstance().retrieveCall(heldCallId, station)) {

			return false;
		}
		setAgentState(AS_BUSY);
		return true;
	}

	private void setAgentState(int agentState) {
		if (agentState == this.agentState) {
			return;
		}

		if (agentState != AS_READY)
			switch (this.getAgentState()) {
			case AS_LOGGEDOUT:
				switch (agentState) {
				case AS_READY:
					break;
				default:
					return;
				}
				break;
			case AS_READY:
				switch (agentState) {
				case AS_NOTREADY:
					break;
				case AS_RINGING:
					break;
				case AS_BUSY:
					break;
				case AS_LOGGEDOUT:
					break;
				default:
					return;
				}
				break;
			case AS_NOTREADY:
				switch (agentState) {
				case AS_LOGGEDOUT:
					break;
				case AS_READY:
					break;
				default:
					return;
				}
				break;
			case AS_ACW:
				switch (agentState) {
				case AS_LOGGEDOUT:
					break;
				case AS_READY:
					break;
				case AS_NOTREADY:
					break;
				default:
					return;
				}
				break;
			case AS_RINGING:
				switch (agentState) {
				case AS_LOGGEDOUT:
					break;
				case AS_NOTREADY:
					break;
				case AS_ACW:
					break;
				case AS_BUSY:
					break;
				case AS_HOLD:
					break;
				default:
					return;
				}
				break;
			case AS_BUSY:
				switch (agentState) {
				case AS_LOGGEDOUT:
					break;
				case AS_NOTREADY:
					break;
				case AS_ACW:
					break;
				case AS_HOLD:
					break;
				default:
					return;
				}
				break;
			case AS_HOLD:
				switch (agentState) {
				case AS_LOGGEDOUT:
					break;
				case AS_ACW:
					break;
				case AS_RINGING:
					break;
				case AS_BUSY:
					break;
				default:
					return;
				}
				break;
			}

		Logger.getLogger(getClass().getSimpleName()).info(
				"Novo agentState: " + agentStateToStr(agentState));
		this.ultimaMudancaEstado = Calendar.getInstance();
		this.agentState = agentState;
	}

	void setPort(int port) {
		this.port = port;
	}

	void setServer(String server) {
		this.server = server;
	}

	/**
	 * <b>Método não implementado</b>. Transfere uma ligação ativa para uma
	 * ligação previamente retida, desconectando o operador da chamada.
	 * 
	 * @param heldCallId
	 *            Chamada retida.
	 * @param activeCallId
	 *            Chamada ativa.
	 * @param station
	 *            Ramal onde ocorrerá a operação.
	 * @return true em caso de sucesso, false em caso de erro na operação.
	 * 
	 * @see #makeCall(String, String, String)
	 * @see #consultationCall(int, String, String, String)
	 */
	public boolean transferCall(int heldCallId, int activeCallId, String station) {
		Logger.getLogger(getClass().getSimpleName()).info(
				String.format("Entrada %s com params %d %d %s", "transferCall",
						heldCallId, activeCallId, station));
		// assertCallIdParameter(heldCallId, "heldCallId");
		// assertCallIdParameter(activeCallId, "activeCallId");
		// assertStringParameter(station, "station");
		assertConnection();
		if (!AzSocket.getInstance().transferCall(heldCallId, activeCallId,
				station)) {
			return false;
		}
		setAgentState(AS_ACW);
		return true;
	}
}
