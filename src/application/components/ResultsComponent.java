package application.components;

import application.model.CellPhone;
import domain.FilterContext;
import domain.filters.Filter;
import domain.hub.IResultHubListener;

import java.util.List;

public class ResultsComponent implements IResultHubListener {

    public ResultsComponent(FilterContext context) {
        context.GetHub().AddResultListener(this);
    }

    @Override
    public void ResultReceived(Object result) {
        System.out.println("===========================ResultsComponent===============================");
        for(CellPhone phone : (List<CellPhone>)result ){
            System.out.println(phone);
        }
        System.out.println("==========================================================================");
        System.out.println();
    }
}
