package org.lee.mugen.sprite.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lee.mugen.parser.type.Functionable;
import org.lee.mugen.parser.type.Valueable;
import org.lee.mugen.sprite.background.bgCtrlFunction.BgFunctionDef;
import org.lee.mugen.sprite.cns.eval.function.MathFunction;
import org.lee.mugen.sprite.cns.eval.function.MathsFunctionDef;
import org.lee.mugen.sprite.cns.eval.function.SpriteCnsFunctionDef;
import org.lee.mugen.sprite.cns.eval.function.SpriteCnsTriggerFunction;
import org.lee.mugen.sprite.cns.eval.operator.CnsOperatorsDef;
import org.lee.mugen.sprite.cns.eval.redirect.SpriteRedirect;
import org.lee.mugen.sprite.cns.eval.trigger.function.spriteCns.Gethitvar;

public class ExpressionFactory {
	public static void main(String[] args) {
//		String str = "root,animelem = 1 || root,animelem = 3 || root,animelem = 4";
//		String[] tokens = expression2Tokens(str);
//		evalExpression(str);
//		for (String s: tokens)
//			System.out.println(s);
		
		System.out.println(Pattern.matches(CnsParse._STATE_CTRL_TITLE_REGEX, "	[state 8990, shift]"));
		
	}
	private static final String _OPEN_BRACET_GRP_REGEX = "\\(";

	private static final String _CLOSE_BRACET_GRP_REGEX = "\\)";

//	private static final String _KEY_WORDS_REGEX = "(begin|action|clsn1|clsn2|clsn1default|clsn2default|loopstart)";

	private static final String _OPERATOR_REGEX = CnsOperatorsDef.getOperatorRegex();//"((=|!=|<=|>=|<|>|,|:|\\+|-|/|\\%|\\*))";


	private static final String _TRIGGER_MATHS_FUNCTION_REGEX = MathsFunctionDef.getMathsFunctionDefRegex();
	private static final String _CONST_SPRITE_REGEX = "(\\bconst\\b *\\([a-zA-Z0-9\\.]*\\))";
	private static final String _TRIGGER_FUNCTION_SPRITE_REGEX = SpriteCnsFunctionDef.getCnsTriggerFunctionRegex();
	
	private static final String _TRIGGER_FUNCTION_BG_REGEX = BgFunctionDef.getCnsTriggerFunctionRegex();

	private static final String _COMMENT_REGEX = ";.*$";

	private static final String _COMMENT_OR_EMPTY_REGEX = "^;.*$|^ *$";

	private static final String _FLOAT_REGEX = "((?:\\+|-)?(?:(?:\\.\\d+)|(?:\\d+(?:\\.\\d*)?)))";//"[+-]*(?:\\d+\\.\\d+)|(?:\\d+)";

	private static final String _STRING_REX_EXP = "(\"[^\"\\\\]*(\\\\.^\"\\\\]*)*\")" + "|" + Gethitvar.getConstRegex() + "|" + "([a-zA-Z][a-zA-Z0-9._]*)";
	private static final String _CONST_STRING_REG_EXP = Gethitvar.getConstRegex();
	private static final String _SPECIAL_OPERATOR_REGEX = "(?:(=|!=) *((?:\\[|\\()) *"
			+ _FLOAT_REGEX + " *, *" + _FLOAT_REGEX + " *((?:\\]|\\))))";


	private static final String _CONST_STRING = "(\"[^\"\\\\]*(\\\\.^\"\\\\]*)*\")";
	public static final Pattern _TOKENIZE_CNS_REGEX = Pattern
			.compile(_CONST_STRING + "|" + SpriteRedirect.SPRITE_REDIRECT_REG + "|" + _CONST_SPRITE_REGEX + "|" + _CONST_STRING_REG_EXP + "|" + _TRIGGER_MATHS_FUNCTION_REGEX + "|" + _TRIGGER_FUNCTION_SPRITE_REGEX + "|" + _TRIGGER_FUNCTION_BG_REGEX + "|"
					+ _SPECIAL_OPERATOR_REGEX + "|" +  _OPERATOR_REGEX + "|" + _FLOAT_REGEX + "|"
					+ _OPEN_BRACET_GRP_REGEX + "|"
					+ _CLOSE_BRACET_GRP_REGEX + "|" + _STRING_REX_EXP);


	
	private static class Executor implements Valueable {

