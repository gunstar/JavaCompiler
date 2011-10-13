package ast;

import java.util.*;

public class AstExprArrayAccess implements AstExpr
{
	public AstExpr	Left;
	public AstExpr	Right;

	public AstExprArrayAccess(AstExpr l, AstExpr r)
	{
		Left	= l;
		Right	= r;
	}
}
