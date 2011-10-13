package ast_from_src;

import ast.*;
import errors.*;
import scanner.*;

import java.util.*;

public class AstFromSrc
{
	private Errors			mErrors;
	private scanner.Scanner	mScanner;

/*******************************************************************/

	private List<AstExprOp>	ExprOpList15;
	private List<AstExprOp>	ExprOpList8;
	private List<AstExprOp>	ExprOpList7;
	private List<AstExprOp>	ExprOpList6;
	private List<AstExprOp>	ExprOpList4;
	private List<AstExprOp>	ExprOpList2;

/*******************************************************************/

	public AstFromSrc()
	{
	// ExprOpList15:

		ExprOpList15 = new ArrayList<AstExprOp>();

		ExprOpList15.add(AstExprOp.Assign);
		ExprOpList15.add(AstExprOp.AssignAdd);
		ExprOpList15.add(AstExprOp.AssignBAnd);
		ExprOpList15.add(AstExprOp.AssignBOr);
		ExprOpList15.add(AstExprOp.AssignBXor);
		ExprOpList15.add(AstExprOp.AssignDiv);
		ExprOpList15.add(AstExprOp.AssignMod);
		ExprOpList15.add(AstExprOp.AssignMul);
		ExprOpList15.add(AstExprOp.AssignShl);
		ExprOpList15.add(AstExprOp.AssignShr);
		ExprOpList15.add(AstExprOp.AssignShr2);
		ExprOpList15.add(AstExprOp.AssignSub);

	// ExprOpList8:

		ExprOpList8 = new ArrayList<AstExprOp>();

		ExprOpList8.add(AstExprOp.Eq);
		ExprOpList8.add(AstExprOp.Ne);

	// ExprOpList7:

		ExprOpList7 = new ArrayList<AstExprOp>();

		ExprOpList7.add(AstExprOp.Ge);
		ExprOpList7.add(AstExprOp.Gt);
		ExprOpList7.add(AstExprOp.Le);
		ExprOpList7.add(AstExprOp.Lt);
		ExprOpList7.add(AstExprOp.InstanceOf);

	// ExprOpList6:

		ExprOpList6 = new ArrayList<AstExprOp>();

		ExprOpList6.add(AstExprOp.InstanceOf);
		ExprOpList6.add(AstExprOp.Shl);
		ExprOpList6.add(AstExprOp.Shr);
		ExprOpList6.add(AstExprOp.Shr2);

	// ExprOpList4:

		ExprOpList4 = new ArrayList<AstExprOp>();

		ExprOpList4.add(AstExprOp.Mul);
		ExprOpList4.add(AstExprOp.Div);
		ExprOpList4.add(AstExprOp.Mod);

	// ExprOpList2:

		ExprOpList2 = new ArrayList<AstExprOp>();

		ExprOpList2.add(AstExprOp.Add2);
		ExprOpList2.add(AstExprOp.Sub2);
		ExprOpList2.add(AstExprOp.Add);
		ExprOpList2.add(AstExprOp.Sub);
		ExprOpList2.add(AstExprOp.LNot);
		ExprOpList2.add(AstExprOp.BNot);
	}

	public AstFile parse(String srcFileName, String src, Errors errors)
	{
		mErrors	 = errors;
		mScanner = new scanner.Scanner(src);

		AstFile astFile = new AstFile(srcFileName);

		parseFile(astFile);

		if(! mErrors.isEmpty())
			mScanner.dump("Error");

		return astFile;
	}

/*******************************************************************/

	boolean parseFile(AstFile astFile)
	{
		if(! parsePackage(astFile))
			return false;

		if(! parseImports(astFile))
			return false;

		if(! parseComponents(astFile))
			return false;

		return true;
	}

/*******************************************************************/

	boolean parsePackage(AstFile astFile)
	{
		if(! mScanner.matchName("package"))
			return true;

		AstName name = parseName(false);

		if(name == null)
			return error("Name expected following 'package'");

		if(! mScanner.matchSymbol(";"))
			return error("';' expected following package name");

		astFile.Package = name;

		return true;
	}

/*******************************************************************/

	boolean parseImports(AstFile astFile)
	{
		while(mScanner.matchSymbol("import"))
		{
			AstName name = parseName(true);

			if(name == null)
				return error("Name expected following 'import'");

			if(! mScanner.matchSymbol(";"))
				return error("';' expected following import name");

			astFile.Imports.add(name);
		}

		return true;
	}

/*******************************************************************/

