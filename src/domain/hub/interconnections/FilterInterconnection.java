package domain.hub.interconnections;

import java.util.ArrayList;
import java.util.List;

public class FilterInterconnection {

    public EventSubjectPair When;
    public List<EventSubjectPair> Then;
    private final Object id;

    public FilterInterconnection(Object id) {
        this.id = id;
        this.Then = new ArrayList<>();
        this.When = new EventSubjectPair();
    }

    public Object getId() {
        return id;
    }

}
