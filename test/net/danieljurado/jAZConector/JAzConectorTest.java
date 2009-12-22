package net.danieljurado.jAZConector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JAzConectorTest {

	private static JAzConector jAzConector;

	@Before
	public void setUp() {
		jAzConector.logOut(station, agentId, agentGroup);
		limpaEventos();
	}

	private void limpaEventos() {
		while (jAzConector.getLastEvent() != JAzConector.EV_NONE) {
		}
	}

	@After
	public void tearDown() {
		jAzConector.logOut(station, agentId, agentGroup);
		limpaEventos();
	}

	@BeforeClass
	public static void classSetUp() {
		jAzConector = new JAzConector();
		jAzConector.init();
		conecta();
	}

	private static void conecta() {
		assertTrue(jAzConector.openStream("localhost", 24000));
		while (!jAzConector.isConnected())
			espera(500);
	}

	private static void espera(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void classTearDown() {
		assertTrue(jAzConector.closeStream());
	}

	private static final String destination = "91";
	private static final String destination2 = "92";
	private static final String userInfo = "teste";

	public void testAlternateCall() {
		loga();
		assertTrue(jAzConector.makeCall(station, destination, userInfo));
		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertTrue(jAzConector.consultationCall(callId, station, destination2,
				userInfo));

		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId2 = jAzConector.getLastCallId();
		assertTrue(callId2 > 0);

		espera(5000);

		assertTrue(jAzConector.alternateCall(callId2, callId, station));

		espera(5000);

		assertTrue(jAzConector.alternateCall(callId, callId2, station));

		assertTrue(jAzConector.clearCall(station, callId));
		assertTrue(jAzConector.clearCall(station, callId2));
	}

	public void testAnswerCall() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertTrue(jAzConector.answerCall(station, callId));

		espera(5000);

		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testClearCall() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	public void testConferenceCall() {
		loga();
		assertTrue(jAzConector.makeCall(station, destination, userInfo));
		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertTrue(jAzConector.consultationCall(callId, station, destination2,
				userInfo));

		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId2 = jAzConector.getLastCallId();
		assertTrue(callId2 > 0);

		espera(5000);

		assertTrue(jAzConector.conferenceCall(callId, callId2, station));

		espera(5000);

		assertTrue(jAzConector.clearCall(station, callId));
		assertTrue(jAzConector.clearCall(station, callId2));
	}

	public void testConsultationCall() {
		loga();
		assertTrue(jAzConector.makeCall(station, destination, userInfo));
		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertTrue(jAzConector.consultationCall(callId, station, destination2,
				userInfo));

		assertEquals(JAzConector.AS_BUSY, jAzConector.getAgentState());
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId2 = jAzConector.getLastCallId();
		assertTrue(callId2 > 0);

		espera(5000);

		assertTrue(jAzConector.clearCall(station, callId));
		assertTrue(jAzConector.clearCall(station, callId2));
	}

	@Test
	public void testGetAgentState() {
		loga();
		assertTrue(jAzConector.ready(station, agentId, agentGroup));
		assertEquals(JAzConector.AS_READY, jAzConector.getAgentState());
		assertTrue(jAzConector.notReady(station, agentId, agentGroup,
				reasonCode));
		assertEquals(JAzConector.AS_NOTREADY, jAzConector.getAgentState());
		desloga();
	}

	private void loga() {
		assertTrue(jAzConector.logIn(station, agentId, agentGroup,
				agentPassword));
		assertEquals(JAzConector.AS_NOTREADY, jAzConector.getAgentState());
	}

	private void desloga() {
		assertTrue(jAzConector.logOut(station, agentId, agentGroup));
		assertEquals(JAzConector.AS_LOGGEDOUT, jAzConector.getAgentState());
	}

	@Test
	public void testGetEventObserver() {
		assertNotNull(jAzConector.getEventObserver());
	}

	@Test
	public void testGetLastANI() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertEquals(JAzConector.EV_CALLDELIVERED, jAzConector.getLastEvent());

		assertEquals(station2, jAzConector.getLastANI());

		espera(5000);

		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testGetLastCallId() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testGetLastCause() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));

		while (jAzConector.getLastEvent() != JAzConector.EV_CALLCLEARED) {

		}

		assertEquals(JAzConector.CS_UNDEFINED, jAzConector.getLastCause());
	}

	@Test
	public void testGetLastDNIS() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertEquals(JAzConector.EV_CALLDELIVERED, jAzConector.getLastEvent());

		assertEquals(station, jAzConector.getLastDNIS());

		espera(5000);

		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testGetLastEvent() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertEquals(JAzConector.EV_CALLDELIVERED, jAzConector.getLastEvent());
		espera(5000);
		assertEquals(JAzConector.EV_CALLESTABLISHED, jAzConector.getLastEvent());
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testGetLastUserDetails() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertEquals(JAzConector.EV_CALLDELIVERED, jAzConector.getLastEvent());
		espera(5000);
		assertEquals("", jAzConector.getLastUserDetails());
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	@Test
	public void testGetLastUserInfo() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);

		assertEquals(JAzConector.EV_CALLDELIVERED, jAzConector.getLastEvent());
		espera(5000);
		assertEquals(userInfo, jAzConector.getLastUserInfo());
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	public void testHoldCall() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsConnected() {
		assertTrue(jAzConector.isConnected());
	}

	private static final String station = "3331";
	private static final String station2 = "3332";
	private static final String agentId = "3331";
	private static final String agentGroup = "101";
	private static final String agentPassword = "3331";

	@Test
	public void testLogIn() {
		loga();
	}

	@Test
	public void testLogOut() {
		loga();
		desloga();
	}

	@Test
	public void testMakeCall() {
		loga();
		assertTrue(jAzConector.makeCall(station2, station, userInfo));
		assertEquals(JAzConector.EV_MAKECALLCONF, jAzConector.getLastEvent());
		int callId = jAzConector.getLastCallId();
		assertTrue(callId > 0);
		espera(5000);
		assertTrue(jAzConector.clearCall(station, callId));
	}

	private static final int reasonCode = 0;

	@Test
	public void testNotReady() {
		loga();
		jAzConector.ready(station, agentId, agentGroup);
		assertTrue(jAzConector.notReady(station, agentId, agentGroup,
				reasonCode));
	}

	@Test
	public void testReady() {
		loga();
		assertTrue(jAzConector.ready(station, agentId, agentGroup));
	}

	@Test
	public void testAcw() {
		loga();
		jAzConector.ready(station, agentId, agentGroup);
		assertTrue(jAzConector.acw(station, agentId, agentGroup));
	}

	public void testRetrieveCall() {
		fail("Not yet implemented");
	}

	public void testTransferCall() {
		fail("Not yet implemented");
	}

}