	boolean parseComponents(AstComponent component)
	{
		while(! mScanner.matchEos())
		{
			AstFlags flags = parseFlags();

			if(mScanner.matchName("class"))
			{
				if(! parseClass(component, flags, false))
					return false;

				continue;
			}

			if(mScanner.matchName("enum"))
			{
				AstEnum en = parseEnum(flags);

				if(en == null)
					return false;

				component.Enums.add(en);

				continue;
			}

			if(mScanner.matchName("interface"))
			{
				if(! parseClass(component, flags, true))
					return false;

				continue;
			}

			return error("'class'/'enum'/'interface' expected");
		}

		return true;
	}

/*******************************************************************/

	AstFlags parseFlags()
	{
		AstFlags flags = new AstFlags();

		while(true)
		{
			if(mScanner.matchName("private"))
			{
				flags.Private = true;

				continue;
			}

			if(mScanner.matchName("public"))
			{
				flags.Public = true;

				continue;
			}

			if(mScanner.matchName("static"))
			{
				flags.Static = true;

				continue;
			}

			return flags;
		}
	}

/*******************************************************************/

	boolean parseClass(AstComponent component, AstFlags flags, boolean isInterface)
	{
		AstClass cl = new AstClass(flags, isInterface);

		if(! parseClassType(cl))
			return false;

		if(! parseClassBody(cl))
			return false;

		component.Classes.add(cl);

		return true;
	}

	boolean parseClassType(AstClass cl)
	{
		cl.Name = parseName(false);

		if(cl.Name == null)
			return error("Class name expected");

		if(mScanner.matchSymbol("extends"))
		{
			cl.Extends = parseName(false);

			if(cl.Extends == null)
				return error("Name expected following 'extends'");
		}

		if(mScanner.matchSymbol("implements"))
		{
			AstName name = parseName(false);

			if(name == null)
				return error("Name expected following 'implements'");

			cl.Implements.add(name);
		}

		return true;
	}

	boolean parseClassBody(AstClass cl)
	{
		if(! mScanner.matchSymbol("{"))
			return error("'{' expected");

		while(! mScanner.matchSymbol("}"))
			if(! parseClassDef(cl))
				return false;

		return true;
	}

	boolean parseClassDef(AstClass cl)
	{
	// Flags:

		AstFlags flags = parseFlags();

	// Type:

		AstName type = parseName(false);

		if(type == null)
			return error("Type expected");

	// 'enum'?

		if(type.isEq("enum"))
		{
			AstEnum en = parseEnum(flags);

			if(en == null)
				return false;

			cl.Enums.add(en);

			return true;
		}

	// If constructor then name = type.toString():

		String name;

		if(type.isEq(cl.Name))
			name = type.toString();
		else
		{
			name = mScanner.readName();

			if(name != null)
			{
			// name;

				if(mScanner.matchSymbol(";"))
				{
					AstVarDef varDef = new AstVarDef(name, type, flags, null);

					cl.VarDefs.add(varDef);

					return true;
				}

			// name = ...;

				if(mScanner.matchSymbol("="))
				{
					AstExpr expr = parseExpr();

					if(expr == null)
						return false;

					if(! mScanner.matchSymbol(";"))
						return error("';' expected following class variable initialization expression");

					AstVarDef varDef = new AstVarDef(name, type, flags, null);

					cl.VarDefs.add(varDef);

					return true;
				}
			}
		}

	// Args:

		AstVars args = parseMethodArgs("method");

		if(args == null)
			return false;

	// If interface then ';' expected instead of body:

		AstStmBlock stm = null;

		if(cl.IsInterface)
		{
			if(! mScanner.matchSymbol(";"))
				return error("';' expected when defining interface method");
		}
		else
		{
			stm = parseStmBlock();

			if(stm == null)
				return false;
		}

	// Method:

		AstMethod method = new AstMethod(flags, name, args, type, stm);

		cl.Methods.add(method);

		return true;
	}

/*******************************************************************/

	AstEnum parseEnum(AstFlags flags)
	{
		String name = mScanner.readName();

		if(name == null)
			return (AstEnum) errorNull("Name expected following 'enum'");

		AstEnum en = new AstEnum(flags, name);

		if(! mScanner.matchSymbol("{"))
			return (AstEnum) errorNull("'{' expected following enum name");

		for(int i = 0; ! mScanner.matchSymbol("}"); i++)
		{
			if(i > 0 && ! mScanner.matchSymbol(","))
				return (AstEnum) errorNull("',' expected following enum");

			name = mScanner.readName();

			if(name == null)
				return (AstEnum) errorNull("Enum or '}' expected");

			en.Enums.add(name);
		}

		return en;
	}

/*******************************************************************/

	AstExpr parseExpr()
	{
		return parseExprNameDef();
	}

