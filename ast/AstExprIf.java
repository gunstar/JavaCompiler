package ast;

import java.util.*;

public class AstExprIf implements AstExpr
{
	public AstExpr	Cond;
	public AstExpr	TExpr;
	public AstExpr	FExpr;

	public AstExprIf(AstExpr c, AstExpr t, AstExpr f)
	{
		Cond	= c;
		TExpr	= t;
		FExpr	= f;
	}
}
