package java_from_ast;

import ast.*;
import java.io.*;
import java.util.*;

public class JavaFromAst
{
	private int		mExprLevel;
	private int		mTabs;
	private boolean	mTabsNow;
	private boolean	mUseEOL;
	private boolean	mWasExpr;
	private Writer	mWriter;
	private boolean	mWriterError;

	public JavaFromAst(Writer writer)
	{
		mWriter = writer;
	}

	public boolean print(AstFile ast)
	{
		mExprLevel		= 0;
		mTabs			= 0;
		mTabsNow		= true;
		mUseEOL			= false;
		mWasExpr		= false;
		mWriterError	= false;

		l("// This code is auto generated. Do not modify!");
		l();

		printPackage(ast.Package);
		printImports(ast.Imports);

		printEnums(ast.Enums);
		printClasses(ast.Classes);

		return ! mWriterError;
	}

/*******************************************************************/

	void printClass(AstClass c)
	{
	// Class header:

		l();

		if(c.Flags.Private)
			w("private ");
		else if(c.Flags.Public)
			w("public ");

		w(c.IsInterface ? "interface " : "class "); print(c.Name);

		if(c.Extends != null)
		{
			w(" extends ");

			print(c.Extends);
		}

		for(int i = 0; i < c.Implements.size(); i++)
		{
			if(i == 0)
				w(" implements ");
			else
				w(", ");

			w(c.Implements.get(i).toString());
		}

		l();
		l("{");

		mTabs++;

	// Class vars:

		ListIterator<AstVarDef> vi = c.VarDefs.listIterator();

		while(vi.hasNext())
			printVarDef(vi.next());

	// Class enums:

		for(int i = 0; i < c.Enums.size(); i++)
			printEnum(c.Enums.get(i));

	// Class methods:

		ListIterator<AstMethod> mi = c.Methods.listIterator();

		if(c.VarDefs.size() > 0 && c.Methods.size() > 0)
			l();

		for(int i = 0; mi.hasNext(); i++)
		{
			if(i > 0)
				l();

			printMethod(c, mi.next());
		}

	// Class footer:

		mTabs--;

		l("}");
	}

	void printClasses(List<AstClass> cs)
	{
		ListIterator<AstClass> li = cs.listIterator();

		while(li.hasNext())
			printClass(li.next());
	}

/*******************************************************************/

	void printEnum(AstEnum e)
	{
		l();

		if(e.Flags.Private)
			w("private ");
		else if(e.Flags.Public)
			w("public ");

		l("enum " + e.Name);
		bp();

		for(int i = 0; i < e.Enums.size(); i++)
		{
			w(e.Enums.get(i));

			l(i + 1 < e.Enums.size() ? "," : "");
		}

		bm();
	}

	void printEnums(List<AstEnum> es)
	{
		for(int i = 0; i < es.size(); i++)
			printEnum(es.get(i));
	}

/*******************************************************************/

	void printExpr(AstExpr expr)
	{
		//w("[EXPR BEGIN [" + expr.getClass().getName() + "]]");

		if(expr instanceof AstExprArrayAccess)	{ printExpr((AstExprArrayAccess)expr); return; }
		if(expr instanceof AstExprBinary)		{ printExpr((AstExprBinary)		expr); return; }
		if(expr instanceof AstExprCall)			{ printExpr((AstExprCall)		expr); return; }
		if(expr instanceof AstExprCast)			{ printExpr((AstExprCast)		expr); return; }
		if(expr instanceof AstExprChar)			{ printExpr((AstExprChar)		expr); return; }
		if(expr instanceof AstExprIf)			{ printExpr((AstExprIf)			expr); return; }
		if(expr instanceof AstExprInt)			{ printExpr((AstExprInt)		expr); return; }
		if(expr instanceof AstExprName)			{ printExpr((AstExprName)		expr); return; }
		if(expr instanceof AstExprNameDef)		{ printExpr((AstExprNameDef)	expr); return; }
		if(expr instanceof AstExprNew)			{ printExpr((AstExprNew)		expr); return; }
		if(expr instanceof AstExprString)		{ printExpr((AstExprString)		expr); return; }

		w("[EXPR " + expr.getClass().getName() + "]");
	}

