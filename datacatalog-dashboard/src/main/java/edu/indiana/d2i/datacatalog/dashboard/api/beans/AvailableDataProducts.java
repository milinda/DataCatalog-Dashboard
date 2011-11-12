package edu.indiana.d2i.datacatalog.dashboard.api.beans;

import edu.indiana.d2i.datacatalog.dashboard.api.DataProduct;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class AvailableDataProducts {
    public List<DataProduct> dataProducts = new ArrayList<DataProduct>();

    public void addDataProduct(DataProduct dataProduct){
        dataProducts.add(dataProduct);
    }
}