	// Type x = ...

	AstExpr parseExprNameDef()
	{
		ScannerMark mark = mScanner.mark();

	// Type?

		AstName type = parseName(false);

		if(type == null || type.isEq("new"))
		{
			mScanner.markUndo(mark);

			return parseExpr15();
		}

	// Name?

		AstName name = parseName(false);

		if(name == null || name.isEq("instanceof"))
		{
			mScanner.markUndo(mark);

			return parseExpr15();
		}

	// ';'?

		if(mScanner.peekSymbol(";"))
			return new AstExprNameDef(name, type, null);

	// '='?

		if(! parseExprOp(AstExprOp.Assign))
			return (AstExpr) errorNull("';' or '=' expected following variable definition");

	// Expr:

		AstExpr expr = parseExpr15();

		if(expr == null)
			return null;

		return new AstExprNameDef(name, type, expr);
	}

	/*
		===========================
		15 - RIGHT TO LEFT:
		===========================
		=
		+=	-=	*=	/=	%=
		&=	^=	|=
		<<=	>>=	>>>=
	*/

	AstExpr parseExpr15()
	{
	// Left:

		AstExpr l = parseExpr14();

		if(l == null)
			return null;

	// Operator?

		for(int i = 0; i < ExprOpList15.size(); i++)
		{
			AstExprOp op = ExprOpList15.get(i);

			if(! parseExprOp(op))
				continue;

			AstExpr r = parseExpr14();

			if(r == null)
				return null;

			l = new AstExprBinary(l, op, r);

			break;
		}

		return l;
	}

	/*
		===========================
		14 - RIGHT TO LEFT:
		===========================
		?: (conditional)
	*/

	AstExpr parseExpr14()
	{
		AstExpr ce = parseExpr13();

		if(ce == null)
			return null;

		if(! mScanner.matchSymbol("?"))
			return ce;

		AstExpr cl = parseExpr14();

		if(cl == null)
			return null;

		if(! mScanner.matchSymbol(":"))
			return (AstExpr) errorNull("':' expected in ?: expression");

		AstExpr cr = parseExpr14();

		if(cr == null)
			return null;

		return new AstExprIf(ce, cl, cr);
	}

	/*
		===========================
		13 - LEFT TO RIGHT:
		===========================
		|| (logical or)
	*/

	AstExpr parseExpr13()
	{
	// Left:

		AstExpr l = parseExpr12();

		if(l == null)
			return null;

	// Operator?

		while(parseExprOp(AstExprOp.LOr))
		{
			AstExpr r = parseExpr12();

			if(r == null)
				return null;

			l = new AstExprBinary(l, AstExprOp.LOr, r);
		}

		return l;
	}

	/*
		===========================
		12 - LEFT TO RIGHT:
		===========================
		&& (logical and)
	*/

		AstExpr parseExpr12()
		{
			AstExpr l = parseExpr11();

			if(l == null)
				return null;

			while(parseExprOp(AstExprOp.LAnd))
			{
				AstExpr r = parseExpr11();

				if(r == null)
					return null;

				l = new AstExprBinary(l, AstExprOp.LAnd, r);
			}

			return l;
		}

	/*
		===========================
		11 - LEFT TO RIGHT:
		===========================
		| (bitwise or)
	*/

		AstExpr parseExpr11()
		{
			AstExpr l = parseExpr10();

			if(l == null)
				return null;

			while(parseExprOp(AstExprOp.BOr))
			{
				AstExpr r = parseExpr10();

				if(r == null)
					return null;

				l = new AstExprBinary(l, AstExprOp.BOr, r);
			}

			return l;
		}

	/*
		===========================
		10 - LEFT TO RIGHT:
		===========================
		^	(bitwise xor)
	*/

		AstExpr parseExpr10()
		{
			AstExpr l = parseExpr9();

			if(l == null)
				return null;

			while(parseExprOp(AstExprOp.BXor))
			{
				AstExpr r = parseExpr9();

				if(r == null)
					return null;

				l = new AstExprBinary(l, AstExprOp.BXor, r);
			}

			return l;
		}

	/*
		===========================
		9 - LEFT TO RIGHT:
		===========================
		&	(bitwise and)
	*/

		AstExpr parseExpr9()
		{
			AstExpr l = parseExpr8();

			if(l == null)
				return null;

			while(parseExprOp(AstExprOp.BAnd))
			{
				AstExpr r = parseExpr8();

				if(r == null)
					return null;

				l = new AstExprBinary(l, AstExprOp.BAnd, r);
			}

			return l;
		}

	/*
		===========================
		8 - LEFT TO RIGHT:
		===========================
		==
		!=
	*/

