package ast;

public class AstExprNameDef implements AstExpr
{
	public AstName Name;
	public AstName Type;
	public AstExpr Expr;		// == null if none.

	public AstExprNameDef(AstName name, AstName type, AstExpr expr)
	{
		Name = name;
		Type = type;
		Expr = expr;
	}

	public String toString()
	{
		String s = Type.toString() + " " + Name.toString();

		if(Expr != null)
			s += " = " + Expr.toString();

		return s;
	}
}
