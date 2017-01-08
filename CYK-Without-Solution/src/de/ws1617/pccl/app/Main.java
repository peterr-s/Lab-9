package de.ws1617.pccl.app;

import java.io.IOException;
import java.util.ArrayList;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.GrammarUtils;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;
import de.ws1617.pccl.parser.CYK;
import de.ws1617.pccl.tree.DOTWriter;
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

			// get switches
			boolean makeDOT = false,
					makeJPG = false,
					showTable = false;
			int brackets = 0;
			/* -d makes a .dot file for each tree
			 * -j makes a .jpg file for each tree
			 * -t shows the CYK table
			 * -b[n] shows the bracket representation for each tree (or first n if specified)
			 */
			int sentenceBegin = 3;
			while(sentenceBegin < args.length && args[sentenceBegin].charAt(0) == '-')
			{
				if(args[sentenceBegin].contains("d"))
					makeDOT = true;
				if(args[sentenceBegin].contains("j"))
					makeJPG = true;
				if(args[sentenceBegin].contains("t"))
					showTable = true;
				
				int index;
				if((index = args[sentenceBegin].indexOf('b')) > 0)
				{
					if(index + 1 >= args[sentenceBegin].length()) // if number is unspecified, default to showing all
						brackets = -1;
					else try
					{
						brackets = Integer.parseInt(args[sentenceBegin].substring(index + 1, args[sentenceBegin].length()));
					}
					catch(NumberFormatException e)
					{
						brackets = -1; // if not a valid number, default to showing all
					}
				}
				
				sentenceBegin++;
			}
			
			// get sentence
			ArrayList<Terminal> tokens = new ArrayList<Terminal>();
			for(int i = sentenceBegin; i < args.length; i ++)
				tokens.add(new Terminal(args[i].toLowerCase()));
			
			ArrayList<Tree<Symbol>> parses = CYK.parse(grammar, lexicon, start, tokens, showTable);
			if(parses == null)
				System.out.println("No parses found.");
			else
			{
				// show number of parses found
				int ct = parses.size();
				System.out.println(ct + " parse" + (ct != 1 ? "s" : "") + " found.\n");
				
				// show bracketed forms
				if(brackets < 0)
					brackets = ct;
				for(int i = 0; i < brackets; i++)
					System.out.println(parses.get(i).toString());
				System.out.println(); // formatting
				
				// make files
				if(makeDOT || makeJPG) for(Tree<Symbol> parse : parses)
					DOTWriter.write(parse, makeDOT, makeJPG);
			}

		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Not enough arguments. Provide a grammar, a lexicon, a start symbol, and a sentence.");
			System.exit(1);
		}
		catch (IOException e)
		{
			System.err.println("File read error: " + e.getMessage());
			System.exit(1);
		}
		catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
		System.exit(0);
	}
}
