package domain.filters.policies;

import domain.filters.SelectedValuePolicy;

public class NullValueAsSelected extends SelectedValuePolicy {

    String x;

    @Override
    public String get(String defaultValue) {
        return x;
    }

    @Override
    public void set(String value) {
        this.x = value;
    }

    @Override
    public void reset(String defaultValue) {
        this.x = null;
    }
}
