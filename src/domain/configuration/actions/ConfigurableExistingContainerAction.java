package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableExistingContainerAction extends ActionBase {

    private boolean add;
    private boolean remove;
    private boolean update;

    public ConfigurableExistingContainerAction(FilterController controller, IActionObserver observer,
                                               boolean add, boolean remove, boolean update) {
        super(controller, observer);
        this.add = add;
        this.remove = remove;
        this.update = update;
    }

    @Override
    public void Execute(List<FilterContainer> arrivedContainers) {
        List<FilterContainer> existingContainers = this.findExistingContainers(arrivedContainers);

        if(this.add){
            for(FilterContainer arrivedContainer : arrivedContainers){
                List<Filter> newFilters = this.findNewFiltersForContainer(arrivedContainer);
                FilterContainer existingContainer = this.controller.GetContainerById(arrivedContainer.GetId());
                for(Filter f : newFilters){
                    existingContainer.AddFilter(f);
                    super.FilterAdded(this, f);
                }
            }
        }

        if(this.update){
            for(FilterContainer existingContainer : existingContainers){
                FilterContainer arrivedContainer = this.ExtractContainerById(arrivedContainers, existingContainer.GetId());
                if(arrivedContainer == null)
                    continue;

                if(existingContainer.UpdateFrom(arrivedContainer))
                    super.ContainerUpdated(this, existingContainer);

                List<Filter> existingFilters = this.findSameFilters(existingContainer, arrivedContainer);
                for(Filter f : existingFilters){
                    Filter arrived = arrivedContainer.GetFilterById(f.Id);
                    //f.setName(arrived.getName());
                    f.UpdateFrom(arrived);
                    // TODO update rest properties
                }
            }
        }

        if(this.remove){
            for(FilterContainer container : existingContainers){
                FilterContainer arrivedContainer = this.ExtractContainerById(arrivedContainers, container.GetId()); //arrivedContainers.stream().filter(x->x.GetId().equals(container.GetId())).findFirst().get();
                List<Filter> newFilters = this.findMissingFiltersForContainer(arrivedContainer);
                for(Filter f : newFilters){
                    container.RemoveFilter(f);
                    super.FilterRemoved(this, f);
                }
            }
        }

    }

    private List<Filter> findSameFilters(FilterContainer c1, FilterContainer c2) {
        List<Filter> filters = new ArrayList<>();
        for(Filter f : c1.GetFilters()){
            for(Filter f2 : c2.GetFilters()){
                if(f.Id.equals(f2.Id)){
                    filters.add(f);
                    break;
                }
            }
        }
        return filters;
    }

    private FilterContainer ExtractContainerById(List<FilterContainer> containers, Object id){
        for(FilterContainer c : containers )
            if(c.GetId().equals(id))
                return c;
        return null;
    }

    private List<Filter> findNewFiltersForContainer(FilterContainer arrivedContainer){
        List<Filter> res = new ArrayList<>();

        FilterContainer existingContainer = this.controller.GetContainerById(arrivedContainer.GetId());
        if(existingContainer == null)
            return res;
        for(Filter arrivedFilter : arrivedContainer.GetFilters()){
            Filter newFilter = existingContainer.GetFilterById(arrivedFilter.Id);
            if(newFilter == null){
                res.add(arrivedFilter);
            }
        }

        return res;
    }

    private List<Filter> findMissingFiltersForContainer(FilterContainer arrivedContainer){
        List<Filter> res = new ArrayList<>();

        FilterContainer existingContainer = this.controller.GetContainerById(arrivedContainer.GetId());
        for(Filter existingFilter : existingContainer.GetFilters()){
            Filter newFilter = arrivedContainer.GetFilterById(existingFilter.Id);
            if(newFilter == null){
                res.add(existingFilter);
            }
        }

        return res;
    }

    private List<FilterContainer> findExistingContainers(List<FilterContainer> containers) {
        List<FilterContainer> res = new ArrayList<>();
        for(FilterContainer c : containers){
            FilterContainer existing = this.controller.FindContainer(c.GetId());
            if(existing != null)
                res.add(existing);
        }
        return res;
    }

    @Override
    public ActionType GetType() {
        return ActionType.ExistingContainerAction;
    }
}