		Functionable function;
		Valueable[] params;

		public Executor(Functionable function, Valueable... params) {
			this.function = function;
			this.params = params;
		}

		public Object getValue(String spriteId, Valueable... params) {
			if (params == null || params.length == 0) {
				return function.getValue(spriteId, this.params);
			} else {
				throw new IllegalArgumentException(
						"Especialy this Valuable doesn't take params");
			}
		}
	}

	private static boolean isThisTokenIsOp(String[] tokens, int pos) {
		
		if (pos == 0)
			return false;
		
		if (Pattern.matches("\\(|\\[|,|=|<|>|-|\\+|<=|>=|&&|\\|\\||&|\\||!=|\\*", tokens[pos - 1]))
			return false;
		return true;
	}



	private static String[] expressionSpecialOp2Tokens(String exp, Pattern regex) {
		Matcher matcher = regex.matcher(exp);
		List<String> list = new ArrayList<String>();
		matcher.find();

		list.add(matcher.group(1));
		list.add(matcher.group(2));
		list.add(matcher.group(3));
		list.add(matcher.group(4));
		list.add(matcher.group(5));
		return list.toArray(new String[list.size()]);
	}

	public static String[] getCloseInTokens(String[] strs, int start,
			String openStr, String closeStr) {
		if (!openStr.equals(strs[start]))
			throw new IllegalArgumentException("Token must begin by '"
					+ openStr + "'");
		int open = 0;
		int i = start;
		List<String> list = new ArrayList<String>();
		do {
			if (openStr.equals(strs[i]))
				open++;
			else if (closeStr.equals(strs[i]))
				open--;

			if (open == 0)
				break;
			if (i != start && i != strs.length - 1)
				list.add(strs[i]);
			i++;
		} while (i < strs.length && open != 0);
		if (open > 0) {
		//	throw new IllegalArgumentException("Token must contains '"
			//		+ closeStr + "'");
			System.out.println("Warning !! le fichier n'est pas vraiment clean il y a une ( en trop");
		}
		return list.toArray(new String[list.size()]);
	}
	


	// --------------------------- //

	/**
	 * Tokenize an expression with default regex
	 */
	public static String[] expression2Tokens(String exp) {
		return expression2Tokens(exp, _TOKENIZE_CNS_REGEX);
	}

	/**
	 * Tokenize an expression with given regex
	 */
	public static String[] expression2Tokens(String exp, Pattern regex) {
		Matcher matcher = regex.matcher(exp);
		List<String> list = new ArrayList<String>();
		while (matcher.find())
			list.add(matcher.group());
		return list.toArray(new String[list.size()]);
	}
	public static Valueable[] evalExpression(String str) {
		String[] tokens = expression2Tokens(str);
		return evalExpression(tokens);
	}
	
	public static Valueable[] evalExpression(String[] tokens) {
		return evalExpression(tokens, true, false);
	}
	public static long totalTime = 0;
	public static Valueable[] evalExpression(String[] tokens, boolean isProcessSprite, boolean isProcessBg) {
		long start = System.currentTimeMillis();
		
		final LinkedList<Valueable> values = new LinkedList<Valueable>();
		final LinkedList<MathFunction> ops = new LinkedList<MathFunction>();
		int i = 0;
		ArrayList<Valueable> valuableList = new ArrayList<Valueable>();
		while (i < tokens.length) {
			if (Pattern.matches(_OPEN_BRACET_GRP_REGEX, tokens[i])) {
				String[] subTokens = getCloseInTokens(tokens, i, "(", ")");
				Valueable valueable = evalExpression(subTokens)[0];
				values.add(valueable);
				i += subTokens.length + 1;
				i++;
			} else if (Pattern.matches(_FLOAT_REGEX, tokens[i])) {
				final float res = Float.parseFloat(tokens[i]);
				values.add(new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return res;
					}
				});
				i++;
			} else if (Pattern.matches(_SPECIAL_OPERATOR_REGEX, tokens[i])) {
				final String[] subTokens = expressionSpecialOp2Tokens(
						tokens[i], Pattern.compile(_SPECIAL_OPERATOR_REGEX));
				String equalityOp = subTokens[0];
				String openBracet = subTokens[1];
				final float first = Float.parseFloat(subTokens[2]);
				final float last = Float.parseFloat(subTokens[3]);
				String closeBracet = subTokens[4];

				Valueable[] valueables = new Valueable[3];
				valueables[0] = values.getLast();
				valueables[1] = new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return first;
					}
				};
				valueables[2] = new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return last;
					}
				};

				MathFunction newOp = CnsOperatorsDef.getSpecialOp(equalityOp
						+ openBracet + closeBracet);
