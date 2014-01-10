import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Dies ist die zu implementierende Klasse.
 */

public class Router implements IRouter {

	public Map<HWPort, Integer> interfaces;
	public Map<NetworkId, RoutingTableEntry> routingTable;

	/**
	 * Konstruktor.
	 * 
	 * Die Klasse enthaelt zunaechst nur einen Konstruktor. Dieser bekommt ein
	 * Array mit IP-Adressen uebergeben und legt eine leere Routing-Tabelle an.
	 * Die uebergebenen IPs bindet er dann der Reihenfolge im Array folgend an
	 * die Ports eth0, eth1, eth2 und eth3.
	 * 
	 * @param localIP
	 */

	public Router(int[] localIPs) {
		this.interfaces = new HashMap<HWPort, Integer>();
		this.routingTable = new HashMap<NetworkId, RoutingTableEntry>();

		if (localIPs.length == 4) {
			for (int i = 0; i <= 3; i++) {
				setLocalIP(localIPs[i], HWPort.values()[i]);
			}
		}
	}

	@Override
	public void setLocalIP(int localIP, HWPort port) {
		this.interfaces.put(port, localIP);
	}

	@Override
	public int getLocalIP(HWPort port) {
		return this.interfaces.get(port);
	}

	@Override
	public boolean routeAdd(int destinationNetwork, byte prefix, int gateway,
			Flags[] flags, HWPort port) {
		System.out.println();
		if (prefix < 0 || prefix > 32) {
			System.err.println(prefix + " is not a valid prefix ");
			return false;
		}

		NetworkId id = new NetworkId(destinationNetwork, prefix);

		if (routingTable.containsKey(id)) {
			System.err.println("Failed to add route: Route "
					+ intIPtoString(destinationNetwork) + " already in use!");
			return false;
		}
		RoutingTableEntry e = new RoutingTableEntry(gateway, flags, port);
		routingTable.put(id, e);

		/**
		 * print: Braodcastadress, if the "H-Flag" isn't set Netzadresse
		 */
		System.out.println("Route added successfully!");
		if (!Arrays.asList(e.flags).contains(Flags.H)) {
			System.out.println("Networkadress:   " + id.toString());
			System.out.println("Broadcastadress: " + intBroadcasttoString(id));
		} else
			System.out.println("Hostadress :   " + id.toString());

		return true;
	}

	@Override
	public boolean routeDelete(int destinationNetwork) {
		System.out.println();
		if (routingTable.remove(new NetworkId(destinationNetwork, (byte) 0)) != null) {
			System.out.println("Route  " + intIPtoString(destinationNetwork)
					+ " deleted successfully.");
			return true;
		}

		System.err
				.println("Failed to delete route! Route with the destination "
						+ intIPtoString(destinationNetwork)
						+ " does not exist ");
		return false;
	}

	@Override
	public boolean routeModify(int destinationNetwork, byte prefix,
			int gateway, Flags[] flags, HWPort port) {
		System.out.println();
		if (prefix < 0 || prefix > 32) {
			System.err.println(prefix + " is not a valid prefix.");
			return false;
		}

		NetworkId id = new NetworkId(destinationNetwork, prefix);

		if (!routingTable.containsKey(id)) {
			System.err.println("Failed to modify route: Route "
					+ intIPtoString(destinationNetwork) + " not in use!");
			return false;
		}
		// This is so ugly. Because of the wrong specs, we have to update the
		// key too.
		routingTable.remove(id);
		routingTable.put(id, new RoutingTableEntry(gateway, flags, port));

		System.out.println("Route modified successfully!");
		if(!Arrays.asList(flags).contains(Flags.H))
		{
			System.out.println("Networkadress:   " + id.toString());
			System.out.println("Broadcastadress: " + intBroadcasttoString(id));
		}
		else
			System.out.println("Hostadress:   " + id.toString());
		System.out.println();
		return true;
	}

	@Override
	public HWPort findRoute(int destination) {		
		//localhost
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable
				.entrySet()) {
			RoutingTableEntry e = entry.getValue();

			if ( (interfaces.containsValue(destination) && Arrays.asList(e.flags).contains(Flags.U))
					|| (destination >> 24 == 127)) {
				return HWPort.localhost;
			}
		}

