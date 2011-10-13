package ast;

import java.util.*;

public class AstStmCase implements AstStm
{
	public AstExpr		Expr;
	public AstStmBlock	Block;

	public AstStmCase(AstExpr expr, AstStmBlock block)
	{
		Expr	= expr;
		Block	= block;
	}
}
