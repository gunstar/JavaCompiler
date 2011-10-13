package ast;

import java.util.*;

public class AstStmReturn implements AstStm
{
	public AstExpr	ExprOrNull;

	public AstStmReturn(AstExpr exprOrNull)
	{
		ExprOrNull = exprOrNull;
	}
}
