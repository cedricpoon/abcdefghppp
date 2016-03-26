package abcdefghppp;

import java.util.*;

class Struct {
	public static Map<Character, Struct> map = new HashMap<Character, Struct>();
	public static Stack<boolean[]> choices = new Stack<boolean[]>();
	public static boolean isForwarding = true;

	public char symbol;
	public char replacement;

	public static int nextChoice() {
		boolean[] choice = choices.peek();
		for (int i = 0; i < choice.length; i++)
			if (choice[i]) {
				choice[i] = false;
				return i;
			}
		return -1;
	}

	public static void prepareChoices(int[] pool, String question) {
		boolean[] b = new boolean[pool.length];
		for (int i = 0; i < pool.length; i++)
			b[i] = (question.contains(pool[i] + "")) ? false : true;
		choices.push(b);
	}

	public static boolean hasChoice() {
		boolean[] choice = choices.peek();
		for (boolean b : choice)
			if (b)
				return true;
		return false;
	}

	public static Struct get(char symbol) {
		if (isForwarding)
			try {
				Integer.parseInt(symbol + "");
				return null;
			} catch (Exception e) {
			}
		return map.get(new Character(symbol));
	}

	public static boolean create(char symbol) {
		try {
			Integer.parseInt(symbol + "");
			return false;
		} catch (Exception e) {
		}
		map.remove(new Character(symbol));
		map.put(new Character(symbol), new Struct(symbol));
		return true;
	}

	public Struct(char symbol) {
		super();
		this.symbol = symbol;
	}

	public void update(char sym) {
		this.symbol = sym;
		map.remove(new Character(symbol));
		map.put(symbol, this);
	}
}

class Tracer {
	public int index;
	public String result;

	public Tracer(int index, String result) {
		super();
		this.index = index;
		this.result = result;
	}
}

public class Solver {
	private String question;
	private int[] choice;
	private final int BASE = 10;

	public Solver(String question, int[] choice) {
		super();
		this.question = "=" + question;
		this.choice = choice;
	}

	private String evaluate(String expression) {
		String[] ss = expression.split("[+\\-*/]");
		if (ss.length == 1)
			return ss[0];
		int one = Integer.parseInt(ss[0]);
		int two = Integer.parseInt(ss[1]);
		if (expression.contains("+"))
			return (one + two) + "";
		else if (expression.contains("-"))
			return (one - two) + "";
		else if (expression.contains("*"))
			return (one * two) + "";
		else if (expression.contains("/") && two != 0)
			return (one / two) + "";
		return "";
	}

	public String solve() {
		String question = this.question;
		int strIndex = question.length() - 1;

		Stack<Tracer> backtrack = new Stack<Tracer>();
		Struct.prepareChoices(choice, question);
		backtrack.add(new Tracer(question.length(), null));

		while (strIndex > -1) {
			char c = question.charAt(strIndex);
			switch (c) {
			case '+':
			case '-':
			case '*':
			case '/':
				if (Struct.isForwarding)
					strIndex--;
				else
					strIndex++;
				break;
			case '=':
				Tracer t = backtrack.peek();
				if (t.index == strIndex) {
					backtrack.pop();
					strIndex++;
					Struct.isForwarding = false;
					break;
				}
				String s = question.substring(strIndex + 1, t.index);
				String res = null;
				res = evaluate(s);
				if (t.result == null || res.equals(t.result)) {
					backtrack.push(new Tracer(strIndex, s.split("[+\\-*/]")[0]));
					strIndex--;
					Struct.isForwarding = true;
					Struct.prepareChoices(choice, question);
				} else {
					strIndex++;
					Struct.isForwarding = false;
				}
				break;
			default:
				Struct.create(c);
				Struct st = Struct.get(c);
				if (Struct.hasChoice() || st == null) {
					if (st != null) {
						char next = Character.forDigit(choice[Struct.nextChoice()], BASE);
						question = question.replace(c, next);
						st.update(next);
					}
					strIndex--;
					if (question.charAt(strIndex) != '=') {
						Struct.prepareChoices(choice, question);
					}
					Struct.isForwarding = true;
				} else {
					strIndex++;
					Struct.choices.pop();
					Struct.isForwarding = false;
					question = this.question.substring(0, strIndex) + question.substring(strIndex);
				}
				break;
			}
		}
		return question.substring(1);
	}

	public static void main(String[] args) {
		Solver s = new Solver("ab-cd=ef+gh=ppp", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		System.out.println(s.solve());
	}
}
