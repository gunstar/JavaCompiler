package ast;

import java.util.*;

public class AstExprBinary implements AstExpr
{
	public AstExpr		Left;
	public AstExprOp	Op;
	public AstExpr		Right;

	public AstExprBinary(AstExpr l, AstExprOp op, AstExpr r)
	{
		Left	= l;
		Op		= op;
		Right	= r;
	}
}
