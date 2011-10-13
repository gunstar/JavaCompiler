package ast;

import java.util.*;

public class AstName
{
	public int				ArrayDims;		// [] = 1, [][] = 2 etc. (used only for types).
	public List<AstName>	Generics;		// <...>
	public List<String>		Path;			// x.y.z

	public AstName()
	{
		ArrayDims	= 0;
		Generics	= new ArrayList<AstName>();
		Path		= new ArrayList<String>();
	}

	public boolean isEq(String s)
	{
		return toString().compareTo(s) == 0;
	}

	public boolean isEq(AstName other)
	{
		return toString().compareTo(other.toString()) == 0;
	}

	public String toString()
	{
		String s = "";

		for(int i = 0; i < Path.size(); i++)
			s += (i > 0 ? "." : "") + Path.get(i);

		if(Generics.size() > 0)
		{
			s += "<";

			for(int i = 0; i < Generics.size(); i++)
				s += (i > 0 ? "," : "") + Generics.get(i);

			s += ">";
		}

		for(int i = 0; i < ArrayDims; i++)
			s += "[]";

		return s;
	}
}
