package domain;

import domain.channelmanipulation.ChannelManipulator;
import domain.configuration.Builder;
import domain.configuration.Configuration;
import domain.filtercontroller.*;
import domain.hub.Hub;
import domain.hub.IHub;

public class FilterContext {
    private static FilterController controller;
    private static Hub hub;
    private static ChannelManipulator channelManipulator;
    private IRequestHandler handler;
    private IRequestConverter converter;
    private Configuration configuration;
    private Builder builder;

    public void Initialize(IRequestHandler requestHandler, IRequestConverter requestConverter,Configuration configuration ){
        if(requestHandler == null || requestConverter == null || configuration == null)
            throw new Error("context cannot be initialized with null parameter");

        if(this.handler == null)
            this.handler = requestHandler;
        if(this.converter == null)
            this.converter = requestConverter;
        if(this.configuration == null)
            this.configuration = configuration;

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

    public IFilterController GetController(){
        if(controller == null)
            throw new Error("controller has not initialized. Consider call Initialize before accessing any members");
        return controller;
    }

    public Builder GetBuilder(){
        if(FilterContext.controller == null)
            throw new Error("controller has not initialized. Consider call Initialize before accessing any members");
        if(this.builder == null)
            this.builder = new Builder(FilterContext.controller, this.configuration);
        return this.builder;
    }

    public IHub GetHub(){
        if(hub == null)
            throw new Error("hub has not initialized. Consider call Initialize before accessing any members");
        return hub;
    }

    public ChannelManipulator GetChannelmanipulator(){
        if(controller == null || hub == null)
            throw new Error("controller or hub are not initialized. Consider call Initialize before accessing any memberd");
        if(channelManipulator == null)
            channelManipulator = new ChannelManipulator(controller, hub);
        return channelManipulator;
    }

    public void Dispose(){
        this.handler = null;
        this.builder = null;
        hub = null;
        controller = null;
        this.converter = null;
        this.configuration = null;
    }
}
