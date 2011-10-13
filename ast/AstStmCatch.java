package ast;

import java.util.*;

public class AstStmCatch implements AstStm
{
	public AstVar		Arg;
	public AstStmBlock	Block;

	public AstStmCatch(AstVar arg, AstStmBlock block)
	{
		Arg		= arg;
		Block	= block;
	}
}