		// complete matches
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable
				.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();

			if (id.destinationNetwork == destination
					&& Arrays.asList(e.flags).contains(Flags.U)) {
				return e.port;
			}
		}

		// sort HashMap via TreeMap and Comparable
		TreeMap<NetworkId, RoutingTableEntry> prefixMap = new TreeMap<NetworkId, RoutingTableEntry>();
		prefixMap.putAll(routingTable);

		// matching network ID, sorted by longer prefix, no match until default
		// route picks default route
		for (Entry<NetworkId, RoutingTableEntry> entry : prefixMap.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();

			// CIDR to netmask
			int netmask = prefixAsNetmask(id);

			if ((destination & netmask) == id.destinationNetwork
					&& Arrays.asList(e.flags).contains(Flags.U)) {
				return e.port;
			}
		}
		System.err.println("Failed to find Route ! "
				+ intIPtoString(destination) + " does not exist !");

		return HWPort.no_route_to_host;
	}

	@Override
	public void printTable() {
		System.out.println();
		System.out.format("%15s%8s%15s%8s%12s%n", "NetworkDest.", "Prefix",
				"Gateway", "Flags", "interface");
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable
				.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();
			System.out.format("%15s%8s%15s%8s%12s%n",
					intIPtoString(id.destinationNetwork), id.prefix,
					intIPtoString(e.gateway), e.getFlagsAsString(), e.port);
		}

	}

	public int prefixAsNetmask(NetworkId id) {
		int netmask;
		if ((int) id.prefix != 0)
			netmask = (0xFFFFFFFF << (32 - (int) id.prefix));
		else
			netmask = 0x00000000;
		return netmask;
	}

	private String intIPtoString(int ip) {
		// Why no IP class? Would be easier, however...
		String readableIP = "";
		readableIP += ((ip >> 24) & 0xFF);
		readableIP += "." + (((ip << 8) >> 24) & 0xFF);
		readableIP += "." + (((ip << 16) >> 24) & 0xFF);
		readableIP += "." + (((ip << 24) >> 24) & 0xFF);

		return readableIP;
	}

	private String intBroadcasttoString(NetworkId id) {
		int netmask = prefixAsNetmask(id);

		// inverse netmask
		netmask = ~netmask;

		int ip = id.destinationNetwork | netmask;

		return intIPtoString(ip);
	}

	public class RoutingTableEntry {
		public int gateway;
		public Flags[] flags;
		public HWPort port;

		public RoutingTableEntry(int gateway, Flags[] flags, HWPort port) {
			this.gateway = gateway;
			this.flags = flags;
			this.port = port;
		}

		@Override
		public String toString() {

			String s = intIPtoString(gateway) + "\t" + getFlagsAsString()
					+ "\t" + intIPtoString(getLocalIP(port));
			return s;
		}

		public String getFlagsAsString() {
			StringBuilder flag = new StringBuilder();

			for (Flags f : flags) {
				switch (f.ordinal()) {
				case 0:
					flag.append("U");
					break;
				case 1:
					if (flag.length() != 0)
						flag.append(",");
					flag.append("G");
					break;
				case 2:
					if (flag.length() != 0)
						flag.append(",");
					flag.append("H");
					break;
				}
			}
			return flag.toString();
		}
	}

	public class NetworkId implements Comparable<NetworkId> {
		private int destinationNetwork;
		private byte prefix;

		public NetworkId(int networkDestination, byte prefix) {
			this.destinationNetwork = networkDestination;
			this.prefix = prefix;
		}

		@Override
		public int compareTo(NetworkId id) {
			if (id.prefix > this.prefix)
				return 1;
			if (id.prefix < this.prefix)
				return -1;
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof NetworkId))
				return false;
			if (((NetworkId) o).destinationNetwork == this.destinationNetwork)
				return true;
			return false;
		}

		@Override
		public int hashCode() {
			return this.destinationNetwork;
		}

		@Override
		public String toString() {
			return intIPtoString(destinationNetwork) + "\t" + prefix + "\t";
		}

	}
}