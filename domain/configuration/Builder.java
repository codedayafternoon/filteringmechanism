package domain.configuration;

import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;
import domain.filters.types.CheckBoxFilter;

import java.util.List;

/**
 * Created by Jimfi on 5/28/2018.
 */
public class Builder {

    private FilterController controller;

    public Builder(FilterController controller) {
        this.controller = controller;
    }

    public void Build(Configuration config){
        List<FilterContainer> containers = config.GetContainers();
        for(FilterContainer c : containers){
            this.controller.AddContainer(c);
        }
    }
}
