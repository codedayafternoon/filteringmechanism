package domain.configuration;

import domain.filtercontroller.FilterContainer;

import java.util.List;

public interface IAction {
    void Execute(List<FilterContainer> arrivedContainer);
    ActionType GetType();
}
