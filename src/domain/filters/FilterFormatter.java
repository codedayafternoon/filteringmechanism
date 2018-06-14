package domain.filters;

import domain.filters.types.CompositeFilter;

public abstract class FilterFormatter {
    private String pattern;
    private String formatted;
    private Object Id;


    public FilterFormatter(Object id, String pattern) {
        if(id == null || pattern == null || pattern.trim().equals(""))
            throw new Error("Formatter cannot initialized without id and pattern");
        this.Id = id;
        this.pattern = pattern;
    }

    public Object getId(){
        return this.Id;
    }

    public String Format(Filter filter){
        this.formatted = this.pattern;

        if(this.pattern.contains("$f[") && filter instanceof CompositeFilter){
            CompositeFilter compositeFilter = (CompositeFilter)filter;
            for(int i = 0; i < compositeFilter.getFilters().size(); i++){
                String t = "$f[" + i + "]";
                Filter f = compositeFilter.getFilters().get(i);
                this.formatted = this.replace(t+".fn", this.formatted,f );
                this.formatted = this.replace(t+".cn", this.formatted,f );
                this.formatted = this.replace(t+".fv[0]", this.formatted,f );
                this.formatted = this.replace(t+".fv[1]", this.formatted,f );
                this.formatted = this.replace(t+".fv", this.formatted,f );
            }
        }

        if(this.pattern.contains("$cn") ){
            if(filter.GetContainer() != null) {
                this.formatted = this.formatted.replace("$cn", filter.GetContainer().GetName());
            }else {
                this.formatted = this.formatted.replace("$cn", "");
            }
        }
        if(this.pattern.contains("$fn")){
            this.formatted = this.formatted.replace("$fn", filter.getName());
        }
        if(this.pattern.contains("$fv[0]")){
            String v = filter.GetValue(0);
            v = v == null ? "" : v;
            this.formatted = this.formatted.replace("$fv[0]", v);
        }else if(this.pattern.contains("$fv[1]")){
            String v = filter.GetValue(1);
            v = v == null ? "" : v;
            this.formatted = this.formatted.replace("$fv[1]", v);
        }
        else if(this.pattern.contains("$fv")){
            String v = filter.GetValue(0);
            v = v == null ? "" : v;
            this.formatted = this.formatted.replace("$fv", v);
        }

        return formatted;
    }

    private String replace(String what, String where, Filter from){
        if(what == null || where == null || from == null)
            return where;
        if(what.contains("cn")){
            if(from.GetContainer() != null) {
                return where.replace(what, from.GetContainer().GetName());
            }else {
                return where.replace(what, "");
            }
        }else if(what.contains("fn")){
            return where.replace(what, from.getName());
        }else if(what.contains("fv[0]")){
            String v = from.GetValue(0);
            v = v == null ? "" : v;
            return where.replace(what, v);
        }else if(what.contains("fv[1]")){
            String v = from.GetValue(1);
            v = v == null ? "" : v;
            return where.replace(what, v);
        }
        else if(what.contains("fv")){
            String v = from.GetValue(0);
            v = v == null ? "" : v;
            return where.replace(what, v);
        }

        return what;
    }

    public String cn(String cn){
        return cn;
    }

    public String fn(String fn){
        return fn;
    }

    public String fv(String filterValue, int valueIndex){
        return filterValue;
    }

}
