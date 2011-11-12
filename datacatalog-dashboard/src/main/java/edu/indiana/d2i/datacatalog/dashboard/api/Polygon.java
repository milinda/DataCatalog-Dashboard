package edu.indiana.d2i.datacatalog.dashboard.api;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collection;

@XmlRootElement(name = "polygon")
public class Polygon {

    public final Collection<Point> points = new ArrayList<Point>();

    public void addPoint(Point p){
        points.add(p);
    }

    public Collection<Point> getPoints(){
        return this.points;
    }

}
