package ast;

public class AstExprString implements AstExpr
{
	public String Value;

	public AstExprString(String value)
	{
		Value = value;
	}

	public String toString()
	{
		return Value;
	}
}
