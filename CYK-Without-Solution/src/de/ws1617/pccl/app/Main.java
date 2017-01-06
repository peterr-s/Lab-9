package de.ws1617.pccl.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.GrammarUtils;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;
import de.ws1617.pccl.parser.CYK;
import de.ws1617.pccl.tree.Tree;

public class Main
{

	public static void main(String[] args)
	{
		try
		{
			Grammar grammar = GrammarUtils.readGrammar(args[0]);
			Lexicon lexicon = GrammarUtils.readLexicon(args[1]);
			NonTerminal start = new NonTerminal(args[2]);

			ArrayList<Terminal> tokens = new ArrayList<Terminal>();
			for(int i = 3; i < args.length; i ++)
				tokens.add(new Terminal(args[i].toLowerCase()));
			
			ArrayList<Tree<Symbol>> parses = CYK.parse(grammar, lexicon, start, tokens);
			if(parses == null)
				System.out.println("No parses found.");
			else for(Tree<Symbol> parse : parses)
				System.out.println(parse.toString());

		}
		/*catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Not enough arguments. Provide a grammar, a lexicon, a start symbol, and a sentence.");
		}*/
		catch (IOException e)
		{
			System.err.println("File read error: " + e.getMessage());
		}
	}
}