	void printExpr(AstExprArrayAccess expr)
	{
		printExpr(expr.Left);

		w("[");

		printExpr(expr.Right);

		w("]");
	}

	void printExpr(AstExprBinary expr)
	{
		if(expr.Op != AstExprOp.Dot && ++mExprLevel > 1)
			w("(");

		if(expr.Left != null)
		{
			printExpr(expr.Left);

			if(expr.Op != AstExprOp.Dot && expr.Right != null)
				w(" ");
		}

		printOp(expr.Op);

		if(expr.Right != null)
		{
			if(expr.Op != AstExprOp.Dot && (expr.Left != null || expr.Op == AstExprOp.LNot))
				w(" ");

			printExpr(expr.Right);
		}

		if(expr.Op != AstExprOp.Dot && --mExprLevel > 0)
			w(")");
	}

	void printExpr(AstExprCall call)
	{
		printExpr(call.Call);
		w("(");

		ListIterator<AstExpr> li = call.Args.listIterator();

		for(int i = 0; li.hasNext(); i++)
		{
			if(i > 0)
				w(", ");

			printExpr(li.next());
		}

		w(")");
	}

	void printExpr(AstExprChar c)
	{
		w("'" + c.Value + "'");
	}

	void printExpr(AstExprCast c)
	{
		w("((");

		print(c.Type);

		w(") ");

		printExpr(c.Expr);

		w(")");
	}

	void printExpr(AstExprIf i)
	{
		if(++mExprLevel > 1)
			w("(");

		printExpr(i.Cond);

		w(" ? ");

		printExpr(i.TExpr);

		w(" : ");

		printExpr(i.FExpr);

		if(--mExprLevel > 0)
			w(")");
	}

	void printExpr(AstExprInt i)
	{
		w(i.toString());
	}

	void printExpr(AstExprName name)
	{
		w(name.Name.toString());
	}

	void printExpr(AstExprNameDef nameDef)
	{
		print(nameDef.Type);
		w(" ");
		print(nameDef.Name);

		if(nameDef.Expr != null)
		{
			w(" = ");

			printExpr(nameDef.Expr);
		}
	}

	void printExpr(AstExprNew n)
	{
		w("new ");

		print(n.Type);

	// ConstructorArgs:

		if(n.ConstructorArgs.size() > 0 || n.ArrayArgs.size() == 0)
		{
			w("(");

			for(int i = 0; i < n.ConstructorArgs.size(); i++)
			{
				if(i > 0)
					w(", ");

				printExpr(n.ConstructorArgs.get(i));
			}

			w(")");
		}

	// ArrayArgs:

		for(int i = 0; i < n.ArrayArgs.size(); i++)
		{
			w("[");

			printExpr(n.ArrayArgs.get(i));

			w("]");
		}
	}

	void printExpr(AstExprOp op)
	{
		printOp(op);
	}

	void printExpr(AstExprString s)
	{
		w("\"" + s.toString() + "\"");
	}

/*******************************************************************/

	void printFlags(AstFlags flags)
	{
		if(flags.Private)
			w("private ");

		if(flags.Public)
			w("public ");

		if(flags.Static)
			w("static ");
	}

/*******************************************************************/

	void printImports(List<AstName> ps)
	{
		if(ps.size() == 0)
			return;

		l();

		ListIterator<AstName> li = ps.listIterator();

		while(li.hasNext())
		{
			w("import ");

			print(li.next());

			l(";");
		}
	}

/*******************************************************************/

	void printMethod(AstClass c, AstMethod method)
	{
		printFlags(method.Flags);

		if(! c.isConstructor(method))
		{
			print(method.Type);

			w(" ");
		}

		w(method.Name + "(");

		for(int i = 0; i < method.Args.size(); i++)
		{
			AstVar v = method.Args.get(i);

			if(i > 0)
				w(", ");

			w(v.Type + " " + v.Name);
		}

		//public AstVars	Args;

		w(")");

		mUseEOL = false;

		if(method.Stm == null)
		{
			l(";");

			return;
		}

		l();

		printStm(method.Stm);
	}

/*******************************************************************/

