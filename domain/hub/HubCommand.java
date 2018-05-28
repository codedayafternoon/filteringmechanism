package domain.hub;

public class HubCommand {
	public final String ContainerName;
	public final String FilterName;
	public final String State;
	public final int Count;

	/**
	 * for updating both state and count
	 * @param containerName
	 * @param filterName
	 * @param count
	 * @param filterState
	 */
	public HubCommand(String containerName, String filterName, int count, String filterState) {
		this.ContainerName = containerName;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = count;
	}

	/**
	 * for updating only state
	 * @param containerName
	 * @param filterName
	 * @param filterState
	 */
	public HubCommand(String containerName, String filterName, String filterState) {
		this.ContainerName = containerName;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = -1;
	}

	/**
	 * for updating only count
	 * @param containerName
	 * @param filterName
	 * @param count
	 */
	public HubCommand(String containerName, String filterName, int count) {
		this.ContainerName = containerName;
		this.FilterName = filterName;
		this.State = null;
		this.Count = count;
	}
	
	@Override
	public String toString() {
		return "container: " + this.ContainerName + " filterName: " + this.FilterName + " state:" + this.State + " count:" + this.Count;
	}
	
}