/* CISC/CMPE 422/835
 * Representation of paths
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Path {
    public List<Integer> path = null;
    public int length = -1;

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
