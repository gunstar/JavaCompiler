package scanner;

public class Scanner
{
	char[]	mChars;
	char	mEol;
	int		mLine;
	int		mNext;

	public Scanner(String src)
	{
		mChars	= src.toCharArray();
		mEol	= '\n';
		mLine	= 1;
		mNext	= 0;

		mark();
	}

	public int getLine()
	{
		return mLine;
	}

	public ScannerMark mark()
	{
		return new ScannerMark(mLine, mNext);
	}

	public void markUndo(ScannerMark mark)
	{
		mLine = mark.Line;
		mNext = mark.Next;
	}

	public boolean peekKeyword(String s)
	{
		ScannerMark mark = mark();

		boolean ok = matchName(s);

		markUndo(mark);

		return ok;
	}

	public boolean peekSymbol(String s)
	{
		ScannerMark mark = mark();

		boolean ok = matchSymbol(s);

		markUndo(mark);

		return ok;
	}

	public boolean matchEos()
	{
		skipWS();

		return isEos();
	}

	public boolean matchName(String kw)
	{
		ScannerMark mark = mark();

		String n = readName();

		if(n == null || kw.compareTo(n) != 0)
		{
			markUndo(mark);

			return false;
		}

		return true;
	}

	public boolean matchSymbol(String s)
	{
		skipWS();

		char[]	sc	= s.toCharArray();
		int		len = sc.length;

		if(mNext + len > mChars.length)
			return false;

		for(int i = 0; i < len; i++)
			if(sc[i] != mChars[mNext + i])
				return false;

		mNext += len;

		return true;
	}

	public String readChar()
	{
		if(matchEos() || mChars[mNext] != '\'')
			return null;

		int mark = mNext;

		if(++mNext < mChars.length)
		{
			if(mChars[mNext] == '\\')
			{
				if(++mNext < mChars.length)
				{
					char c = mChars[mNext];

					if(++mNext < mChars.length && mChars[mNext++] == '\'')
						return "\\" + c;
				}
			}
			else
			{
				char c = mChars[mNext];

				if(++mNext < mChars.length && mChars[mNext++] == '\'')
					return "" + c;
			}
		}

		mNext = mark;

		return null;
	}

	public Integer readInt()
	{
		if(matchEos() || ! isDigit(mChars[mNext]))
			return null;

		int v = 0;

		for(; ! isEos(); mNext++)
		{
			char c = mChars[mNext];

			if(! isDigit(c))
				break;

			v = v * 10 + (c - '0');
		}

		return v;
	}

	public String readName()
	{
		if(matchEos() || ! isChar(mChars[mNext]))
			return null;

		String s = "";

		for(; ! isEos(); mNext++)
		{
			char c = mChars[mNext];

			if(! isChar(c) && ! isDigit(c))
				break;

			s += c;
		}

		return s;
	}

	public String readString()
	{
		if(matchEos() || mChars[mNext] != '"')
			return null;

		String s = "";

		ScannerMark mark = mark();

		for(mNext++; ! isEos(); mNext++)
		{
			char c = mChars[mNext];

			if(c == '"')
			{
				mNext++;

				return s;
			}

			if(c == '\\' && mNext + 1 < mChars.length)
			{
				s += c;

				c = mChars[++mNext];
			}

			s += c;
		}

		markUndo(mark);

		return null;
	}

	public void skipWS()
	{
		while(! isEos())
		{
			char c = mChars[mNext];

		// Comment:

			if(c == '/' && mNext + 1 < mChars.length)
			{
				if(mChars[mNext + 1] == '/')
				{
					for(mNext += 2; ! isEos(); mNext++)
						if(isEol(mChars[mNext]))
							break;

					continue;
				}

				if(mChars[mNext + 1] == '*')
				{
					for(mNext += 2; ! isEos(); mNext++)
					{
						c = mChars[mNext];

						if(c == '*' && mNext + 1 < mChars.length && mChars[mNext + 1] == '/')
						{
							mNext += 2;

							break;
						}

						if(isEol(c))
							mLine++;
					}

					continue;
				}
			}

		// Normal:

			if(! isWS(c))
				return;

			if(isEol(c))
				mLine++;

			mNext++;
		}
	}

/*******************************************************************/

	public void dump(String header)
	{
		System.out.print(header + " line " + mLine + " [");

		int pMin = mNext - 50; pMin = (pMin < 0 ? 0 : pMin);
		int pMax = mNext + 50; pMax = (pMax > mChars.length ? mChars.length : pMax);

		for(int i = pMin; i < pMax; i++)
		{
			char c = mChars[i];

			if(c <= ' ')
				System.out.print(" ");
			else
				System.out.print("" + mChars[i]);
		}

		System.out.print("]\n");

		System.out.print(header + " line " + mLine + " .");

		for(int i = pMin; i < mNext; i++)
			System.out.print(".");

		System.out.println("^");
	}

/*******************************************************************/

	boolean isChar(char c)
	{
		if('A' <= c && c <= 'Z')
			return true;

		if('a' <= c && c <= 'z')
			return true;

		return c == '_';
	}

	boolean isDigit(char c)
	{
		return '0' <= c && c <= '9';
	}

	boolean isEol(char c)
	{
		return c == mEol;
	}

	boolean isEos()
	{
		return mNext >= mChars.length;
	}

	boolean isWS(char c)
	{
		return c <= ' ' || c == mEol;
	}
}
