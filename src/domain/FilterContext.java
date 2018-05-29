package domain;

import domain.configuration.Builder;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filtercontroller.IRequestHandler;
import domain.filters.Filter;
import domain.hub.Hub;

public class FilterContext {
    private static FilterController controller;
    private static Hub hub;
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
        if(controller == null)
            controller = new FilterController(this.GetHub(), this.handler, this.converter);
        return controller;
    }

    public Builder GetBuilder(){
        if(this.builder == null)
            this.builder = new Builder(this.GetController());
        return this.builder;
    }

    public Hub GetHub(){
        if(hub == null)
            hub = new Hub();
        return hub;
    }
}
