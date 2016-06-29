package expressioncalculator.src;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.io.Files;

/**
 * 問題2
 * 
 * @author jyotaku
 * @since 2016.5.27
 *
 */
public class Utils {
	public final static String TIMES = "*";
	public final static String PLUS = "+";
	public final static String MINUS = "-";
	public final static String REGEX_PLUS = "\\+";
	public final static String L_BRACKET = "(";
	public final static String R_BRACKET = ")";
	public final static String SPLIT_WORD = " ";
	
	public final static String ANSWER_FOLDER_NAME = "answer";
	public final static String ANSWER_FILE_NAME_SUFFIX = "_answer";
	public final static String ANSWER_FILE_EXTENSION = ".txt";

	public static String createAnswerFolder(String inputFileName) {
		String answerFolder = getAnswerFloder(inputFileName);
		File newdir = new File(answerFolder);
		newdir.mkdir();
		return answerFolder;
	}

	public static String getAnswerFloder(String inputFileName) {
		Path outputFilePath = Paths.get(inputFileName);
		Path parent = outputFilePath.getParent();
		String anwserFolderPath = parent.toString() + File.separator + ANSWER_FOLDER_NAME;
		return anwserFolderPath;
	}

	public static String getOutputFileName(String answerFolderPath, String inputFileName) {
		String answerFileName = answerFolderPath + File.separator + Files.getNameWithoutExtension(inputFileName)
				+ ANSWER_FILE_NAME_SUFFIX + ANSWER_FILE_EXTENSION;

		return answerFileName;
	}

	/**
	 * ファイルの存在チェック
	 * 
	 * @return
	 */
	public static boolean fileExists(String inputFileName) {
		File file = new File(inputFileName);
		boolean result = file.exists();
		if (!result) {
			System.out.println("There is no such file '" + inputFileName + "'.");
		}
		return result;
	}

	/**
	 * 演算子かどうかをチェック
	 * @param operator
	 * @return
	 */
	public static boolean isOperator(String operator) {
		return TIMES.equals(operator) 
				|| PLUS.equals(operator) 
				|| MINUS.equals(operator) 
				|| L_BRACKET.equals(operator)
				|| R_BRACKET.equals(operator);
	}

	/**
	 * 演算子ではないことをチェック
	 * @param c
	 * @return
	 */
	public static boolean isNotOperator(char c) {
		String str = String.valueOf(c);
		return !isOperator(str);
	}

	/**
	 * 多項式を"+"で区切った配列を生成
	 * 
	 * @param targetStr
	 * @return
	 *   多項式の各項の配列
	 */
	public static String[] getPartsArray(String targetStr) {
		targetStr = targetStr.replaceAll(MINUS, PLUS + MINUS);
		if(targetStr.startsWith(PLUS)){
			targetStr = targetStr.substring(1);
		}
		return targetStr.split(REGEX_PLUS);
	}

	/**
	 * 数字と数字の四則演算
	 * 
	 * @param aStr
	 * @param bStr
	 * @param operator
	 * @return
	 */
	public static String calcNumber(String aStr, String bStr, String operator) {
		if (!NumberUtils.isNumber(aStr) || !NumberUtils.isNumber(bStr)) {
			return StringUtils.EMPTY;
		}
		long a = Long.parseLong(aStr);
		long b = Long.parseLong(bStr);
		switch (operator) {
		case PLUS:
			return String.valueOf(a + b);
		case MINUS:
			return String.valueOf(a - b);
		case TIMES:
			return String.valueOf(a * b);
		default:
			System.out.println("Unexpect Operator");
			return StringUtils.EMPTY;
		}
	}

}
