package ast;

public class AstStmExpr implements AstStm
{
	public AstExpr Expr;

	public AstStmExpr(AstExpr expr)
	{
		Expr = expr;
	}
}
