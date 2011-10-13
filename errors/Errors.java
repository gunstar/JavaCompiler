package errors;

import java.util.*;

public class Errors extends ArrayList<String>
{
	private String SrcFileName;

	public Errors(String srcFileName)
	{
		SrcFileName = srcFileName;
	}

	public void add(int line, String error)
	{
		if(line > 0)
			add("Error in " + SrcFileName + " (line " + line + "): " + error);
		else
			add("Error: " + error);
	}

	public void dump()
	{
		for(int i = 0; i < size(); i++)
			System.out.println(get(i));
	}
}
