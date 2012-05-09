package packet_pkg;

public enum PacketType {
	RREQ,			// Route Request - used in Route Discovery
	RREP,			// Route Reply - used in Route Discovery
	MACT,			// Multicast Activation - used in Route Discovery
	MRATE,			// Multicast Rate - in attack mitigation
	GROUP_HELLO,	// Group Hello - Source sends it periodically
	BEACON,			// Beacon - nodes will send beacon messages to upstream node
	STICK_TO_ME,	// StickToMe - nodes will send StickToMe messages to downstream nodes
	DATA_PKT,		// Data Packet
	TREE_PRUNE		// TreePrune - When a node wants to leave the multicast group
}