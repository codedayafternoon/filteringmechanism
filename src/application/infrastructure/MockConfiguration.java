package application.infrastructure;

import domain.configuration.Configuration;
import domain.configuration.ExistingContainerActionType;
import domain.configuration.MissingContainerActionType;
import domain.configuration.NewContainerActionType;

public class MockConfiguration extends Configuration {

    @Override
    public MissingContainerActionType getMissingContainerActionType() {
        return MissingContainerActionType.RemoveFilters;
    }

    @Override
    public NewContainerActionType getNewContainerActionType() {
        return NewContainerActionType.AddFilters;
    }

    @Override
    public ExistingContainerActionType getExistingContainerActionType() {
        return ExistingContainerActionType.AddRemoveAndUpdate;
    }
}
