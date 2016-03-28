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
			b[i] = (question.contains(Integer.toString(pool[i], Solver.base) + "")) ? false : true;
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
				Integer.parseInt(symbol + "", Solver.base);
				return null;
			} catch (Exception e) {
			}
		return map.get(new Character(symbol));
	}

	public static boolean create(char symbol) {
		try {
			Integer.parseInt(symbol + "", Solver.base);
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
	public static int base = 10;

	public Solver(String question, int base, int[] choice) {
		super();
		this.question = "=" + question;
		this.choice = choice;
		Solver.base = base;
	}

	private String evaluate(String expression) {
		String[] ss = expression.split("[+\\-*/]");
		if (ss.length == 1)
			return ss[0];
		int one = Integer.parseInt(ss[0], base);
		int two = Integer.parseInt(ss[1], base);
		int res = 0;
		try {
			if (expression.contains("+"))
				res = one + two;
			else if (expression.contains("-"))
				res = one - two;
			else if (expression.contains("*"))
				res = one * two;
			else if (expression.contains("/"))
				res = one / two;
			return Integer.toString(res, Solver.base) + "";
		} catch (Exception e) {
			return "";
		}
	}

	public String solve() {
		String question = this.question;
		int strIndex = question.length() - 1;

		Stack<Tracer> backtrack = new Stack<Tracer>();
		Struct.prepareChoices(choice, question);
		backtrack.add(new Tracer(question.length(), null));

		while (strIndex > -1 && strIndex < question.length()) {
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
						char next = Character.forDigit(choice[Struct.nextChoice()], base);
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
		if (strIndex == -1)
			return question.substring(1);
		else
			return null;
	}

	public static void main(String[] args) {
		Solver s = new Solver("ghi-klm=opq+stu=wwww", 16,
				new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf });
		System.out.println(s.solve());
	}
}
