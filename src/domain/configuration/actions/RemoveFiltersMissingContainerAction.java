package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class RemoveFiltersMissingContainerAction extends ActionBase{

    public RemoveFiltersMissingContainerAction(FilterController controller, IActionObserver observer) {
        super(controller, observer);
    }

    @Override
    public void Execute(List<FilterContainer> arrivedContainers) {
        List<FilterContainer> missingContainers = this.findMissingContainers(arrivedContainers);
        for(FilterContainer container : missingContainers){
            this.controller.RemoveContainerById(container);
            this.notifyContainerRemoved(container);
        }
    }

    private void notifyContainerRemoved(FilterContainer container) {
        super.ContainerRemoved(this, container);
        for(Filter f : container.GetFilters()){
            super.FilterRemoved(this, f);
        }
    }

    private List<FilterContainer> findMissingContainers(List<FilterContainer> containers) {
        List<FilterContainer> res = new ArrayList<>();
        for(FilterContainer existing : this.controller.GetContainers()){
            FilterContainer newcontainer = this.ExtractContainerById(containers, existing.GetId());
            if(newcontainer == null)
                res.add(existing);
        }
        return res;
    }

    private FilterContainer ExtractContainerById(List<FilterContainer> containers, Object id){
        for(FilterContainer c : containers){
            if(c.GetId().equals(id))
                return c;
        }
        return null;
    }

    @Override
    public ActionType GetType() {
        return ActionType.MissingContainerAction;
    }
}
