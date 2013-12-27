/**
 * Diese Klasse ist zum Testen des Programmes. Diese Klasse darf nicht
 * veraendert werden.
 */
public class Main {

	/**
	 * Die Main-Methode erstellt eine neue Routing-Tabelle und fuehrt
	 * anschliessend einige testaufrufe aus.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Eigene IP-Adressen
		Router myRouter = new Router(new int[] { IP(212, 84, 12, 1),
				IP(13, 37, 23, 34), IP(211, 2, 0, 2), IP(192, 168, 10, 1) });

		// 212.84.12.0 ; 0.0.0.0 ; 255.255.255.0 ; U ; eth0
		myRouter.routeAdd(IP(212, 84, 12, 0), (byte) 24 // 255.255.255.0
				, IP(0, 0, 0, 0), new Flags[] { Flags.U }, HWPort.eth0);

		// 212.84.12.128 ; 212.84.12.2 ; 255.255.255.128 ; UG ; eth0
		myRouter.routeAdd(IP(212, 84, 12, 128),
				(byte) 25 // 255.255.255.128
				, IP(212, 84, 12, 2), new Flags[] { Flags.U, Flags.G },
				HWPort.eth0);

		// Find Route for 13.37.23.42
		System.out.println("Searching for route: 13.37.23.42\nInterface: "
				+ myRouter.findRoute(IP(13, 37, 23, 42)));

		// 13.37.23.41 ; 0.0.0.0 ; 255.255.255.255 ; UH ; eth1
		myRouter.routeAdd(IP(13, 37, 23, 41), (byte) 32 // 255.255.255.255
				, IP(0, 0, 0, 0), new Flags[] { Flags.U, Flags.H }, HWPort.eth1);

		// loesche 13.37.23.41
		myRouter.routeDelete(IP(13, 37, 23, 41));

		// 13.37.23.42 ; 0.0.0.0 ; 255.255.255.255 ; UH ; eth1
		myRouter.routeAdd(IP(13, 37, 23, 42), (byte) 32 // 255.255.255.255
				, IP(0, 0, 0, 0), new Flags[] { Flags.U, Flags.H }, HWPort.eth1);

		// 221.2.0.0 ; 221.2.0.1 ; 225.255.0.0 ; UG ; eth2
		myRouter.routeAdd(IP(221, 2, 0, 0),
				(byte) 16 // 255.255.0.0
				, IP(221, 2, 0, 1), new Flags[] { Flags.U, Flags.G },
				HWPort.eth2);

		// 0.0.0.0 ; 212.69.12.12 ; 0.0.0.0 ; UG ; eth0
		myRouter.routeAdd(IP(0, 0, 0, 0), (byte) 0, IP(212, 69, 12, 12),
				new Flags[] { Flags.U, Flags.G }, HWPort.eth0);

		// 212.84.12.128 ; 212.84.12.3 ; 255.255.255.128 ; UG ; eth3
		myRouter.routeModify(IP(212, 84, 12, 128),
				(byte) 25 // 255.255.255.128
				, IP(212, 84, 12, 3), new Flags[] { Flags.U, Flags.G },
				HWPort.eth3);

		// Find Route for 212.84.12.144
		System.out.println("Searching for route: 212.84.12.144\nInterface: "
				+ myRouter.findRoute(IP(212, 84, 12, 144)));

		// Find Route for 212.84.12.44
		System.out.println("Searching for route: 212.84.12.44\nInterface: "
				+ myRouter.findRoute(IP(212, 84, 12, 44)));

		// Set local IP to 212.84.12.7
		myRouter.setLocalIP(IP(212, 84, 12, 7), HWPort.eth0);

		// 112.84.12.0 ; 0.0.0.0 ; 255.255.255.15 ; U ; eth0
		myRouter.routeAdd(IP(112, 84, 12, 0), (byte) 28 // 255.255.255.240
				, IP(0, 0, 0, 0), new Flags[] { Flags.U }, HWPort.eth0);

		// Find Route for 212.84.12.7
		System.out.println("Searching for route: 212.84.12.7\nInterface: "
				+ myRouter.findRoute(IP(212, 84, 12, 7)));

		// Print Table
		myRouter.printTable();
		myRouter.routeModify(IP(212, 84, 12, 128), (byte) 25 // 255.255.255.128
				, IP(212, 84, 12, 2), new Flags[] { Flags.G }, HWPort.eth0);

		// Find Route for 212.84.12.144
		System.out.println("Searching for route: 212.84.12.144\nInterface: "
				+ myRouter.findRoute(IP(212, 84, 12, 144)));

		// Find Route for 13.37.23.42
		System.out.println("Searching for route: 13.37.23.42\nInterface: "
				+ myRouter.findRoute(IP(13, 37, 23, 42)));

		// Print Table
		myRouter.printTable();
	}

	/**
	 * Hilfsmethode um IP-Adressen in Integers umzuwandeln. Nicht benutzen.
	 */
	private static int IP(int a, int b, int c, int d) {
		return ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((c & 0xFF) << 8)
				| (d & 0xFF);
	}
}
