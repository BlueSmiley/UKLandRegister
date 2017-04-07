package gui.events;

import java.util.ArrayList;

public class Event {
	public interface EventOperation {
		public void run ();
	}
	
	protected ArrayList<EventOperation> connections = new ArrayList<EventOperation> ();
	
	public void connect (EventOperation operation) {
		connections.add(operation);
	}
	
	public void disconnect (EventOperation operation) {
		connections.remove(operation);
	}
	
	public void disconnectAll () {
		connections.clear();
	}
	
	public void trigger () {
		for (EventOperation operation: connections) {
			operation.run();
		}
	}
}