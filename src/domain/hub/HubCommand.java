package domain.hub;

public class HubCommand {
	//private Object Id;
	public final Object ContainerId;
	public final String ContainerName;
	public final Object FilterId;
	public final String FilterName;
	public final String State;
	public final int Count;

	/**
	 * for updating both state and count
	 */
	public HubCommand(Object containerId, String containerName, Object filterId, String filterName, int count, String filterState) {
		this.ContainerId = containerId;
		this.ContainerName = containerName;
		this.FilterId = filterId;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = count;
	}

	/**
	 * for updating only state
	 */
	public HubCommand(Object containerId, String containerName, Object filterId, String filterName, String filterState) {
		this.ContainerId = containerId;
		this.ContainerName = containerName;
		this.FilterId = filterId;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = -1;
	}

	/**
	 * for updating only count
	 */
	public HubCommand( Object containerId, String containerName, Object filterId, String filterName, int count) {
		this.ContainerId = containerId;
		this.ContainerName = containerName;
		this.FilterId = filterId;
		this.FilterName = filterName;
		this.State = null;
		this.Count = count;
	}
	
	@Override
	public String toString() {
		return "container: " + this.ContainerName + " filterName: " + this.FilterName + " state:" + this.State + " count:" + this.Count;
	}
	
}