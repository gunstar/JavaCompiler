package ast;

public class AstVar
{
	public String	Name;
	public AstName	Type;

	public AstVar(String name, AstName type)
	{
		Name = name;
		Type = type;
	}
}
