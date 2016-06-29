package expressioncalculator.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.io.Files;

import expressioncalculator.src.ExpressionCalculator;
import expressioncalculator.src.Utils;

public class ExpressionCalculatorTest {
	public static void main(String[] args) {

		String dir = System.getProperty("user.dir");
		String path = dir + File.separator + "testData";
		String inputFileName = "";
		// Create answer file
		File file = new File(path);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				File questionFile = files[i];
				if (questionFile.isDirectory() || ".DS_Store".equals(questionFile.getName())
						|| !questionFile.getName().contains("input")) {
					continue;
				}
				inputFileName = questionFile.getName();
				
				// check answer
				ExpressionCalculator expressionCalculator = new ExpressionCalculator(questionFile.getPath());
				expressionCalculator.run();
			} catch (Exception e) {
				System.out.println("Error file: " + inputFileName);
				e.printStackTrace();
			}
		}

		System.out.println("---- " + ExpressionCalculatorTest.class.getSimpleName() + " Start ----");
		System.out.println("");
		compareAnswer(path);

		System.out.println("---- " + ExpressionCalculatorTest.class.getSimpleName() + " End ----");
	}

	private static void compareAnswer(String testDatafolder) {
		String answerFileFolder = testDatafolder + File.separator + Utils.ANSWER_FOLDER_NAME + File.separator;
		String standardAnswerFolder = testDatafolder + File.separator + "standardAnswer";

		File file = new File(standardAnswerFolder);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File standardAnswerFile = files[i];
			String standardAnswerFilePath = standardAnswerFile.getPath();

			System.out.println("------ Check " + Files.getNameWithoutExtension(standardAnswerFilePath) + " ------");

			String outputAnswerFileName = Files
					.getNameWithoutExtension(standardAnswerFile.getName().replace("output", "input"))
					+ Utils.ANSWER_FILE_NAME_SUFFIX + Utils.ANSWER_FILE_EXTENSION;

			File outputFile = new File(answerFileFolder + outputAnswerFileName);
			if (!outputFile.exists()) {
				System.out.println("「" + outputAnswerFileName + "」解答ファイルが存在しません。");
				System.out.println("");
				continue;
			}
			outputAnswerFileName = answerFileFolder + outputAnswerFileName;
			try (FileReader sfr = new FileReader(standardAnswerFilePath);
					BufferedReader sbr = new BufferedReader(sfr);
					FileReader ofr = new FileReader(outputAnswerFileName);
					BufferedReader obr = new BufferedReader(ofr);) {
				List<String> standardAnswerList = sbr.lines().collect(Collectors.toList());
				List<String> outputAnswerList = obr.lines().collect(Collectors.toList());
				if (standardAnswerList.size() != outputAnswerList.size()) {
					System.out.println("解答の数が一致しない");
				}
				boolean isAllCorrect = standardAnswerList.toString().equals(outputAnswerList.toString());
				if (isAllCorrect) {
					System.out.println("Good Job !");
				} else {
					Optional<String> maxLengthStr = standardAnswerList.stream()
							.max(Comparator.comparingInt(String::length));
					int maxLength = maxLengthStr.get().length();
					maxLength = maxLength < 10 ? 10 : maxLength;
					for (int j = 0; j < standardAnswerList.size(); j++) {
						String standardAnswer = standardAnswerList.get(j);
						String outputAnswer = "";
						if (j < outputAnswerList.size()) {
							outputAnswer = outputAnswerList.get(j);
						}
						String outputStr = Strings.padStart(standardAnswer, maxLength, ' ')
								+ "  " + Strings.padStart(outputAnswer, maxLength, ' ');

						outputStr = (standardAnswer.equals(outputAnswer) ? "O" : "X") + "  " + outputStr;
						System.out.println(outputStr);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				System.out.println("");
			}
		}
	}
}
