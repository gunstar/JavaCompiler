package ast;

import java.util.*;

public class AstFile extends AstComponent
{
	public List<AstName>	Imports;
	public AstName			Package;
	public String			SrcFileName;

	public AstFile(String srcFileName)
	{
		Imports		= new ArrayList<AstName>();

		SrcFileName	= srcFileName;
	}
}