	void printPackage(AstName name)
	{
		if(name == null)
			return;

		w("package ");

		print(name);

		l(";");
	}

/*******************************************************************/

	void printStm(AstStm stm)
	{
		if(stm instanceof AstStmExpr) { printStm((AstStmExpr) stm); return; }

		mWasExpr = false;

		if(stm instanceof AstStmBlock)		{ printStm((AstStmBlock)	stm); return; }
		if(stm instanceof AstStmBreak)		{ printStm((AstStmBreak)	stm); return; }
		if(stm instanceof AstStmContinue)	{ printStm((AstStmContinue)	stm); return; }
		if(stm instanceof AstStmFor)		{ printStm((AstStmFor)		stm); return; }
		if(stm instanceof AstStmIf)			{ printStm((AstStmIf)		stm); return; }
		if(stm instanceof AstStmReturn)		{ printStm((AstStmReturn)	stm); return; }
		if(stm instanceof AstStmSwitch)		{ printStm((AstStmSwitch)	stm); return; }
		if(stm instanceof AstStmTry)		{ printStm((AstStmTry)		stm); return; }
		if(stm instanceof AstStmWhile)		{ printStm((AstStmWhile)	stm); return; }

		l("[STM " + stm.getClass().getName() + "]");
	}

	void printStm(AstStmBlock block)
	{
		if(mUseEOL) l(); mUseEOL = false;

		l("{");
		mTabs++;

		ListIterator<AstStm> li = block.listIterator();

		for(int i = 0; li.hasNext(); i++)
			printStm(li.next());

		mTabs--;
		l("}");

		mUseEOL = true;
	}

	void printStm(AstStmBreak stm)
	{
		if(mUseEOL)
			l();

		l("break;");

		mUseEOL = true;
	}

	void printStm(AstStmCatch stm)
	{
		w("catch(");

		printVar(stm.Arg);

		l(")");

		mUseEOL = false;

		printStm(stm.Block);
	}

	void printStm(AstStmContinue stm)
	{
		if(mUseEOL)
			l();

		l("continue;");

		mUseEOL = true;
	}

	void printStm(AstStmExpr stm)
	{
		if(mUseEOL && ! mWasExpr)
			l();

		printExpr(stm.Expr);

		l(";");

		mUseEOL		= true;
		mWasExpr	= true;
	}

	void printStm(AstStmFor stm)
	{
		if(mUseEOL) l();

		w("for(");

		if(stm.E1 != null)
			printExpr(stm.E1);

		w(";");

		if(stm.E2 != null)
		{
			w(" ");

			printExpr(stm.E2);
		}

		w(";");

		if(stm.E3 != null)
		{
			w(" ");

			printExpr(stm.E3);
		}

		l(")");

		mUseEOL = false;

		if(! (stm.Stm instanceof AstStmBlock))
			w("\t");

		printStm(stm.Stm);

		mUseEOL = true;
	}

	void printStm(AstStmIf stm)
	{
	// EOL:

		if(mUseEOL) l();

	// if:

		mUseEOL = false;

		w("if("); printExpr(stm.Condition); l(")");

		if(! (stm.TrueStm instanceof AstStmBlock))
			w("\t");

		printStm(stm.TrueStm);

	// else:

		if(stm.FalseStm != null)
		{
			mUseEOL = false;

			l("else");

			if(! (stm.FalseStm instanceof AstStmBlock))
				w("\t");

			printStm(stm.FalseStm);
		}

	// done:

		mUseEOL = true;
	}

	void printStm(AstStmReturn stm)
	{
		if(mUseEOL)
			l();

		w("return");

		if(stm.ExprOrNull != null)
		{
			w(" ");

			printExpr(stm.ExprOrNull);
		}

		l(";");

		mUseEOL	= true;
	}

	void printStm(AstStmSwitch stm)
	{
	// EOL:

		if(mUseEOL) l();

	// try:

		w("switch(");

		printExpr(stm.Arg);

		l(")");
		bp();

	// Cases:

		for(int i = 0; i < stm.Cases.size(); i++)
		{
			AstStmCase c = stm.Cases.get(i);

			if(mUseEOL)
				l();

			w("case "); printExpr(c.Expr); l(":");

			mUseEOL = false;
			printStm(c.Block);
			mUseEOL = true;
		}

	// Default:

		if(stm.Default != null)
		{
			if(mUseEOL)
				l();

			l("default:");

			mUseEOL = false;
			printStm(stm.Default);
			mUseEOL = true;
		}

		bm();

		mUseEOL = true;
	}

