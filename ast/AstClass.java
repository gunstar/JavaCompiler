package ast;

import java.util.*;

public class AstClass extends AstComponent
{
	public AstName	Extends;
	public AstFlags	Flags;
	public AstName	Name;

	public boolean	IsInterface;

	public List<AstName>	Implements;
	public List<AstMethod>	Methods;
	public AstVarDefs		VarDefs;

	public AstClass(AstFlags flags, boolean isInterface)
	{
		Flags		= flags;

		IsInterface	= isInterface;

		Implements	= new ArrayList<AstName>();
		Methods		= new ArrayList<AstMethod>();
		VarDefs		= new AstVarDefs();
	}

	public boolean isConstructor(AstMethod method)
	{
		return Name.isEq(method.Name);
	}
}