		AstExpr parseExpr8()
		{
			AstExpr l = parseExpr7();

			if(l == null)
				return null;

			for(int i = 0; i < ExprOpList8.size(); i++)
			{
				AstExprOp op = ExprOpList8.get(i);

				if(parseExprOp(op))
				{
					AstExpr r = parseExpr7();

					if(r == null)
						return null;

					l = new AstExprBinary(l, op, r);
					i = -1;
				}
			}

			return l;
		}

	/*
		===========================
		7 - LEFT TO RIGHT:
		===========================
		<	<=	>	>=
		instanceof
	*/

		AstExpr parseExpr7()
		{
			AstExpr l = parseExpr6();

			if(l == null)
				return null;

			for(int i = 0; i < ExprOpList7.size(); i++)
			{
				AstExprOp op = ExprOpList7.get(i);

				if(parseExprOp(op))
				{
					AstExpr r = parseExpr6();

					if(r == null)
						return null;

					l = new AstExprBinary(l, op, r);
					i = -1;
				}
			}

			return l;
		}

	/*
		===========================
		6 - LEFT TO RIGHT:
		===========================
		<<	>>	>>> instanceof
	*/

		AstExpr parseExpr6()
		{
			AstExpr l = parseExpr5();

			if(l == null)
				return null;

			for(int i = 0; i < ExprOpList6.size(); i++)
			{
				AstExprOp op = ExprOpList6.get(i);

				if(parseExprOp(op))
				{
					AstExpr r = parseExpr5();

					if(r == null)
						return null;

					l = new AstExprBinary(l, op, r);
					i = -1;
				}
			}

			return l;
		}

	/*
		===========================
		5 - LEFT TO RIGHT:
		===========================
		+	-
	*/

		AstExpr parseExpr5()
		{
			AstExpr l = parseExpr4();

			if(l == null)
				return null;

			while(true)
			{
				AstExprOp op = null;

				if(parseExprOp(AstExprOp.Add))
					op = AstExprOp.Add;
				else if(parseExprOp(AstExprOp.Sub))
					op = AstExprOp.Sub;
				else
					return l;

				AstExpr r = parseExpr4();

				if(r == null)
					return null;

				l = new AstExprBinary(l, op, r);
			}
		}

	/*
		===========================
		4 - LEFT TO RIGHT:
		===========================
		*	/	%
	*/

		AstExpr parseExpr4()
		{
			AstExpr l = parseExpr3();

			if(l == null)
				return null;

			for(int i = 0; i < ExprOpList4.size(); i++)
			{
				AstExprOp op = ExprOpList4.get(i);

				if(parseExprOp(op))
				{
					AstExpr r = parseExpr3();

					if(r == null)
						return null;

					l = new AstExprBinary(l, op, r);
					i = -1;
				}
			}

			return l;
		}

	/*
		===========================
		3 - RIGHT TO LEFT:
		===========================
		()	(cast)
		new	(object creation)
	*/

	AstExpr parseExpr3()
	{
	// cast?

		ScannerMark mark = mScanner.mark();

		if(mScanner.matchSymbol("("))
		{
			AstName type = parseName(false);

			if(type == null || ! mScanner.matchSymbol(")"))
			{
				mScanner.markUndo(mark);

				return parseExpr2();
			}

			AstExpr expr = parseExpr3();

			if(expr == null)
				return null;

			return new AstExprCast(type, expr);
		}

	// new?

		if(mScanner.matchName("new"))
		{
		// Type:

			AstName type = parseName(false);

			if(type == null)
				return (AstExpr) errorNull("Type name expected following 'new'");

			AstExprNew exprNew = null;

		// ConstructorArgs:

			if(mScanner.matchSymbol("("))
			{
				if(exprNew == null)
					exprNew = new AstExprNew(type);

				for(int i = 0; ! mScanner.matchSymbol(")"); i++)
				{
					if(i > 0 && ! mScanner.matchSymbol(","))
						return (AstExpr) errorNull("',' expected following argument " + (1 + i) + " in 'new' expression");

					AstExpr expr = parseExpr();

					if(expr == null)
						return null;

					exprNew.ConstructorArgs.add(expr);
				}
			}

		// Array args:

			while(mScanner.matchSymbol("["))
			{
				if(exprNew == null)
					exprNew = new AstExprNew(type);

				AstExpr expr = parseExpr();

				if(expr == null)
					return null;

				if(! mScanner.matchSymbol("]"))
					return (AstExpr) errorNull("']' expected following '[' expression");

				exprNew.ArrayArgs.add(expr);
			}

		// Check:

			if(exprNew == null)
				return (AstExpr) errorNull("'(' or '[' expected following 'new' expression type");

			return exprNew;
		}

		return parseExpr2();
	}

