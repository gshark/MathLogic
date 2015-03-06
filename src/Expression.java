/**
 * Created by GShark on 06.03.2015.
 */
public class Expression {
    boolean equals(Expression expression) throws Exception {
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
        throw new Exception("Something strange in expression");
    }

}
