package expressioncalculator.src;

import java.util.ListIterator;
import java.util.Stack;

/**
 * 逆ポーランド形式変換
 * 
 * @author jyotaku
 * @since 2016.5.27
 */
public class ReversePolishCreator {

	/**
	 * 逆ポーランド形式文字列に変換
	 * 
	 * @param originExpression
	 * @return
	 * 　　　逆ポーランド形式の計算式
	 */
	public static String getReversePolishStr(String originExpression) {
		StringBuffer sb = new StringBuffer();
		Stack<String> polishStack = new Stack<>();
		String operator;
		int i = 0;
		while (i < originExpression.length()) {
			if (Utils.isNotOperator(originExpression.charAt(i))) {
				sb.append(Utils.SPLIT_WORD);
				do {
					sb.append(originExpression.charAt(i++));
				} while (i < originExpression.length() && Utils.isNotOperator(originExpression.charAt(i)));
				sb.append(Utils.SPLIT_WORD);
			} else {
				operator = String.valueOf(originExpression.charAt(i++));
				switch (operator) {
				case Utils.L_BRACKET:
					polishStack.push(Utils.L_BRACKET);
					break;
				case Utils.R_BRACKET:
					processRightBracket(sb, polishStack);
					break;
				case Utils.PLUS:
				case Utils.MINUS:
					while (!polishStack.empty() && polishStack.peek() != Utils.L_BRACKET) {
						sb.append(polishStack.pop() + Utils.SPLIT_WORD);
					}
					polishStack.push(operator);
					break;
				case Utils.TIMES:
					while (!polishStack.empty() && polishStack.peek().equals(Utils.TIMES)) {
						sb.append(polishStack.pop() + Utils.SPLIT_WORD);
					}
					polishStack.push(operator);
					break;
				}
			}
		}
		
	    // 残りの演算子を書き出す
		ListIterator<String> it = polishStack.listIterator(polishStack.size());
		while (it.hasPrevious()) {
			sb.append(it.previous() + Utils.SPLIT_WORD);
		}

		return sb.toString().trim();
	}

	/**
	 * 右括弧が検出された時の処理
	 * (！！パラメータの中身を書き換える！！)
	 * 
	 * @param sb
	 * @param polishStack
	 */
	private static void processRightBracket(StringBuffer sb, Stack<String> polishStack) {
		while (!polishStack.peek().equals(Utils.L_BRACKET)) {
			String tmpOperator = (String) polishStack.pop();
			sb.append(tmpOperator);
			if (tmpOperator.length() == 1 && Utils.isOperator(tmpOperator)) {
				sb.append(Utils.SPLIT_WORD);
			}
		}
		polishStack.pop();
		sb.append(Utils.SPLIT_WORD);
	}
	
}
