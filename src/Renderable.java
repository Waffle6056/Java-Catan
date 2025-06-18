import RenderingStuff.Mesh;

import java.util.ArrayList;
import java.util.List;

public interface Renderable {
    public List<Mesh> toMesh();
}
interface Renderable2d {
    public List<Mesh> toMesh2d();
}
