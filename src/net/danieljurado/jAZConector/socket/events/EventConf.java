package net.danieljurado.jAZConector.socket.events;

class EventConf extends Event {

	private int invokeId = -1;

	EventConf(int callId) {
		super(callId);
		// TODO Auto-generated constructor stub
	}

	EventConf(int callId, int invokeId) {
		super(callId);
		this.invokeId = invokeId;
	}

	/**
	 * @return the invokeId
	 */
	int getInvokeId() {
		return invokeId;
	}

}
