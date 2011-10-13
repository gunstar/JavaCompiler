package ast;

import java.util.*;

public class AstStmSwitch implements AstStm
{
	public	AstExpr				Arg;
	public	List<AstStmCase>	Cases;
	public	AstStmBlock			Default;	// == null if none.

	public AstStmSwitch()
	{
		Cases = new ArrayList<AstStmCase>();
	}
}