	/*
		===========================
		2 - RIGHT TO LEFT:
		===========================
		++	(pre-increment)
		--	(pre-decrement)
		+	(unary +)
		-	(unary -)
		!	(logical not)
		~	(binary not)
	*/

	AstExpr parseExpr2()
	{
		for(int i = 0; i < ExprOpList2.size(); i++)
		{
			AstExprOp op = ExprOpList2.get(i);

			if(parseExprOp(op))
			{
				AstExpr e = parseExpr2();

				if(e == null)
					return null;

				return new AstExprBinary(null, op, e);
			}
		}

		return parseExpr1();
	}

	/*
		===========================
		1 - LEFT TO RIGHT:
		===========================
		[]	(access array element)
		.	(access object member)
		()	(invoke a method)
		++	(post-increment)
		--	(post-decrement)
	*/

	AstExpr parseExpr1()
	{
	// Expr:

		AstExpr expr = parseExpr0();

		if(expr == null)
			return null;

	// ++/--

		AstExprOp op = null;

		if(parseExprOp(AstExprOp.Add2))
			expr = new AstExprBinary(expr, AstExprOp.Add2, null);
		else if(parseExprOp(AstExprOp.Sub2))
			expr = new AstExprBinary(expr, AstExprOp.Sub2, null);

	// [] () .

		while(true)
		{
		// . (access element):

			if(mScanner.matchSymbol("."))
			{
				AstName name = parseName(false);

				if(name == null)
					return (AstExpr) errorNull("Name expected following '.'");

				expr = new AstExprBinary(expr, AstExprOp.Dot, new AstExprName(name));

				continue;
			}

		// [] (access array element):

			if(mScanner.matchSymbol("["))
			{
				AstExpr r = parseExpr();

				if(r == null)
					return null;

				if(! mScanner.matchSymbol("]"))
					return (AstExpr) errorNull("']' expected following '[' expressions");

				expr = new AstExprArrayAccess(expr, r);

				continue;
			}

		// () (invoke a method):

			if(mScanner.matchSymbol("("))
			{
				AstExprCall call = new AstExprCall(expr);

				for(int i = 0; ! mScanner.matchSymbol(")"); i++)
				{
					if(i > 0 && ! mScanner.matchSymbol(","))
						return (AstExpr) errorNull("',' expected following argument" + (1 + i));

					expr = parseExpr();

					if(expr == null)
						return null;

					call.Args.add(expr);
				}

				expr = call;

				continue;
			}

			return expr;
		}
	}

	/*
		===========================
		0 - Values
		===========================
		int
		name
		string
		( expr )
	*/

