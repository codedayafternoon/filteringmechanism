package domain.hub.interconnections;

import java.util.ArrayList;
import java.util.List;

public class FilterInterconnection {

    public EventSubjectPair When;
    public List<EventSubjectPair> Then;

    public FilterInterconnection() {
        this.Then = new ArrayList<>();
        this.When = new EventSubjectPair();
    }

}
