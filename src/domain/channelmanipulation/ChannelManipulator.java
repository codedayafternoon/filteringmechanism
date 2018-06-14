package domain.channelmanipulation;

import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestHandler;
import domain.hub.Hub;
import domain.hub.IFilterHubListener;
import domain.hub.IParameterHubListener;
import domain.hub.IRequestHubListener;
import domain.notifier.NotifierChannelType;

public class ChannelManipulator {

    private final FilterController controller;
    private final Hub hub;

    public ChannelManipulator(FilterController controller, Hub hub) {
        this.controller = controller;
        this.hub = hub;
    }

    public void PauseChannel(NotifierChannelType channelType){
        switch (channelType){
            case FilterChannel:
                this.packFilterListeners();
                break;
            case ParameterChannel:
                this.packParameterListeners();
                break;
            case RequestChannel:
                this.packRequestListeners();
                break;
        }
    }

    public void UnPauseChannel(NotifierChannelType channelType){
        switch (channelType){
            case FilterChannel:
                this.unpackFilterListeners();
                break;
            case ParameterChannel:
                this.unpackParameterListeners();
                break;
            case RequestChannel:
                this.unpackRequestListeners();
                break;
        }
    }

    public void PauseAllFilterChannels(){
        this.PauseChannel(NotifierChannelType.FilterChannel);
        this.PauseChannel(NotifierChannelType.ParameterChannel);
        this.PauseChannel(NotifierChannelType.RequestChannel);
    }

    public void UnPauseAllFilterChannels(){
        this.UnPauseChannel(NotifierChannelType.FilterChannel);
        this.UnPauseChannel(NotifierChannelType.ParameterChannel);
        this.UnPauseChannel(NotifierChannelType.RequestChannel);
    }

    public void PauseRequestHandlerChannel(){
        IRequestHandler handler = this.controller.getRequestHandler();
        if(handler instanceof OpenRequestHandler)
            return;
        OpenRequestHandler open = new OpenRequestHandler(handler);
        this.controller.setRequestHandler(open);
    }

    public void UnPauseRequestHandlerChannel(){
        IRequestHandler open = this.controller.getRequestHandler();
        if(open instanceof OpenRequestHandler) {
            IRequestHandler handler = ((OpenRequestHandler)open).getRequestHandler();
            this.controller.setRequestHandler(handler);
        }
    }

    public void PauseBuilderChannel(){
        throw new Error("not implemented");
    }

    public void UnPauseBuilderChannel(){
        throw new Error("not implemented");
    }

    // ============================================= START HELPERS ======================================================

    private void packFilterListeners(){
        if(this.hub.getFilterListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getFilterListeners().size(); i++){
            IFilterHubListener listener = this.hub.getFilterListeners().get(0);
            if(listener instanceof OpenFilterHubListener)
                continue;
            OpenFilterHubListener open = new OpenFilterHubListener(listener);
            this.hub.getFilterListeners().remove(listener);
            this.hub.getFilterListeners().add(open);
        }
    }

    private void unpackFilterListeners(){
        if(this.hub.getFilterListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getFilterListeners().size(); i++){
            IFilterHubListener open = this.hub.getFilterListeners().get(0);
            if(!(open instanceof OpenFilterHubListener))
                continue;
            IFilterHubListener listener = ( (OpenFilterHubListener)open ).getListener();
            this.hub.getFilterListeners().remove(open);
            this.hub.getFilterListeners().add(listener);
        }
    }


    private void packParameterListeners() {
        if(this.hub.getParameterListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getParameterListeners().size(); i++){
            IParameterHubListener listener = this.hub.getParameterListeners().get(0);
            if(listener instanceof OpenParameterHubListener)
                continue;
            OpenParameterHubListener open = new OpenParameterHubListener(listener);
            this.hub.getParameterListeners().remove(listener);
            this.hub.getParameterListeners().add(open);
        }
    }

    private void unpackParameterListeners() {
        if(this.hub.getParameterListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getParameterListeners().size(); i++){
            IParameterHubListener open = this.hub.getParameterListeners().get(0);
            if(!(open instanceof OpenParameterHubListener))
                continue;
            IParameterHubListener listener = ( (OpenParameterHubListener)open ).getListener();
            this.hub.getParameterListeners().remove(open);
            this.hub.getParameterListeners().add(listener);
        }
    }

    private void packRequestListeners(){
        if(this.hub.getRequestListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getRequestListeners().size(); i++){
            IRequestHubListener listener = this.hub.getRequestListeners().get(0);
            if(listener instanceof OpenRequestHubListener)
                continue;
            OpenRequestHubListener open = new OpenRequestHubListener(listener);
            this.hub.getRequestListeners().remove(listener);
            this.hub.getRequestListeners().add(open);
        }
    }

    private void unpackRequestListeners(){
        if(this.hub.getRequestListeners().size() == 0)
            return;
        for(int i = 0; i < this.hub.getRequestListeners().size(); i++){
            IRequestHubListener open = this.hub.getRequestListeners().get(0);
            if(!(open instanceof OpenRequestHubListener))
                continue;
            IRequestHubListener listener = ( (OpenRequestHubListener)open ).getListener();
            this.hub.getRequestListeners().remove(open);
            this.hub.getRequestListeners().add(listener);
        }
    }

    // ============================================= END HELPERS ======================================================
}
