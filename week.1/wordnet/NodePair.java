/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class NodePair {
    private int v;
    private int w;

    public NodePair(int v, int w) {
        this.v = v;
        this.w = w;
    }

    public int nodeV() {
        return v;
    }

    public int nodeW() {
        return w;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if ((obj.getClass() != this.getClass())) return false;
        NodePair that = (NodePair) obj;
        return (this.v == that.v && this.w == that.w) || (this.v == that.w && this.w == that.v);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(Math.min(v, w)) + Integer.hashCode(Math.max(v, w));
    }
}
