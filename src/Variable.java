/**
 * Created by GShark on 06.03.2015.
 */
public class Variable extends Expression {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
