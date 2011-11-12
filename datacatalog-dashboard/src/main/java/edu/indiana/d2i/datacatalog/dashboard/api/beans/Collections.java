package edu.indiana.d2i.datacatalog.dashboard.api.beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Collections {

    public List<String> collections = new ArrayList<String>();

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
}
