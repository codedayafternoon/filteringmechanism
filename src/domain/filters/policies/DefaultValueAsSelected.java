package domain.filters.policies;

import domain.filters.SelectedValuePolicy;

public class DefaultValueAsSelected extends SelectedValuePolicy {
    String value;

    @Override
    public String get(String defaultValue) {
        if(this.value == null)
            return defaultValue;
        return  this.value;
    }

    @Override
    public void set(String value) {
        this.value = value;
    }

    @Override
    public void reset(String defaultValue) {
        this.value = defaultValue;
    }

}
