package ast;

import java.util.*;

public class AstExprNew implements AstExpr
{
	public List<AstExpr>	ArrayArgs;
	public List<AstExpr>	ConstructorArgs;
	public AstName			Type;

	public AstExprNew(AstName type)
	{
		ArrayArgs		= new ArrayList<AstExpr>();
		ConstructorArgs = new ArrayList<AstExpr>();
		Type			= type;
	}
}
