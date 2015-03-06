/**
 * Created by GShark on 06.03.2015.
 */
public abstract class BinaryOperation extends Expression {
    Expression first, second;
    String name;

    public BinaryOperation(Expression first, Expression second, String name) {
        this.first = first;
        this.second = second;
        this.name = name;
    }

    public int hashCode() {
        return name.hashCode() ^ first.hashCode() ^ second.hashCode();
    }
}
