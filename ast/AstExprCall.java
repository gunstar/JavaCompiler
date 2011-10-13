package ast;

import java.util.*;

public class AstExprCall implements AstExpr
{
	public List<AstExpr>	Args;
	public AstExpr			Call;

	public AstExprCall(AstExpr call)
	{
		Args = new ArrayList<AstExpr>();
		Call = call;
	}
}