	AstExpr parseExpr0()
	{
	// '(':

		if(mScanner.matchSymbol("("))
		{
			AstExpr expr = parseExpr();

			if(expr == null)
				return null;

			if(! mScanner.matchSymbol(")"))
				return (AstExpr) errorNull("')' expected following expression");

			return expr;
		}

	// Char:

		String c = mScanner.readChar();

		if(c != null)
			return new AstExprChar(c);

	// Int:

		Integer io = mScanner.readInt();

		if(io != null)
			return new AstExprInt(io);

	// Name:

		AstName name = parseName(false);

		if(name != null)
			return new AstExprName(name);

	// String:

		String s = mScanner.readString();

		if(s != null)
			return new AstExprString(s);

	// Unknown:

		return (AstExpr) errorNull("Unknown expression");
	}

/*******************************************************************/

boolean parseExprOp(AstExprOp test)
{
	ScannerMark mark = mScanner.mark();

	AstExprOp op = parseExprOp();

	if(test == op)
		return true;

	mScanner.markUndo(mark);

	return false;
}

AstExprOp parseExprOp()
{
// 4:

	if(mScanner.matchSymbol(">>>="))
		return AstExprOp.AssignShr2;

// 3:

	if(mScanner.matchSymbol("<<="))
		return AstExprOp.AssignShl;

	if(mScanner.matchSymbol(">>="))
		return AstExprOp.AssignShr;

	if(mScanner.matchSymbol(">>>"))
		return AstExprOp.Shr2;

// 2:

	if(mScanner.matchSymbol("+="))
		return AstExprOp.AssignAdd;

	if(mScanner.matchSymbol("&="))
		return AstExprOp.AssignBAnd;

	if(mScanner.matchSymbol("|="))
		return AstExprOp.AssignBOr;

	if(mScanner.matchSymbol("^="))
		return AstExprOp.AssignBXor;

	if(mScanner.matchSymbol("/="))
		return AstExprOp.AssignDiv;

	if(mScanner.matchSymbol("%="))
		return AstExprOp.AssignMod;

	if(mScanner.matchSymbol("*="))
		return AstExprOp.AssignMul;

	if(mScanner.matchSymbol("-="))
		return AstExprOp.AssignSub;

	if(mScanner.matchSymbol("++"))
		return AstExprOp.Add2;

	if(mScanner.matchSymbol("=="))
		return AstExprOp.Eq;

	if(mScanner.matchSymbol(">="))
		return AstExprOp.Ge;

	if(mScanner.matchSymbol("<="))
		return AstExprOp.Le;

	if(mScanner.matchSymbol("&&"))
		return AstExprOp.LAnd;

	if(mScanner.matchSymbol("||"))
		return AstExprOp.LOr;

	if(mScanner.matchSymbol("!="))
		return AstExprOp.Ne;

	if(mScanner.matchSymbol("<<"))
		return AstExprOp.Shl;

	if(mScanner.matchSymbol(">>"))
		return AstExprOp.Shr;

	if(mScanner.matchSymbol("--"))
		return AstExprOp.Sub2;

// 1:

	if(mScanner.matchSymbol("+"))
		return AstExprOp.Add;

	if(mScanner.matchSymbol("="))
		return AstExprOp.Assign;

	if(mScanner.matchSymbol("&"))
		return AstExprOp.BAnd;

	if(mScanner.matchSymbol("|"))
		return AstExprOp.BOr;

	if(mScanner.matchSymbol("~"))
		return AstExprOp.BNot;

	if(mScanner.matchSymbol("^"))
		return AstExprOp.BXor;

	if(mScanner.matchSymbol(":"))
		return AstExprOp.Colon;

	if(mScanner.matchSymbol("/"))
		return AstExprOp.Div;

	if(mScanner.matchSymbol(">"))
		return AstExprOp.Gt;

	if(mScanner.matchName("instanceof"))
		return AstExprOp.InstanceOf;

	if(mScanner.matchSymbol("!"))
		return AstExprOp.LNot;

	if(mScanner.matchSymbol("<"))
		return AstExprOp.Lt;

	if(mScanner.matchSymbol("%"))
		return AstExprOp.Mod;

	if(mScanner.matchSymbol("*"))
		return AstExprOp.Mul;

	if(mScanner.matchName("new"))
		return AstExprOp.New;

	if(mScanner.matchName("null"))
		return AstExprOp.Null;

	if(mScanner.matchSymbol("?"))
		return AstExprOp.Question;

	if(mScanner.matchSymbol("-"))
		return AstExprOp.Sub;

	return null;
}

/*******************************************************************/

	AstVars parseMethodArgs(String methodType)
	{
		if(! mScanner.matchSymbol("("))
			return (AstVars) errorNull("'(' expected for " + methodType + " definition");

		AstVars args = new AstVars();

		for(int i = 0; ! mScanner.matchSymbol(")"); i++)
		{
			if(i > 0 && ! mScanner.matchSymbol(","))
				return (AstVars) errorNull("',' expected");

			AstName type = parseName(false);

			if(type == null)
				return (AstVars) errorNull("Argument type expected");

			String name = mScanner.readName();

			if(name == null)
				return (AstVars) errorNull("Argument name expected following argument type");

			args.add(new AstVar(name, type));
		}

		return args;
	}

/*******************************************************************/

	AstStm parseStm()
	{
		if(mScanner.peekSymbol("{"))
			return parseStmBlock();

		if(mScanner.peekKeyword("break"))
			return parseStmBreak();

		if(mScanner.peekKeyword("continue"))
			return parseStmContinue();

		if(mScanner.peekKeyword("for"))
			return parseStmFor();

		if(mScanner.peekKeyword("if"))
			return parseStmIf();

		if(mScanner.peekKeyword("return"))
			return parseStmReturn();

		if(mScanner.peekKeyword("switch"))
			return parseStmSwitch();

		if(mScanner.peekKeyword("try"))
			return parseStmTry();

		if(mScanner.peekKeyword("while"))
			return parseStmWhile();

		return parseStmExpr();
	}

	AstStmBlock parseStmBlock()
	{
		if(! mScanner.matchSymbol("{"))
			return (AstStmBlock) errorNull("'{' expected for statement block");

		AstStmBlock block = new AstStmBlock();

		while(! mScanner.matchSymbol("}"))
		{
			AstStm stm = parseStm();

			if(stm == null)
				return null;

			block.add(stm);
		}

		return block;
	}

