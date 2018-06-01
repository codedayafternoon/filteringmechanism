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
        this.initializeController();
    }

    private void initializeController() {
        if(hub == null)
            hub = new Hub();
        if(controller == null) {
            controller = new FilterController(this.hub, this.handler, this.converter);
            hub.SetFilterController(controller);
        }
    }

    public FilterController GetController(){
        if(controller == null)
            throw new Error("controller has not initialized. Consider call Initialize before accessing any members");
        return controller;
    }

    public Builder GetBuilder(){
        if(this.builder == null)
            this.builder = new Builder(this.GetController());
        return this.builder;
    }

    public Hub GetHub(){
        if(hub == null)
            throw new Error("hub has not initialized. Consider call Initialize before accessing any members");
        return hub;
    }
}