//				Executor executor = new Executor(newOp, valueables);
//				values.add(executor);
				values.add(newOp);
				values.add(valueables[1]);
				values.add(valueables[2]);
				ops.add(newOp);
				i++;
			} else if (isProcessSprite && Pattern.matches(SpriteRedirect.SPRITE_REDIRECT_REG, tokens[i])) {
				MathFunction newOp = (MathFunction) SpriteRedirect.getFunction(tokens[i]);
				if (newOp.getFunction() instanceof SpriteRedirect.SpecialPatternRedirect){
					
					if (tokens[i + 1].equals("(")) {
						i++;
						String[] subTokens = getCloseInTokens(tokens, i, "(", ")");
						Valueable param = evalExpression(subTokens)[0];
						((SpriteRedirect.SpecialPatternRedirect)newOp.getFunction()).setParam(param);
						i += subTokens.length + 1;
						
					}
					
				}
				i++; // la virgule
				assert tokens[i].equals(",");
				values.add(newOp);
				ops.add(newOp);
				i++;
			} else if (Pattern.matches(_OPERATOR_REGEX, tokens[i])) {
				if (tokens[i].equals("-") || tokens[i].equals("+")) {
					final int mul = tokens[i].equals("-")? -1: 1;
					if (!isThisTokenIsOp(tokens, i)) {
						values.add(new Valueable() {

							public Object getValue(String spriteId, Valueable... params) {
								return mul;
							}});
						MathFunction mulOp = CnsOperatorsDef.getOperator("*");
						ops.add(mulOp);
						values.add(mulOp);
						i++;
						continue;
					}
				}
				MathFunction newOp = CnsOperatorsDef.getOperator(tokens[i]);
				// TODO si séparator
				if (",".equals(tokens[i])) {
					giveValue(values, ops, valuableList);
					i++;
					if (i > tokens.length - 1) {
						values.add(new Valueable() {

							public Object getValue(String spriteId, Valueable... params) {
								return 0;
							}});
					}
					continue;
				}

				values.add(newOp);
				ops.add(newOp);
				
				i++;
				
			} else if (Pattern.matches(_TRIGGER_MATHS_FUNCTION_REGEX, tokens[i])) {
				final Functionable f = MathsFunctionDef.getFunction(tokens[i]);
				i++;
				String[] subTokens = {};
				if (tokens[i].equals("(")) {
					subTokens = getCloseInTokens(tokens, i, "(", ")");
				}
				 
				final Valueable[] valueables;
				
				if (subTokens.length != 0) {
					i += subTokens.length + 1;
					valueables = evalExpression(subTokens);
				} else {
					valueables = new Valueable[0];
					i--;
				}
				
				values.add(new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return f.getValue(spriteId, valueables);
					}
				});
				i++;
			} else if (isProcessSprite && Pattern.matches(_CONST_SPRITE_REGEX, tokens[i])) {
				
				Matcher m = Pattern.compile("const *\\((.*)\\)").matcher(tokens[i]);
				m.find();
				final String constant = m.group(1);
				
				
				final SpriteCnsTriggerFunction f = SpriteCnsFunctionDef.getSpriteCnsFunc("const");
				i++;
				
				values.add(new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return f.getValue(spriteId, new Valueable() {
							public Object getValue(String spriteId, Valueable... params) {
								return constant;
							}});
					}
				});
			} else if (isProcessSprite && Pattern.matches(_TRIGGER_FUNCTION_SPRITE_REGEX, tokens[i])) {
				final SpriteCnsTriggerFunction f = SpriteCnsFunctionDef.getSpriteCnsFunc(tokens[i]);
				// TODO : Do trigger own parser from function
				List<Valueable> result = new ArrayList<Valueable>();
				int newPos = f.parseValue(tokens, i, result);
				i = newPos;
				final Valueable[] valueables = result.toArray(new Valueable[result.size()]);
				
				values.add(new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return f.getValue(spriteId, valueables);
					}
				});
				i++;
			} else if (isProcessBg && Pattern.matches(_TRIGGER_FUNCTION_BG_REGEX, tokens[i])) {
				final SpriteCnsTriggerFunction f = BgFunctionDef.getSpriteCnsFunc(tokens[i]);
				// TODO : Do trigger own parser from function
				List<Valueable> result = new ArrayList<Valueable>();
				int newPos = f.parseValue(tokens, i, result);
				i = newPos;
				final Valueable[] valueables = result.toArray(new Valueable[result.size()]);
				
				values.add(new Valueable() {
					public Object getValue(String bgId, Valueable... params) {
						return f.getValue(bgId, valueables);
					}
				});
				i++;
			} else if (Pattern.matches(_STRING_REX_EXP, tokens[i])) {
				final String res;// = tokens[i];
				String goodString = tokens[i];
				if (goodString.startsWith("\"") && goodString.endsWith("\"")) {
					goodString = goodString.substring(1, goodString.length() - 1).replaceAll("\\\"", "\"");
				}
				res = goodString;
				
				values.add(new Valueable() {
					public Object getValue(String spriteId, Valueable... params) {
						return res;
					}
				});
				i++;
			} else {
				throw new IllegalStateException("this state must not be reacheds");
			}
		}

		giveValue(values, ops, valuableList);
		
		long end = System.currentTimeMillis();
		
		totalTime += end - start;
		
		return (Valueable[]) valuableList.toArray(new Valueable[0]);
	}

	private static void giveValue(final LinkedList<Valueable> values, 
			LinkedList<MathFunction> ops, 
			ArrayList<Valueable> valuableList) {
		Collections.sort(ops, new Comparator<MathFunction>() {
			public int compare(MathFunction o1, MathFunction o2) {
				return o2.getPriority() - o1.getPriority();
			}
		});
		for (final MathFunction op: ops) {
			int paramCount = op.getParamCount();
			int index = values.indexOf(op);
			assert index != -1;
			if (paramCount == 3) { // special Op
				final Valueable one = values.get(index - 1);
				final Valueable two = values.get(index + 1);
				final Valueable tree = values.get(index + 2);
				
				values.remove(op);
				values.remove(one);
				values.remove(two);
				values.remove(tree);
				Valueable result = new Valueable() {

					public Object getValue(String spriteId, Valueable... params) {
						
						return op.getValue(spriteId, one, two, tree);
					}};
				
				values.add(index - 1, result);
				
			} else if (paramCount == 2) {
				final Valueable one = values.get(index - 1);
				final Valueable two = values.get(index + 1);
				
				values.remove(op);
				values.remove(one);
				values.remove(two);
				
				Valueable result = new Valueable() {

					public Object getValue(String spriteId, Valueable... params) {
						
						return op.getValue(spriteId, one, two);
					}};
				values.add(index - 1, result);
			} else if (paramCount == 1) {				
				if (index + 1 < values.size()) {
					final Valueable one = values.get(index + 1);
	
					
					values.remove(op);
					values.remove(one);
					
					Valueable result = new Valueable() {
	
						public Object getValue(String spriteId, Valueable... params) {
							
							return op.getValue(spriteId, one);
						}};
					values.add(index, result);
				} else {
					values.remove(op);
					System.err.println("Plus rien pour cet operateur unaire. l'expression avant est retournée");
					continue;
				}
			}
//			else {
//				throw new IllegalArgumentException("Ce parseur ne prend que des operateurs unaire ou binaire");
//			}
		}
		if (values.size() > 1) {
			final List<Valueable> result = new ArrayList<Valueable>();
			result.addAll(values);
			Valueable v = new Valueable() {

				public Object getValue(String spriteId, Valueable... params) {
					StringBuilder buffer = new StringBuilder();
					for (Valueable val: result) {
						buffer.append(val.getValue(spriteId, params));
					}
					return buffer.toString();
				}
				
			};
			values.clear();
			values.add(v);
		}
		valuableList.addAll(values);
		values.clear();
		ops.clear();
	}

}