	AstStmBreak parseStmBreak()
	{
		if(! mScanner.matchName("break"))
			return (AstStmBreak) errorNull("'break' expected");

		if(! mScanner.matchSymbol(";"))
			return (AstStmBreak) errorNull("';' expected following 'break'");

		return new AstStmBreak();
	}

	AstStmContinue parseStmContinue()
	{
		if(! mScanner.matchName("continue"))
			return (AstStmContinue) errorNull("'continue' expected");

		if(! mScanner.matchSymbol(";"))
			return (AstStmContinue) errorNull("';' expected following 'continue'");

		return new AstStmContinue();
	}

	AstStmExpr parseStmExpr()
	{
		AstExpr expr = parseExpr();

		if(expr == null)
			return null;

		if(! mScanner.matchSymbol(";"))
			return (AstStmExpr) errorNull("';' expected following expression");

		return new AstStmExpr(expr);
	}

	AstStmFor parseStmFor()
	{
	// for(

		if(! mScanner.matchName("for"))
			return (AstStmFor) errorNull("'for' expected");

		if(! mScanner.matchSymbol("("))
			return (AstStmFor) errorNull("'(' expected following 'for'");

	// e1:

		AstExpr e1 = null;

		if(! mScanner.matchSymbol(";"))
		{
			e1 = parseExpr();

			if(e1 == null)
				return (AstStmFor) errorNull("Expression expected following 'for('");

			if(! mScanner.matchSymbol(";"))
				return (AstStmFor) errorNull("';' expected following first 'for' expression");
		}

	// e2:

		AstExpr e2 = parseExpr();

		if(e2 == null)
			return (AstStmFor) errorNull("Expression expected following ';'");

		if(! mScanner.matchSymbol(";"))
			return (AstStmFor) errorNull("';' expected following second 'for' expression");

	// e3:

		AstExpr e3 = parseExpr();

		if(e3 == null)
			return (AstStmFor) errorNull("Expression expected following second ';'");

	// )

		if(! mScanner.matchSymbol(")"))
			return (AstStmFor) errorNull("')' expected in for statement");

	// stm:

		AstStm stm = parseStm();

		if(stm == null)
			return null;

	// Done:

		return new AstStmFor(e1, e2, e3, stm);
	}

	AstStmIf parseStmIf()
	{
	// Condition:

		if(! mScanner.matchName("if"))
			return (AstStmIf) errorNull("'if' expected");

		if(! mScanner.matchSymbol("("))
			return (AstStmIf) errorNull("'(' expected");

		AstExpr cond = parseExpr();

		if(cond == null)
			return null;

		if(! mScanner.matchSymbol(")"))
			return (AstStmIf) errorNull("')' expected");

	// StmTrue:

		AstStm stmIfTrue = parseStm();

		if(stmIfTrue == null)
			return null;

	// StmFalse:

		AstStm stmIfFalse = null;

		if(mScanner.matchName("else"))
		{
			stmIfFalse = parseStm();

			if(stmIfFalse == null)
				return null;
		}
		else
			stmIfFalse = null;		// Only here to test compiler compiling this .java file.

	// Result:

		return new AstStmIf(cond, stmIfTrue, stmIfFalse);
	}

	AstStmReturn parseStmReturn()
	{
		if(! mScanner.matchName("return"))
			return (AstStmReturn) errorNull("'return' expected");

		if(mScanner.matchSymbol(";"))
			return new AstStmReturn(null);

		AstExpr expr = parseExpr();

		if(expr == null)
			return null;

		if(! mScanner.matchSymbol(";"))
			return (AstStmReturn) errorNull("';' expected");

		return new AstStmReturn(expr);
	}

