package ast;

import java.util.*;

public class AstMethod
{
	public AstVars	Args;
	public AstFlags	Flags;
	public String	Name;
	public AstStm	Stm;
	public AstName	Type;

	public AstMethod(AstFlags flags, String name, AstVars args, AstName type, AstStm stm)
	{
		Args	= args;
		Flags	= flags;
		Name	= name;
		Type	= type;
		Stm		= stm;
	}
}
