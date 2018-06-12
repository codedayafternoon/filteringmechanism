package domain.filters.structures;

import domain.filters.SelectedValuePolicy;
import domain.filters.policies.NullValueAsSelected;

import java.util.List;

public class RangePart {
    private List<String> items;
    private String defaultValue;
    private SelectedValuePolicy selectedValuePolicy;
    //private String selectedValue;

    public RangePart(SelectedValuePolicy selectedValuePolicy, List<String> items, String defaultValue) {
        this.items = items;
        this.defaultValue = defaultValue;
        this.selectedValuePolicy = selectedValuePolicy;
    }

    public boolean UpdateFrom(RangePart part){
        if(part == null)
            return false;

        if(part.getItems() == null || part.getItems().size() != this.items.size())
            return false;

        int selectedValueFromIndex = this.getSelectedValueIndex();
        int selectedDefaultValueFromIndex = this.getDefaultValueIndex();

        this.items.clear();
        for(String x : part.getItems()){
            this.items.add(x);
        }
        if(selectedValueFromIndex != -1) {
            //this.selectedValue = this.items.get(selectedValueFromIndex);
            this.selectedValuePolicy.set(this.items.get(selectedValueFromIndex));
        }

        if(part.getDefaultValue() == null){
            if(selectedDefaultValueFromIndex != -1)
            {
                this.defaultValue = this.items.get(selectedDefaultValueFromIndex);
            }
        }else {
            this.defaultValue = part.getDefaultValue();
        }
        return true;
    }

    private int getSelectedValueIndex(){
        if(this.items.size() == 0)
            return -1;
        if(!this.items.contains( this.selectedValuePolicy.get(this.defaultValue)))
            return -1;

        return items.indexOf(this.selectedValuePolicy.get(this.defaultValue));
    }

    private int getDefaultValueIndex(){
        if(this.items.size() == 0)
            return -1;
        if(!this.items.contains(this.defaultValue))
            return -1;

        return items.indexOf(this.defaultValue);
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSelectedValue() {
        return this.selectedValuePolicy.get(this.defaultValue);
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValuePolicy.set(selectedValue);
    }

    public boolean IsReset() {
        if(this.selectedValuePolicy.get(this.defaultValue) == null || this.defaultValue == null)
            return true;
        return this.selectedValuePolicy.get(defaultValue).equals(this.defaultValue);
    }

    public void Reset() {
        if(this.IsReset())
            return;
        this.selectedValuePolicy.reset( this.defaultValue );
    }

    public void SetSelectedValuePolicy(SelectedValuePolicy policy) {
        this.selectedValuePolicy = policy;
    }
}
