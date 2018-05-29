package domain.configuration;

import domain.filtercontroller.FilterContainer;

import java.util.List;

/**
 * Created by Jimfi on 5/28/2018.
 */
public abstract class Configuration {
    public abstract List<FilterContainer> GetContainers();
}
