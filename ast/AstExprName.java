package ast;

public class AstExprName implements AstExpr
{
	public AstName Name;

	public AstExprName(AstName name)
	{
		Name = name;
	}

	public String toString()
	{
		return Name.toString();
	}
}
