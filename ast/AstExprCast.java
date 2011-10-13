package ast;

import java.util.*;

public class AstExprCast implements AstExpr
{
	public AstName		Type;
	public AstExpr		Expr;

	public AstExprCast(AstName type, AstExpr expr)
	{
		Type = type;
		Expr = expr;
	}
}
