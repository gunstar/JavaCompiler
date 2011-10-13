package ast;

import java.util.*;

public class AstStmFor implements AstStm
{
	public AstExpr	E1;
	public AstExpr	E2;
	public AstExpr	E3;
	public AstStm	Stm;

	public AstStmFor(AstExpr e1, AstExpr e2, AstExpr e3, AstStm stm)
	{
		E1	= e1;
		E2	= e2;
		E3	= e3;
		Stm	= stm;
	}
}
