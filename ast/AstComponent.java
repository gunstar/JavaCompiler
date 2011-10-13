package ast;

import java.util.*;

public class AstComponent
{
	public List<AstClass>	Classes;
	public List<AstEnum>	Enums;

	public AstComponent()
	{
		Classes	= new ArrayList<AstClass>();
		Enums	= new ArrayList<AstEnum>();
	}
}