	AstStmSwitch parseStmSwitch()
	{
	// 'switch'

		if(! mScanner.matchName("switch"))
			return (AstStmSwitch) errorNull("'switch' expected");

	// '('

		if(! mScanner.matchSymbol("("))
			return (AstStmSwitch) errorNull("'(' expected following 'switch'");

	// expr:

		AstStmSwitch sw = new AstStmSwitch();

		sw.Arg = parseExpr();

		if(sw.Arg == null)
			return null;

	// ')'

		if(! mScanner.matchSymbol(")"))
			return (AstStmSwitch) errorNull("')' expected following 'switch' argument");

	// '{'

		if(! mScanner.matchSymbol("{"))
			return (AstStmSwitch) errorNull("'{' expected following 'switch'");

	// cases:

		while(! mScanner.matchSymbol("}"))
		{
			if(mScanner.matchName("case"))
			{
				AstExpr expr = parseExpr();

				if(expr == null)
					return null;

				if(! mScanner.matchSymbol(":"))
					return (AstStmSwitch) errorNull("':' expected in 'case' statement");

				AstStmBlock block = new AstStmBlock();

				while(! mScanner.peekKeyword("case") && ! mScanner.peekKeyword("default") && ! mScanner.peekSymbol("}"))
				{
					AstStm stm = parseStm();

					if(stm == null)
						return null;

					block.add(stm);
				}

				AstStmCase ca = new AstStmCase(expr, block);

				sw.Cases.add(ca);

				continue;
			}

			if(mScanner.matchName("default"))
			{
				if(sw.Default != null)
					return (AstStmSwitch) errorNull("'switch' already has a 'default' case");

				if(! mScanner.matchSymbol(":"))
					return (AstStmSwitch) errorNull("':' expected in 'case' statement");

				AstStmBlock block = new AstStmBlock();

				while(! mScanner.peekKeyword("case") && ! mScanner.peekKeyword("default") && ! mScanner.peekSymbol("}"))
				{
					AstStm stm = parseStm();

					if(stm == null)
						return null;

					block.add(stm);
				}

				sw.Default = block;

				continue;
			}

			return (AstStmSwitch) errorNull("'case', 'default' or '}' expected in 'switch' statement");
		}

		return sw;
	}

	AstStmTry parseStmTry()
	{
	// 'try'

		if(! mScanner.matchName("try"))
			return (AstStmTry) errorNull("'try' expected");

		AstStmBlock tryBlock = parseStmBlock();

		if(tryBlock == null)
			return null;

	// 'catch'

		AstStmCatch catchStm = null;

		if(mScanner.matchName("catch"))
		{
			AstVars args = parseMethodArgs("'catch'");

			if(args == null)
				return null;

			if(args.size() != 1)
				return (AstStmTry) errorNull("'catch' expects a single argument");

			AstStmBlock block = parseStmBlock();

			if(block == null)
				return null;

			catchStm = new AstStmCatch(args.get(0), block);
		}

	// 'finally'

		AstStmBlock finalBlock = null;

		if(mScanner.matchName("finally"))
		{
			finalBlock = parseStmBlock();

			if(finalBlock == null)
				return null;
		}

	// Done:

		return new AstStmTry(tryBlock, catchStm, finalBlock);
	}

	AstStmWhile parseStmWhile()
	{
	// Condition:

		if(! mScanner.matchName("while"))
			return (AstStmWhile) errorNull("'while' expected");

		if(! mScanner.matchSymbol("("))
			return (AstStmWhile) errorNull("'(' expected");

		AstExpr cond = parseExpr();

		if(cond == null)
			return null;

		if(! mScanner.matchSymbol(")"))
			return (AstStmWhile) errorNull("')' expected");

	// Stm:

		AstStm stm = parseStm();

		if(stm == null)
			return null;

		return new AstStmWhile(cond, stm);
	}

/*******************************************************************/

	AstName parseName(boolean canHaveStarAsLastName)
	{
	// Check first name:

		String s = mScanner.readName();

		if(s == null)
			return null;

	// Make name:

		AstName name = new AstName();

		name.Path.add(s);

	// Additional "." names:

		while(mScanner.matchSymbol("."))
		{
			s = mScanner.readName();

			if(s != null)
			{
				name.Path.add(s);

				continue;
			}

			if(canHaveStarAsLastName && mScanner.matchSymbol("*"))
			{
				name.Path.add("*");

				return name;
			}

			return (AstName) errorNull("Name expected following '.'");
		}

	// Generic?

		ScannerMark mark = mScanner.mark();

		if(mScanner.matchSymbol("<"))
		{
			for(int ni = 0; true; ni++)
			{
				AstName g = parseName(canHaveStarAsLastName);

				if(g == null)
					break;

				name.Generics.add(g);

				if(mScanner.matchSymbol(">"))
					return name;

				if(! mScanner.matchSymbol(","))
					if(ni == 0)
						break;
					else
						return (AstName) errorNull("',' expected in generic name");
			}

		// Nope - no generic:

			mScanner.markUndo(mark);

			name.Generics.clear();
		}

	// Empty array [] (used for types only):

		while(true)
		{
			mark = mScanner.mark();

			if(mScanner.matchSymbol("[") && mScanner.matchSymbol("]"))
			{
				name.ArrayDims++;

				continue;
			}

			mScanner.markUndo(mark);

			break;
		}

	// Done:

		return name;
	}

/*******************************************************************/

	boolean error(String e)
	{
		mErrors.add(mScanner.getLine(), e);

		return false;
	}

	Object errorNull(String e)
	{
		mErrors.add(mScanner.getLine(), e);

		return null;
	}
}
