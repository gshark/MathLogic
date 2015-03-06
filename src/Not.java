/**
 * Created by GShark on 06.03.2015.
 */
public class Not extends Expression {
    public Expression once;


    public Not(Expression once) {
        this.once = once;
    }

    public int hashCode() {
        return "!".hashCode() ^ once.hashCode();
    }
}
