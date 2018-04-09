
import java.util.ArrayList;
import java.util.*;

class stringComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
    }
}

public class Location {
    private List<String> name;
    
    public Location () { name = new ArrayList<>();}
    
    public Location(String st1, String st2) {
        this();
        setName(st1, st2); }
    
    public Location(String st1, String st2, String st3) {
        this();
        setName(st1, st2, st3); }
    
    public void setName(String st)
    {
        if (st != null)
            name.add(st);
        sortName();
    }
    public void setName(String st1, String st2)
    {
        if( st1 != null && st2 != null) {
            name.add(st1);
            name.add(st2);
        }
        sortName();
    }
    
    public void setName(String st1, String st2, String st3)
    {
        if( st1 != null && st2 != null) {
            name.add(st1);
            name.add(st2);
            name.add(st3);
        }
        sortName();
    }
    
    private void sortName() { Collections.sort(name, new stringComparator());}
    
    public List<String> getName() { return name; }
    
    public String toString() { return "Vertex"+ name; }
    
    @Override
    public boolean equals(Object obj) {
        List<String> compName;
        // check the class
        if (obj instanceof Location) {
            compName = ((Location)obj).getName();
        } else {
            return false;
        }
        return name.equals(compName);
    }
    
    @Override
    public int hashCode() { return name.hashCode(); }
}


