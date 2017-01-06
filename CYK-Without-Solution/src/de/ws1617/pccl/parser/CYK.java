package de.ws1617.pccl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

		@SuppressWarnings("unchecked") // this is the only way to do this
		HashMap<NonTerminal, ArrayList<Tree<Symbol>>>[][] parses = new HashMap[n][n];
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
				parses[i][j] = new HashMap<>();
		}
		
		// POS tagging
		for(int s = 0; s < n; s++) // for each terminal
		{
			Terminal token = tokens.get(s);
			
			// find the nonterminals that can correspond to it
			HashSet<NonTerminal> tags = lexicon.getPosTags(token);
			
			if(tags.isEmpty())
			{
				System.out.println("unrecognized word: " + token.getValue());
				return null;
			}
			
			for(NonTerminal tag : tags) // for each tag
			{
				// this is the list of ways to get from the nonterminal to the phrase; will in this case only have one element
				ArrayList<Tree<Symbol>> tagTrees = new ArrayList<>();
				
				// make a tree
				Tree<Symbol> tree = new Tree<>(tag);
				tree.addChild(new Tree<>(token));
				
				// add the tree to the list
				tagTrees.add(tree);
				
				// add the list to the map
				parses[s][0].put(tag, tagTrees);
			}
		}
		
		// table traversal
		for(int l = 2; l <= n; l++) // for all substrings of length >= 2 (l = 1 was handled by POS tagging)
		{
			for(int s = 1; s <= n - l + 1; s++) // beginning at all s possible
			{
				for(int p = 1; p <= l - 1; p++) // split at any point possible
				{
					// if either substring was not parsed, continue
					if(parses[s - 1][p - 1].isEmpty() || parses[s + p - 1][l - p - 1].isEmpty())
						continue;
					
					// get the heads of the appropriate cells
					Set<NonTerminal> leftHeads = parses[s - 1][p - 1].keySet(),
							rightHeads = parses[s + p - 1][l - p - 1].keySet();
					
					// else check whether there is a production rule that fits
					for(NonTerminal phraseType : grammar.getNonTerminals()) // for each nonterminal
					{
						// list of parses
						ArrayList<Tree<Symbol>> phraseParses = new ArrayList<>();
						
						// check each rule against the cell contents
						HashSet<ArrayList<NonTerminal>> productions = grammar.getRuleForLHS(phraseType);
						for(ArrayList<NonTerminal> production : productions)
						{
							// if there's a match
							if(leftHeads.contains(production.get(0)) && rightHeads.contains(production.get(1)))
							{
								// make all trees
								ArrayList<Tree<Symbol>> leftChildren = parses[s - 1][p - 1].get(production.get(0)),
										rightChildren = parses[s + p - 1][l - p - 1].get(production.get(1));
								for(Tree<Symbol> leftChild : leftChildren) // for all permutations
								{
									for(Tree<Symbol> rightChild : rightChildren)
									{
										// make the tree
										Tree<Symbol> temp = new Tree<>(phraseType);
										temp.addChild(leftChild);
										temp.addChild(rightChild);
										
										// add the tree
										phraseParses.add(temp);
									}
								}
							}
						}
						
						// add the trees if there are any
						if(!phraseParses.isEmpty())
						{
							if(!parses[s - 1][l - 1].containsKey(phraseType))
								parses[s - 1][l - 1].put(phraseType, phraseParses);
							else
								parses[s - 1][l - 1].get(phraseType).addAll(phraseParses); // prevent overwriting
						}
					}
				}
			}
		}

		// DEBUG: show table
		/*for(Terminal token : tokens)
			System.out.print(token.getValue() + "\t");
		System.out.println();
		for(int i = 0; i < n; i++)
		{
			for(int j = 0; j < n; j++)
				System.out.print(parses[j][i].keySet() + "\t");
			System.out.println();
		}
		System.out.println();*/
		
		return parses[0][n - 1].get(startSymbol);
	}
}
