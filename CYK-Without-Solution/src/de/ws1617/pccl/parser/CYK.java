package de.ws1617.pccl.parser;

import java.util.ArrayList;
import java.util.HashMap;

import de.ws1617.pccl.grammar.Grammar;
import de.ws1617.pccl.grammar.Lexicon;
import de.ws1617.pccl.grammar.NonTerminal;
import de.ws1617.pccl.grammar.Symbol;
import de.ws1617.pccl.grammar.Terminal;
import de.ws1617.pccl.tree.Tree;

public class CYK
{

	public static ArrayList<Tree<Symbol>> parse(Grammar grammar, Lexicon lexicon, NonTerminal startSymbol, ArrayList<Terminal> tokens)
	{
		int n = tokens.size();

		HashMap<NonTerminal, ArrayList<Tree<Symbol>>>[][] parses = new HashMap[n][n];

		return parses[0][n].get(startSymbol);
	}
}
