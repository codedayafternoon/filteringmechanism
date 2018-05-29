package domain;

import domain.configuration.Builder;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.hub.Hub;

public class FilterContext {
    private static FilterController controller;
    Hub hub;
    private IRequestHandler handler;
    private IRequestConverter converter;
    private Builder builder;

    public void Initialize(IRequestHandler requestHandler, IRequestConverter requestConverter ){
        if(this.handler == null)
            this.handler = requestHandler;
        if(this.converter == null)
            this.converter = requestConverter;
    }

    public FilterController GetController(){
        if(this.controller == null)
            this.controller = new FilterController(this.GetHub(), this.handler, this.converter);
        return this.controller;
    }

    public Builder GetBuilder(){
        if(this.builder == null)
            this.builder = new Builder(this.GetController());
        return this.builder;
    }

    private Hub GetHub(){
        if(this.hub == null)
            this.hub = new Hub();
        return this.hub;
    }
}
