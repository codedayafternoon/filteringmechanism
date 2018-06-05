package domain.filters.structures;

import java.util.List;

public class RangePart {
    private List<String> items;
    private String defaultValue;
    private String selectedValue;

    public RangePart(List<String> items, String defaultValue) {
        this.items = items;
        this.defaultValue = defaultValue;
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
        this.selectedValue = this.items.get(selectedValueFromIndex);

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
        if(!this.items.contains(this.selectedValue))
            return -1;

        return items.indexOf(this.selectedValue);
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
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
