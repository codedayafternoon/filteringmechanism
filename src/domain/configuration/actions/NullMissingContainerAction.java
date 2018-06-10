package domain.configuration.actions;

import domain.configuration.ActionType;
import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;

import java.util.List;

public class NullMissingContainerAction extends ActionBase {

    public NullMissingContainerAction() {
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
