/**
 * Interface fuer den Router.
 * 
 * Dieses Interface darf nicht veraendert werden. Alle IP-Adressen und Gateways
 * werden mit Hilfe des Datentypes Integer uebergeben. Netzmasken werden in
 * CIDR-Notation als Routingpraefix mit Hilfe des Datentypes byte uebergeben.
 * 
 */
public interface IRouter {

	/**
	 * Bindet die lokale IP-Adresse an den Uebergebenen Netzwerkport.
	 * 
	 * @param int localIP Die zu setzende IP-Adresse
	 * @param HWPort
	 *            port Der Netzwerkport, an den die Adresse gebunden werden soll
	 */
	public void setLocalIP(int localIP, HWPort port);

	/**
	 * Gibt die Lokale IP-Adresse am Uebergebenen Netzwerkport zurueck.
	 * 
	 * @param HWPort
	 *            port Der Netzwerkport dessen Adresse geliefert werden soll.
	 * @return int IP-Adresse
	 */
	public int getLocalIP(HWPort port);

	/**
	 * Fuegt eine Route hinzu.
	 * 
	 * Ist bereits eine Route mit dem gleichen Zielnetzwerk in der
	 * Routing-Tabelle, wird eine Fehlermeldung ausgegeben, zum Beispiel:
	 * "Failed to add route: Route already in use!"
	 * 
	 * Wurde die Route erfolgreich hinzugefuegt, muss dies ausgegeben. Weiterhin
	 * muss die Netzwerkadresse und die Broadcastadresse des Zielnetzes
	 * ausgegeben werden. Falls das Flag "H" gesetzt wurde, wird lediglich die
	 * Host-IP-Adresse ausgegeben.
	 * 
	 * @param int destinationNetwork Adresse des Zielnetzwerks fuer eine neue
	 *        Route
	 * @param byte prefix Das Routingpraefix (Subnetzmaske) in CIDR-Notation /XY
	 * @param int gateway Adresse des naechsten Routers fuer die Route
	 * @param Flags
	 *            [] flags
	 * @param HWPort
	 *            port Der Hardware-Port, auf dem Pakete fuer die Route
	 *            weitergeleitet werden
	 * @return boolean Wurde die Route erfolgreich eingetragen, wird true
	 *         zurueck gegeben. Ansonsten wird false zurueck gegeben.
	 * 
	 */
	public boolean routeAdd(int destinationNetwork, byte prefix, int gateway,
			Flags[] flags, HWPort port);

	/**
	 * Loescht einen Eintrag aus der Routing-Tabelle.
	 * 
	 * Der Schluessel, um den Eintrag zu erkennen, ist das Zielnetzwerk. Am Ende
	 * muss ausgegeben werden, dass die Route geloescht wurde. Es muss
	 * ueberprueft werden, ob die Subnetzmaske gueltig ist.
	 * 
	 * @param int destinationNetwork Adresse des Zielnetzwerks fuer die zu
	 *        loeschende Route
	 * @return boolean Wurde die Route erfolgreich geloescht, wird true zurueck
	 *         gegeben. Ansonsten wird false zurueck gegeben.
	 */
	public boolean routeDelete(int destinationNetwork);

	/**
	 * Modifiziert eine bestehende Route.
	 * 
	 * Der Schluessel, um das Netzwerk zu identifizieren, ist wiederum das
	 * Zielnetzwerk. Diese Methode funktioniert genau wie routeAdd, mit dem
	 * Unterschied, dass das davon ausgegangen wird, dass bereits eine Route mit
	 * dem gleichen Zielnetzwerk vorhanden ist. Dieses wird durch das neue
	 * ersetzt.
	 * 
	 * Ist das Zielnetzwerk noch nicht in der Routig-Tabelle eingetragen, muss
	 * eine Fehlermeldung ausgegeben werden.
	 * 
	 * Wie bei routeAdd soll beim modifizieren wieder Netzwerkadresse und
	 * Broadcastadresse des neuen Zielnetzwerkes ausgegeben werden.
	 * 
	 * @param int destinationNetwork Adresse des Zielnetzwerks fuer die zu
	 *        aendernde Route
	 * @param byte prefix Das Routingpraefix (Subnetzmaske) in CIDR-Notation /XY
	 * @param int gateway Adresse des naechsten Routers fuer die Route
	 * @param Flags
	 *            [] flags
	 * @param HWPort
	 *            port Der Hardware-Port, auf dem Pakete fuer die Route
	 *            weitergeleitet werden
	 * @return boolean Wurde die Route erfolgreich modifiziert, wird true
	 *         zurueck gegeben. Ansonsten wird false zurueck gegeben.
	 */
	public boolean routeModify(int destinationNetwork, byte prefix,
			int gateway, Flags[] flags, HWPort port);

	/**
	 * Sucht fuer eine Zieladresse die Passende Route.
	 * 
	 * Diese Methode bekommt eine IP-Adresse uebergeben und sucht aus der
	 * Routing-Tabbelle die passende Route raus. Der Rueckgabewert ist das
	 * Interface, ueber das geroutet werden soll.
	 * 
	 * Ist die Zieladresse der eigene Rechner, wird der String "localhost"
	 * zurueckgegegben. Auf der Konsole wird dann ausgegeben, dass der eigene
	 * Rechner das Ziel ist.
	 * 
	 * Kann keine passende Route gefunden werden wird der String
	 * "no route to host" zurueckgegeben und eine Fehlermeldung ausgegeben.
	 * 
	 * Achten sie auf die korrekten Flags.
	 * 
	 * @param int destination Adresse des Zielhosts fuer den die Route gesucht
	 *        wird
	 * @return HWPort Das Interface / der Hardware-Port auf fuer die Route
	 */
	public HWPort findRoute(int destination);

	/**
	 * Druckt die Routing-Tabelle aus.
	 * 
	 * Die Ausgabe muss aehnlich sein, wie die von netstat -nr, jedoch mit CIDR-
	 * Notation. Achten sie auch korrekte Einrückung. Eine exemplarische
	 * Ausgabe koennte so aussehen:
	 * 
	 * Destination Prefix Gateway Flags Iface 212.69.12.0 /28 0.0.0.0 U eth0 ...
	 */
	public void printTable();

}
