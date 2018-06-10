package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class AddNewFiltersNewContainerAction extends  ActionBase{

    public AddNewFiltersNewContainerAction(FilterController controller, IActionObserver observer) {
        super(controller, observer);
    }

    @Override
    public void Execute(List<FilterContainer> arrivedContainers) {
        List<FilterContainer> newContainers = this.findNewContainers(arrivedContainers);
        for(FilterContainer container : newContainers){
            this.controller.AddContainer(container);
            this.notifyContainerAdded(container);
        }

    }

    @Override
    public ActionType GetType() {
        return ActionType.NewContainerAction;
    }

    private void notifyContainerAdded(FilterContainer container) {
        super.ContainerAdded(this, container);
        for(Filter f : container.GetFilters()){
            super.FilterAdded(this, f);
        }
    }

    private List<FilterContainer> findNewContainers(List<FilterContainer> containers) {
        List<FilterContainer> res = new ArrayList<>();
        for(FilterContainer c : containers){
            FilterContainer existing = this.controller.GetContainerById(c.GetId());
            if(existing == null)
                res.add(c);
        }
        return res;
    }
}
