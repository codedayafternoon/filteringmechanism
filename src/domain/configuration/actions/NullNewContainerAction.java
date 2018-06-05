package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;

import java.util.List;

public class NullNewContainerAction extends ActionBase {

    public NullNewContainerAction() {
        super(null, null);
    }

    @Override
    public void Execute(List<FilterContainer> container) {

    }

    @Override
    public ActionType GetType() {
        return ActionType.NewContainerAction;
    }
}
