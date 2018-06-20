package application;

import java.util.List;

import domain.filtercontroller.FilterContainer;
import domain.filtercontroller.FilterController;
import domain.filtercontroller.IRequestHandler;
import domain.filtercontroller.IRequestConverter;
import domain.hub.Hub;
import domain.hub.IHub;

public class Controller extends FilterController {
	
	public Controller(List<FilterContainer> containers, Hub hub, IRequestHandler receiver, IRequestConverter requestConverter) {
		super(containers, hub, receiver, requestConverter);
	}

}
