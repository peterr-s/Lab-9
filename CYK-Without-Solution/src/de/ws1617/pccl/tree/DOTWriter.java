package de.ws1617.pccl.tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" }) // generics are weird but this is all kosher I swear
public class DOTWriter
{
	public static String makeDOT(Tree tree)
	{
		return "digraph {\n" + makeString(tree) + "}";
	}
	
	private static String makeString(Tree tree)
	{
		List<Tree> children = tree.getChildren();
		
		// make entry for this node
		String stringForm = "\t{" + tree.hashCode() + "[label=\"" + tree.getData().toString() + "\"]} -> {";
		for(Tree t : children)
			stringForm += t.hashCode() + " ";
		stringForm += "}\n";
		
		// make child entries recursively
		for(Tree t : children)
			stringForm += makeString(t);
		
		return stringForm;
	}
	
	public static void write(Tree tree, boolean DOT, boolean JPG)
	{
		String DOTString = makeDOT(tree);
		int hash = tree.hashCode();
		/*if(hash < 0)
			hash *= -1;*/
		String path = "./tree" + hash;
		
		// make DOT file
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path + ".dot")));
			writer.write(DOTString);
			writer.close();
		}
		catch(Exception e)
		{
			System.err.println("I/O error: DOT file at " + path + " could not be created.");
			return;
		}
		
		// make JPG file
		if(JPG) try
		{
			Runtime.getRuntime().exec("dot -Tjpg " + path + ".dot -o " + path + ".jpg").waitFor();
			System.out.println(path + ".jpg created.");
		}
		catch(Exception e)
		{
			System.err.println("GraphViz error: JPG at " + path + " could not be created. Make sure you have GraphViz installed and on your PATH.");
			return;
		}
		
		// destroy DOT file if not wanted
		if(!DOT) try
		{
			File DOTFile = new File(path + ".dot");
			DOTFile.delete();
		}
		catch(Exception e)
		{
			System.err.println("I/O error: temporary DOT file at " + path + " could not be created.");
		}
		else System.out.println(path + ".dot created.");
	}
}
