package src;

import java.io.*;

public class SrcFromFile
{
	static public String load(String fileName)
	{
		StringBuffer	buffer	= new StringBuffer();
		String			eol		= "\n";
		File			file	= new File(fileName);

		try
		{
			String text;

			BufferedReader reader = new BufferedReader(new FileReader(file));

			while((text = reader.readLine()) != null)
				buffer.append(text).append(eol);

			return buffer.toString();
		}
		catch(IOException e)
		{
			return null;
		}
	}
}
