package ast;

import java.util.*;

public class AstStmTry implements AstStm
{
	public AstStmBlock	TryBlock;
	public AstStmCatch	CatchBlock;		// == null if none.
	public AstStmBlock	FinallyBlock;	// == null if none.

	public AstStmTry(AstStmBlock tryBlock, AstStmCatch catchBlock, AstStmBlock finallyBlock)
	{
		TryBlock		= tryBlock;
		CatchBlock		= catchBlock;
		FinallyBlock	= finallyBlock;
	}
}
