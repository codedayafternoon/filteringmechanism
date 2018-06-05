package domain.configuration;

public abstract class Configuration  {
    public abstract MissingContainerActionType getMissingContainerActionType();
    public abstract NewContainerActionType getNewContainerActionType();
    public abstract ExistingContainerActionType getExistingContainerActionType();
}
