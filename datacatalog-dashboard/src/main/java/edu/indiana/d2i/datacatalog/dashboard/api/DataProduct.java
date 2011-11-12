package edu.indiana.d2i.datacatalog.dashboard.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DataProduct {
    public String name;

    public void setName(String name){
        this.name = name;
    }
}
