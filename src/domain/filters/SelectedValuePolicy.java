package domain.filters;

public abstract class SelectedValuePolicy {
    public abstract String get(String defaultValue);
    public abstract void set(String value);
    public abstract void reset(String defaultValue);
}
