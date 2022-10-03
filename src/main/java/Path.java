/* CISC/CMPE 422/835
 * Representation of paths
 */

import java.util.List;

public class Path {
    public List<Integer> path;
    public int length;

    public Path(List<Integer> p, int len) {
        this.path = p;
        this.length = len;
    }
    public String toString() {
        if (this.path == null)
            return "<null, inf>";
        else
            return "<" + this.path + ", " + this.length + ">";
    }
}
