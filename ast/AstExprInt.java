package ast;

public class AstExprInt implements AstExpr
{
	public int Value;

	public AstExprInt(int value)
	{
		Value = value;
	}

	public String toString()
	{
		return Integer.toString(Value);
	}
}
