package net.danieljurado.jAZConector.socket.events;

public class Event {
	private int callId = -1;

	Event(int callId) {
		super();
		this.callId = callId;
	}

	/**
	 * @return the callId
	 */
	public int getCallId() {
		return callId;
	}
}
