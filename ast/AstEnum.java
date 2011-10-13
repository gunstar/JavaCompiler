package ast;

import java.util.*;

public class AstEnum
{
	public List<String>	Enums;
	public AstFlags		Flags;
	public String		Name;

	public AstEnum(AstFlags flags, String name)
	{
		Enums	= new ArrayList<String>();
		Flags	= flags;
		Name	= name;
	}
}
