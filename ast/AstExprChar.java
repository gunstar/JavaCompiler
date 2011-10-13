package ast;

public class AstExprChar implements AstExpr
{
	public String Value;

	public AstExprChar(String value)
	{
		Value = value;
	}

	public String toString()
	{
		return "'" + Value + "'";
	}
}
