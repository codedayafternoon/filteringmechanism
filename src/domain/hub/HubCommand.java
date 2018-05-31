package domain.hub;

public class HubCommand {
	private Object Id;
	public final String ContainerName;
	public final String FilterName;
	public final String State;
	public final int Count;

	public Object GetId(){
		return this.Id;
	}

	/**
	 * for updating both state and count
	 * @param id
	 * @param containerName
	 * @param filterName
	 * @param count
	 * @param filterState
	 */
	public HubCommand(Object id, String containerName, String filterName, int count, String filterState) {
		this.Id = id;
		this.ContainerName = containerName;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = count;
	}

	/**
	 * for updating only state
	 * @param id
	 * @param containerName
	 * @param filterName
	 * @param filterState
	 */
	public HubCommand(int id, String containerName, String filterName, String filterState) {
		this.Id = id;
		this.ContainerName = containerName;
		this.FilterName = filterName;
		this.State = filterState;
		this.Count = -1;
	}

	/**
	 * for updating only count
	 * @param id
	 * @param containerName
	 * @param filterName
	 * @param count
	 */
	public HubCommand(int id, String containerName, String filterName, int count) {
		this.Id = id;
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