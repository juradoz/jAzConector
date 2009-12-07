package net.danieljurado.jAZConector.socket.events;

public class EventCallDelivered extends Event {

	private String ani = null;
	private String userInfo = null;
	private String userDetails = null;
	private String dnis = null;

	public EventCallDelivered(int callId, String ani, String userInfo,
			String userDetails, String dnis) {
		super(callId);
		this.ani = ani;
		this.userInfo = userInfo;
		this.userDetails = userDetails;
		this.dnis = dnis;
	}

	/**
	 * @return the ani
	 */
	public String getAni() {
		return ani;
	}

	/**
	 * @return the dnis
	 */
	public String getDnis() {
		return dnis;
	}

	/**
	 * @return the userDetails
	 */
	public String getUserDetails() {
		return userDetails;
	}

	/**
	 * @return the userInfo
	 */
	public String getUserInfo() {
		return userInfo;
	}

}
