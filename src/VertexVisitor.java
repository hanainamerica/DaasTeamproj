
public class VertexVisitor implements Visitor<Location> {
    @Override
    public void visit(Location obj) {
        System.out.println(obj.toString());
    }
}
