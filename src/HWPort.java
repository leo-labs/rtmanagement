/**
 * Der Router hat verschiedene Hardwareschnittstellen. Zusaetzlich existieren
 * noch Repraesentationen dafuer, dass keine Route zum Ziel existiert, sowie de
 * Verweis auf den Router selbst.
 * 
 * Die Position eines HWPort-Elements innerhalb der Enumeration kann mit der
 * Methode ordinal() abgefragt werden.
 * 
 * Beispiel:
 * 
 * HWPort p = HWPort.eth2; p.ordinal(); // Liefert 2 zurueck
 */
public enum HWPort {

	/**
	 * Netzwerkport 0 ordinal() := 0
	 */
	eth0(),
	/**
	 * Netzwerkport 1 ordinal() := 1
	 */
	eth1(),
	/**
	 * Netzwerkport 2 ordinal() := 2
	 */
	eth2(),
	/**
	 * Netzwerkport 3 ordinal() := 3
	 */
	eth3(),
	/**
	 * Repraesentation dafuer, dass keine Route zum Ziel gefunden wurde
	 * ordinal() := 4
	 */
	no_route_to_host(),
	/**
	 * Loopback auf den Router ordinal() := 5
	 */
	localhost();

}