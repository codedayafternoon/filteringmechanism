package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;

import java.util.List;

public class NullExistingContainerAction extends ActionBase {

    public NullExistingContainerAction() {
        super(null, null);
    }

    @Override
    public void Execute(List<FilterContainer> containers) {

    }

    @Override
    public ActionType GetType() {
        return ActionType.MissingContainerAction;
    }
}
