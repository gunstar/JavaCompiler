package ast;

import java.util.*;

public class AstStmIf implements AstStm
{
	public AstExpr	Condition;
	public AstStm	TrueStm;
	public AstStm	FalseStm;		// == null if none.

	public AstStmIf(AstExpr c, AstStm t, AstStm f)
	{
		Condition	= c;
		TrueStm		= t;
		FalseStm	= f;
	}
}
