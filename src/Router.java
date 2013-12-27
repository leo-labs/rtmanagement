/**
 * Dies ist die zu implementierende Klasse.
 */

public class Router implements IRouter {

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
	// Der Konstruktor muss auch noch implementiert werden.
    }

	@Override
	public void setLocalIP(int localIP, HWPort port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLocalIP(HWPort port) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean routeAdd(int destinationNetwork, byte prefix, int gateway,
			Flags[] flags, HWPort port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean routeDelete(int destinationNetwork) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean routeModify(int destinationNetwork, byte prefix,
			int gateway, Flags[] flags, HWPort port) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HWPort findRoute(int destination) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printTable() {
		// TODO Auto-generated method stub
		
	}
}
