package expressioncalculator.src;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * 問題2
 * 
 * @author jyotaku
 * @since 2016.5.27
 */
public class ExpressionCalculator {
	
	private final static String TMP_SYMBOL_HOLDER = "#";

	private final List<String> parameterStrList = Lists.newArrayList();
	private final List<String> answerList = Lists.newArrayList();
	private String originExpression;
	private String inputFileName;

	public ExpressionCalculator(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	/**
	 * メイン処理関数
	 */
	public void run() {
		// 数式とパラメータをテキストファイルから取得
		initializeExpressionAndParameters(this.inputFileName);
		for (String parameters : this.parameterStrList) {
			// 引数代入
			String expression = substituteParameters(this.originExpression, parameters);
			// 逆ポーランド文字列に変換
			String reversePolishStr = ReversePolishCreator.getReversePolishStr(expression);
			// 逆ポーランド文字列で計算
			String calcResult = calcReversePolishStr(reversePolishStr);
			// 多項式整理
			String resultStr = organizeExpression(calcResult);
			this.answerList.add(resultStr);
		}
		// 結果出力
		printoutAnswer(this.inputFileName, this.answerList);
	}

	/**
	 * 計算式と計算用パラメータの初期化
	 * 
	 * @param inputFileName
	 */
	private void initializeExpressionAndParameters(String inputFileName) {
		try (FileReader fr = new FileReader(inputFileName); BufferedReader br = new BufferedReader(fr);) {
			List<String> lines = br.lines().collect(Collectors.toList());
			this.originExpression = lines.get(0);
			lines = lines.subList(2, lines.size());
			this.parameterStrList.addAll(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  引数代入
	 *  
	 * @param expression
	 * @param parameters
	 * @return
	 *     引数代入された式
	 */
	private String substituteParameters(String expression, String parameters) {
		List<String> parameterList = Lists.newArrayList(parameters.split(","));
		for (String parameter : parameterList) {
			String[] parameterArray = parameter.split("=");
			String replacement = parameterArray[1];
			long a = Long.parseLong(replacement);
			if (a < 0) {
				replacement = "(0" + replacement + ")";
			}
			expression = expression.replaceAll(parameterArray[0], replacement);
		}
		if(expression.startsWith(Utils.MINUS)){
			expression = "0" + expression;
		}
		return expression;
	}

	/**
	 *  逆ポーランド文字列に基づき、結果を計算
	 *  
	 * @param reversePolishStr
	 * @return
	 *     数字または多項式を返す
	 */
	private String calcReversePolishStr(String reversePolishStr) {
		Stack<String> charStack = new Stack<>();
		String parts[] = reversePolishStr.split(Utils.SPLIT_WORD + Utils.PLUS);
		String calcResult = StringUtils.EMPTY;
		for (int i = 0; i < parts.length; i++) {
			String singlePart = parts[i];
			if (!Utils.isOperator(singlePart)) {
				charStack.push(singlePart);
			} else {
				String item2 = charStack.pop();
				String item1 = charStack.pop();
				calcResult = calculate(item1, item2, singlePart);
				charStack.push(calcResult);
			}
		}
		return calcResult;
	}

	/**
	 *  多項式を整理
	 *  
	 * @param expression
	 * @return
	 *     数字または多項式を返す
	 */
	private String organizeExpression(String expression) {
		long numberAmount = 0;
		List<SubTerm> unknownPartList = Lists.newArrayList();

		// 数字項目と未知数項目を分離
		String[] partsArray = Utils.getPartsArray(expression);
		for (int i = 0; i < partsArray.length; i++) {
			if (NumberUtils.isNumber(partsArray[i])) {
				numberAmount += Long.parseLong(partsArray[i]);
			} else {
				unknownPartList.add(new SubTerm(partsArray[i]));
			}
		}

		// 同じ未知数項目を足しあわせ、項目の順番をソート
		List<SubTerm> finalUnknownPartList = summaryAndSortUnknownParts(unknownPartList);

		// 結果文字列の作成
		String resultStr = createResultString(numberAmount, finalUnknownPartList);
		
		return resultStr;
	}
	
	/**
	 * 結果文字列の作成
	 * 
	 * @param numberAmount
	 * @param finalUnknownPartList
	 * @return
	 *    計算結果の多項式または数字
	 */
	private String createResultString(long numberAmount, List<SubTerm> finalUnknownPartList) {
		// 未知数なしの場合、数字のみ返す
		if (finalUnknownPartList.size() == 0) {
			return String.valueOf(numberAmount);
		}
		
		// 未知数リストを繋ぎぐ
		StringBuffer sb = new StringBuffer();
		finalUnknownPartList.forEach(part -> {
			sb.append(part.getOutputStr());
		});
		String resultStr = sb.toString();
		if (resultStr.startsWith(Utils.PLUS)) {
			resultStr = resultStr.substring(1);
		}
		
		// 数字部分を結果文字列に追加
		if (numberAmount != 0) {
			if (numberAmount > 0) {
				resultStr += Utils.PLUS + numberAmount;
			}else{
				resultStr += numberAmount;
			}
		}
		return resultStr;
	}

	/**
	 * 同じ未知数項目を足しあわせ、項目の順番をソート
	 * 
	 * @param unknownPartList
	 * @return
	 *     多項式リスト
	 */
	private List<SubTerm> summaryAndSortUnknownParts(List<SubTerm> unknownPartList) {
		ListMultimap<String, SubTerm> sumMap = ArrayListMultimap.create();
		unknownPartList.forEach(unknowPart -> {
			sumMap.put(unknowPart.getKey(), unknowPart);
		});
		List<SubTerm> finalUnknownPartList = sumMap.keySet().stream().map(key -> {
			List<SubTerm> numberList = sumMap.get(key);
			long amount = 0;
			for (int i = 0; i < numberList.size(); i++) {
				amount += numberList.get(i).getNumber();
			}
			SubTerm rlt = numberList.get(0);
			rlt.setNumber(amount);
			return rlt;
		}).filter(term -> term.getNumber() != 0)
				.sorted(Comparator.comparingInt(SubTerm::getDegreeAmount).reversed()
						.thenComparing(Comparator.comparing(SubTerm::getUnknownStrDistinct)))
				.collect(Collectors.toList());

		return finalUnknownPartList;
	}

	/**
	 * 計算結果をファイルに書き出す
	 * 
	 * @param inputFileName
	 * @param answerList
	 */
	private void printoutAnswer(String inputFileName, List<String> answerList) {
		String answerFolderPath = Utils.createAnswerFolder(inputFileName);
		String outputFileName = Utils.getOutputFileName(answerFolderPath, inputFileName);
		try (OutputStream out = new FileOutputStream(outputFileName); PrintWriter writer = new PrintWriter(out);) {
			answerList.forEach(writer::println);
			System.out.println("**** FINISH ****");
			System.out.println("Answer file path: " + outputFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 多項式の逆ポーランド文字列を計算
	 * 
	 * @param a
	 * @param b
	 * @param operator
	 * @return
	 * 　　数字また未知数を含む項
	 */
	private String calculate(String a, String b, String operator) {
		String resultStr = StringUtils.EMPTY;
		
		// 数字だけの場合
		resultStr = Utils.calcNumber(a, b, operator);
		if(!Strings.isNullOrEmpty(resultStr)){
			return resultStr;
		}
		
		// 未知数を含む場合
		switch (operator) {
		case Utils.PLUS:
			return b.startsWith(Utils.MINUS) 
					? a + b 
				    : a + Utils.PLUS + b;
		case Utils.MINUS:
			return a + swapPlusMinus(b);
		case Utils.TIMES:
			return expandTimesExpression(a, b);
		default:
			System.out.println("Unexpect Operator");
			return "";
		}
	}
	
	/**
	 * 多項式文字列間の掛け算を計算
	 * 
	 * @param a
	 * @param b
	 * @return
	 *     多項式
	 */
	private String expandTimesExpression(String a, String b) {
		String resultStr = StringUtils.EMPTY;
		String[] aArray = Utils.getPartsArray(a);
		String[] bArray = Utils.getPartsArray(b);
		for (int i = 0; i < aArray.length; i++) {
			for (int j = 0; j < bArray.length; j++) {
				// 数字×数字の場合
				String numberResult = Utils.calcNumber(aArray[i],bArray[j],Utils.TIMES);
				if(!Strings.isNullOrEmpty(numberResult)){
					if(!numberResult.startsWith(Utils.MINUS)){
						numberResult = Utils.PLUS + numberResult;
					}
					resultStr += numberResult;
					continue;
				}
				// 少なくとも片方は未知数を含む
				SubTerm aTerm = new SubTerm(aArray[i]);
				SubTerm bTerm = new SubTerm(bArray[j]);
				long rltNumber = aTerm.getNumber() * bTerm.getNumber();
				if(rltNumber == 0){
					resultStr += "0";
				}else{					
					String unknownResult = aTerm.getUnknownStr() + bTerm.getUnknownStr();
					resultStr += getInitialNumberStr(rltNumber) + unknownResult;
				}
			}
		}
		if(resultStr.startsWith(Utils.PLUS)){
			resultStr = resultStr.substring(1);
		}
		return resultStr;
	}
	
	/**
	 * 先頭数字を表す文字列を取得
	 * 
	 * @param number
	 * @return
	 */
	private String getInitialNumberStr(long number){
		String numberStr = String.valueOf(number);
		if(number == 1){
			numberStr = Utils.PLUS;
		}else if (number == -1){
			numberStr = "-";
		}else if (number > 0){
			numberStr = Utils.PLUS + numberStr;
		}
		return numberStr;
	}

	/**
	 * 項目内のプラスとマイナス符号を逆転する
	 * 
	 * @param term
	 * @return
	 */
	private String swapPlusMinus(String term) {
		if (!term.startsWith(Utils.MINUS)) {
			term = Utils.PLUS + term;
		}		
		term = term.replaceAll(Utils.MINUS, TMP_SYMBOL_HOLDER);
		term = term.replaceAll(Utils.REGEX_PLUS, Utils.MINUS);
		term = term.replaceAll(TMP_SYMBOL_HOLDER, Utils.PLUS);
		
		return term;
	}

	/**
	 * main 関数
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("Please set the input file path for the first parameter");
			return;
		}
		ExpressionCalculator expressionCalculator = new ExpressionCalculator(args[0]);
		expressionCalculator.run();
	}
}
