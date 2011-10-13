package ast;

import java.util.*;

public class AstStmWhile implements AstStm
{
	public AstExpr	Cond;
	public AstStm	Stm;

	public AstStmWhile(AstExpr cond, AstStm stm)
	{
		Cond	= cond;
		Stm		= stm;
	}
}
