package ast;

public class AstVarDef
{
	public String	Name;
	public AstName	Type;
	public AstFlags	Flags;
	public AstExpr	Expr;	// == null if none.

	public AstVarDef(String name, AstName type, AstFlags flags, AstExpr expr)
	{
		Name	 = name;
		Type	= type;
		Flags	= flags;
		Expr	= expr;
	}
}
