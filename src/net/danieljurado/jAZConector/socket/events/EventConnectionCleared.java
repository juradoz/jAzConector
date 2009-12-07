package net.danieljurado.jAZConector.socket.events;

public class EventConnectionCleared extends Event {
	private int cause = -1;

	public EventConnectionCleared(int callId, int cause) {
		super(callId);
		this.cause = cause;
	}

	/**
	 * @return the cause
	 */
	public int getCause() {
		return cause;
	}

}