	void printStm(AstStmTry stm)
	{
	// EOL:

		if(mUseEOL) l();

	// try:

		mUseEOL = false;

		l("try");

		printStm(stm.TryBlock);

	// catch(s):

		if(stm.CatchBlock != null)
			printStm(stm.CatchBlock);

	// finally:

		if(stm.FinallyBlock != null)
		{
			l("finally");

			mUseEOL = false;

			printStm(stm.FinallyBlock);
		}
	}

	void printStm(AstStmWhile stm)
	{
	// EOL:

		if(mUseEOL) l();

	// while:

		mUseEOL = false;

		w("while("); printExpr(stm.Cond); l(")");

		if(! (stm.Stm instanceof AstStmBlock))
			w("\t");

		printStm(stm.Stm);

	// done:

		mUseEOL = true;
	}

/*******************************************************************/

	void printOp(AstExprOp op)
	{
		switch(op)
		{
			case Add:			w("+");			return;
			case Add2:			w("++");		return;
			case Assign:		w("=");			return;
			case AssignAdd:		w("+=");		return;
			case AssignBAnd:	w("&=");		return;
			case AssignBOr:		w("|=");		return;
			case AssignBXor:	w("^=");		return;
			case AssignDiv:		w("/=");		return;
			case AssignMod:		w("%=");		return;
			case AssignMul:		w("*=");		return;
			case AssignShl:		w("<<=");		return;
			case AssignShr:		w(">>=");		return;
			case AssignShr2:	w(">>>=");		return;
			case AssignSub:		w("-=");		return;
			case BAnd:			w("&");			return;
			case BOr:			w("|");			return;
			case BNot:			w("~");			return;
			case BXor:			w("^");			return;
			case Colon:			w(":");			return;
			case Div:			w("/");			return;
			case Dot:			w(".");			return;
			case Eq:			w("==");		return;
			case Ge:			w(">=");		return;
			case Gt:			w(">");			return;
			case InstanceOf:	w("instanceof");return;
			case LAnd:			w("&&");		return;
			case LNot:			w("!");			return;
			case LOr:			w("||");		return;
			case Le:			w("<=");		return;
			case Lt:			w("<");			return;
			case Mod:			w("%");			return;
			case Mul:			w("*");			return;
			case Ne:			w("!=");		return;
			case New:			w("new");		return;
			case Null:			w("null");		return;
			case Question:		w("?");			return;
			case Shl:			w("<<");		return;
			case Shr:			w(">>");		return;
			case Shr2:			w(">>>");		return;
			case Sub:			w("-");			return;
			case Sub2:			w("--");		return;

			default: break;
		}

		w("[UNKNOWN AstExprOp]");
	}

	void printVar(AstVar var)
	{
		print(var.Type);

		w(" " + var.Name);
	}

	void printVarDef(AstVarDef varDef)
	{
		printFlags(varDef.Flags);
		print(varDef.Type);

		l(" " + varDef.Name + ";");
	}

/*******************************************************************/

	void print(AstName name)
	{
		w(name.toString());
	}

/*******************************************************************/

	void bp()
	{
		l("{");

		mTabs++;

		mUseEOL = false;
	}

	void bm()
	{
		mTabs--;

		l("}");

		mUseEOL = true;
	}

	void l()
	{
		w("\n");
	}

	void l(String s)
	{
		w(s + "\n");
	}

	void w(String s)
	{
		try
		{
			for(int i = 0; i < s.length(); i++)
			{
				if(mTabsNow)
				{
					for(int ti = 0; ti < mTabs; ti++)
						mWriter.write("\t");

					mTabsNow = false;
				}

				char c = s.charAt(i);

				mWriter.write(c);

				mTabsNow = (c == '\n');
			}
		}
		catch(IOException e)
		{
			mWriterError = true;
		}
	}
}
