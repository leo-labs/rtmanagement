
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * Dies ist die zu implementierende Klasse.
 */

public class Router implements IRouter {

	public Map<HWPort, Integer> interfaces;
	public TreeMap<NetworkId, RoutingTableEntry> routingTable;

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
		this.routingTable = new TreeMap<NetworkId, RoutingTableEntry>();

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
		return true;
	}

	@Override
	public boolean routeDelete(int destinationNetwork) {
		if (routingTable.remove(new NetworkId(destinationNetwork, (byte) 0)) != null)
			return true;

		System.err
				.println("Failed to delete route! Route with the destination "
						+ intIPtoString(destinationNetwork) + " does not exist");
		return false;
	}

	@Override
	public boolean routeModify(int destinationNetwork, byte prefix,
			int gateway, Flags[] flags, HWPort port) {
		if (prefix < 0 || prefix > 32) {
			System.err.println(prefix + " is not a valid prefix ");
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
		return true;
	}

	@Override
	public HWPort findRoute(int destination) {
		// complete matches
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();

			if (id.destinationNetwork == destination
					&& Arrays.asList(e).contains(Flags.U)) {
				return e.port;
			}
		}

		// matching network ID, sorted by longer prefix, no match until default
		// route picks default route
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();
			
			// CIDR to netmask
			int netmask;
			if ((int) id.prefix != 0)
				netmask = (0xFFFFFFFF << (32 - (int) id.prefix));
			else
				netmask = 0x00000000;

			if ((destination & netmask) == id.destinationNetwork
					&& Arrays.asList(e).contains(Flags.U)) {
				return e.port;
			}
		}
		System.err.println("Failed to find Route ! "
				+ intIPtoString(destination) + " does not exist !");
		return HWPort.no_route_to_host;
	}

	@Override
	public void printTable() {
		System.out.format("%15s%8s%15s%8s%12s%n", "NetworkDest.", "Prefix",
				"Gateway", "Flags", "interface");
		for (Entry<NetworkId, RoutingTableEntry> entry : routingTable.entrySet()) {
			NetworkId id = entry.getKey();
			RoutingTableEntry e = entry.getValue();
			System.out.format("%15s%8s%15s%8s%12s%n",
					intIPtoString(id.destinationNetwork), id.prefix,
					intIPtoString(e.gateway), e.getFlagsAsString(), e.port);
		}

	}

	private String intIPtoString(int ip) {
		// Wy no IP class? Would be easier, however...
		String readableIP = "";
		readableIP += ((ip >> 24) & 0xFF);
		readableIP += "." + (((ip << 8) >> 24) & 0xFF);
		readableIP += "." + (((ip << 16) >> 24) & 0xFF);
		readableIP += "." + (((ip << 24) >> 24) & 0xFF);

		return readableIP;
	}
	
	private String intBroadcasttoString(int ip) {
		String readableIP = "";
		readableIP += ((ip >> 24) & 0xFF);
		readableIP += "." + (((ip << 8) >> 24) & 0xFF);
		readableIP += "." + (((ip << 16) >> 24) & 0xFF);
		readableIP += "." + (((ip << 24) >> 24) | 0xFF);

		return readableIP;
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
		public int destinationNetwork;
		public byte prefix;

		public NetworkId(int networkDestination, byte prefix) {
			this.destinationNetwork = networkDestination;
			this.prefix = prefix;
		}

		@Override
		public int compareTo(NetworkId n) {
			if (n.prefix > this.prefix)
				return 1;
			if (n.prefix < this.prefix)
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
		public String toString() {
			return intIPtoString(destinationNetwork) + "\t" + prefix + "\t";
		}
	}
}
