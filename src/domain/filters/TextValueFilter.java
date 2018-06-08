package domain.filters;

import domain.filters.formatters.DefaultValuePostFormatter;

public abstract class TextValueFilter extends Filter {

    protected IValuePostFormatter postFormatter;

    public TextValueFilter(Object id, String name, INotifier notifier) {
        super(id, name, notifier);
    }

    public IValuePostFormatter GetPostFormatter(){
        if(this.postFormatter == null)
            this.postFormatter = new DefaultValuePostFormatter();
        return this.postFormatter;
    }

    public void SetValuePostFormatter(IValuePostFormatter formatter){
        this.postFormatter = formatter;
    }

    public final String GetParameterValue(){
        String res = this.DoGetParameterValue();
        for(String number : this.GetPostFormatter().Extract(res)) {
            String formattedNumber = this.GetPostFormatter().Format(number);
            if(!number.equals(formattedNumber))
                res = res.replace(number, formattedNumber);
        }
        return res;
    }

    protected abstract String DoGetParameterValue();


}
