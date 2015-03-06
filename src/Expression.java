/**
 * Created by GShark on 06.03.2015.
 */
public class Expression {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Expression) ) {
            return false;
        }
        Expression expression = (Expression) o;
        if (!this.getClass().equals(expression.getClass())) {
            return false;
        }
        if (this instanceof BinaryOperation) {
            BinaryOperation binThis = (BinaryOperation) this;
            BinaryOperation binExpr = (BinaryOperation) expression;
            return binThis.first.equals(binExpr.first) &&
                   binThis.second.equals(binExpr.second);
        } else if (this instanceof Not) {
            return ((Not) this).once.equals(((Not) expression).once);
        } else if (this instanceof Variable) {
            return ((Variable) this).getName().equals(((Variable) expression).getName());
        }
        return false;
    }

}
