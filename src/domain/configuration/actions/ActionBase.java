package domain.configuration.actions;

import domain.configuration.IAction;
import domain.configuration.IActionObservable;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filters.Filter;

import java.util.List;

public abstract class ActionBase implements IAction, IActionObservable {

    protected FilterController controller;
    //protected List<FilterContainer> arrivedContainers;
    private IActionObserver observer;

    public ActionBase(FilterController controller, IActionObserver observer) {
        this.controller = controller;
        this.observer = observer;
    }
//
//    @Override
//    public void SetContainers(List<FilterContainer> containers){
//        this.arrivedContainers = containers;
//    }

    protected void ContainerAdded(IAction action, FilterContainer container) {
        this.observer.ContainerAdded(action.GetType(), container);
    }

    protected void FilterAdded(IAction action, Filter f) {
        this.observer.FilterAdded(action.GetType(), f);
    }

    protected void ContainerRemoved(IAction action, FilterContainer container) {
        this.observer.ContainerRemoved(action.GetType(), container);
    }

    protected void FilterRemoved(IAction action, Filter f) {
        this.observer.FilterRemoved(action.GetType(), f);
    }
}
