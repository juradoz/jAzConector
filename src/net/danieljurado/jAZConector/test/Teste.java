package net.danieljurado.jAZConector.test;

import net.danieljurado.jAZConector.JAzConector;

public class Teste {
	public static void main(String[] args) {
		JAzConector jAzonector = new JAzConector();
		jAzonector.init();
		jAzonector.openStream("10.10.10.9", 22000);
		// espera(5);

		loga(jAzonector);
		esperaEventos(jAzonector);
		desloga(jAzonector);

		loga(jAzonector);
		pausaDespausa(jAzonector);
		desloga(jAzonector);

		ligaEDesliga(jAzonector);

		logaEDesloga(jAzonector);
	}

	private static void esperaEventos(JAzConector jAzonector) {
		for (int i = 0; i < 30; i++) {
			int evento = jAzonector.getLastEvent();

			switch (evento) {
			case JAzConector.EV_NONE:
				break;
			case JAzConector.EV_CALLCLEARED:
				System.out.println("EV_CALLCLEARED callId "
						+ jAzonector.getLastCallId() + " cause "
						+ jAzonector.getLastCause());
				break;

			case JAzConector.EV_CALLDELIVERED:
				System.out.println("EV_CALLDELIVERED callId "
						+ jAzonector.getLastCallId() + " ani "
						+ jAzonector.getLastANI() + " dnis "
						+ jAzonector.getLastDNIS() + " userInfo "
						+ jAzonector.getLastUserInfo());
				break;
			case JAzConector.EV_CALLESTABLISHED:
				System.out.println("EV_CALLESTABLISHED callId "
						+ jAzonector.getLastCallId() + " ani "
						+ jAzonector.getLastANI() + " dnis "
						+ jAzonector.getLastDNIS() + " userInfo "
						+ jAzonector.getLastUserInfo());
				break;
			case JAzConector.EV_CONNECTIONCLEARED:
				System.out.println("EV_CONNECTIONCLEARED callId "
						+ jAzonector.getLastCallId() + " cause "
						+ jAzonector.getLastCause());
				break;
			case JAzConector.EV_MAKECALLCONF:
				System.out.println("EV_MAKECALLCONF callId "
						+ jAzonector.getLastCallId());
				break;
			}
			espera(1);
		}
	}

	private static void logaEDesloga(JAzConector jAzConector) {
		loga(jAzConector);
		desloga(jAzConector);
	}

	private static void desloga(JAzConector jAzConector) {
		System.out.println("Logout: "
				+ jAzConector.logOut("8003", "1000", "4040"));
		jAzConector.getLastEvent();
	}

	private static void pausaDespausa(JAzConector jAzConector) {
		System.out.println("NotReady: "
				+ jAzConector.notReady("8003", "1000", "4040", 0));
		jAzConector.getLastEvent();

		System.out.println("Ready: "
				+ jAzConector.ready("8003", "1000", "4040"));
		jAzConector.getLastEvent();
	}

	private static void loga(JAzConector jAzConector) {
		boolean result = jAzConector.logIn("8003", "1000", "4040", "1000");
		if (!result) {
			desloga(jAzConector);
			result = jAzConector.logIn("8003", "1000", "4040", "1000");
		}
		System.out.println(result);
		jAzConector.getLastEvent();
	}

	private static void ligaEDesliga(JAzConector jAzConector) {
		System.out.println("MakeCall: "
				+ jAzConector.makeCall("8003", "8004", "Teste"));
		espera(5);

		jAzConector.getLastEvent();

		System.out.println("ClearCall: "
				+ jAzConector.clearCall("8003", jAzConector.getLastCallId()));
		espera(5);
		jAzConector.getLastEvent();
	}

	private static void espera(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
