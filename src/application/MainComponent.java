package application;

import application.components.FilterPreviewComponent;
import application.components.FiltersTreeViewComponent;
import application.components.ResultsComponent;
import application.infrastructure.MockConfiguration;
import domain.FilterContext;
import domain.buildins.UrlBuilder;
import domain.buildins.UrlQueryConverter;
import domain.filtercontroller.IFilterController;
import domain.filtercontroller.IRequestConverter;
import domain.filters.Filter;
import domain.filters.FilterPropertyType;
import domain.hub.IFilterHubListener;
import domain.hub.IHub;
import testing.BuilderTesting;

public class MainComponent implements IFilterHubListener {

    private IFilterController controller;
    private IHub hub;
    private ResultsComponent resultsComponent;
    private FiltersTreeViewComponent filtersComponent;
    private FilterPreviewComponent previewComponent;
    private IRequestConverter converter;
    private String url = "";

    public MainComponent(FilterContext context, ResultsComponent resultsComponent, FiltersTreeViewComponent filtersComponent, FilterPreviewComponent previewComponent) {

        this.controller = context.GetController();
        this.hub = context.GetHub();
        this.hub.AddFilterListener(this);

        this.resultsComponent = resultsComponent;
        this.filtersComponent = filtersComponent;
        this.previewComponent = previewComponent;

        this.converter = new UrlQueryConverter(new UrlBuilder(",", "&"));
    }

    public void Print(){
        System.out.println("=============================MainComponent================================");
        System.out.println("url:http://site.com/search?"+this.url);
        System.out.println("==========================================================================");
        System.out.println();
    }

    private void refreshUrl(){
        this.url = this.converter.Convert(this.hub.GetFilters());
    }

    @Override
    public void FilterChanged(Filter filter) {
        this.refreshUrl();
        this.Print();
    }

    @Override
    public void FilterReset(Filter filter) {
        this.refreshUrl();
        this.Print();
    }

    @Override
    public void FilterPropertyChanged(Filter filter, String old, String _new, FilterPropertyType propType) {

    }

    @Override
    public void FilterUpdated(Filter filter) {

    }


}
