package boot;

import ast.*;
import ast_from_src.*;
import errors.*;
import java.io.*;
import java_from_ast.*;
import src.*;

public class Boot
{
	private static void compileFile(String srcFileName, String dstFileName, Errors errors)
	{
	// Load src:

		String src = SrcFromFile.load(srcFileName);

		if(src == null)
		{
			errors.add(0, "Can't load file '" + srcFileName + "'");

			return;
		}

		//System.out.print(src);

	// Ast from Src:

		AstFromSrc parser = new AstFromSrc();

		AstFile ast = parser.parse(srcFileName, src, errors);

		if(errors.size() > 0)
			return;

	// Java from Ast:

		if(true)
		{
		// Make dstFileName folders:

			File file = new File(dstFileName);

			file.getParentFile().mkdirs();

		// Write:

			if(true)
			{
				try
				{
					FileWriter writer = new FileWriter(file);

					JavaFromAst javaFromAst = new JavaFromAst(writer);

					javaFromAst.print(ast);

					writer.close();
				}
				catch(IOException e)
				{
					errors.add("Can't write to file '" + dstFileName + "'");
				}
			}
		}

	// Ast with Types from Ast:

	// IL from Ast:

	// C++ from IL:

	// Done!
	}

	public static void main(String[] args)
	{
		if(args.length < 2)
		{
			System.out.println("usage: exe <dst_folder> <java_files>");

			return;
		}

		for(int i = 1; i < args.length; i++)
		{
			String srcFileName	= args[i];
			String dstFileName	= args[0] + "/" + srcFileName;
			Errors errors		= new Errors(srcFileName);

			System.out.println("[" + (1 + i) + "] " + srcFileName + " -> [" + dstFileName + "]");

			compileFile(srcFileName, dstFileName, errors);

			if(errors.size() > 0)
			{
				errors.dump();

				break;
			}
		}
	}
}
