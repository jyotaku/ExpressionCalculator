package expressioncalculator.src;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 問題2
 * 
 * @author jyotaku
 * @since 2016.5.27
 *
 */
public class SubTerm {
	
	// 項目の中の数字の部分 例：3ab → 3
	private long number;
	
	// 項目の中の未知数の部分 例：3ab → ab
	private String unknownStr;
	
	// 未知数の次数をカウントするマップ
	private Map<String, Integer> unknownDegreeCountMap;
	
	// 同じ項かどうかを判定するためのキー
	private String key;

	// 各未知数の次数の合計
	private int degreeAmount;
	
	// 次数抜き未知数　3a2b → ab
	private String unknownStrDistinct;

	// ファイル書き出し用文字列
	private String outputStr;

	public SubTerm(String originStr) {
		this.number = abstractNumber(originStr);
		this.unknownStr = abstractUnknownStr(originStr, this.number);
		this.unknownDegreeCountMap = abstractUnknowDegreeCountMap(originStr);
		this.unknownStrDistinct = abstracUnknowsStrDistinct(this.unknownDegreeCountMap);
		this.key = abstractKey(this.unknownDegreeCountMap);
	
		this.degreeAmount = abstractDegreeAmount(this.unknownDegreeCountMap);
		this.outputStr = abstractOutputStr(this.number, this.key);
	}

	private long abstractNumber(String originStr) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < originStr.length(); i++) {
			String str = String.valueOf(originStr.charAt(i));
				if (NumberUtils.isNumber(str) || "-".equals(str)) {
					sb.append(str);
				}
		}
		String rlt = sb.toString();
		if(Utils.MINUS.equals(rlt)){
			return -1;
		}else if(rlt.isEmpty()){
			return 1;
		}
		return Long.parseLong(sb.toString());		
	}
	
	private String abstractUnknownStr(String originStr, long number){
		if(originStr.startsWith(Utils.PLUS) || originStr.startsWith(Utils.MINUS)){
			originStr = originStr.substring(1);
		}
		for (int i = 0; i < originStr.length(); i++) {
			String str = String.valueOf(originStr.charAt(i));
				if (!NumberUtils.isNumber(str) 
						&& !Utils.MINUS.equals(str)
						&& !Utils.PLUS.equals(str)) {
					return originStr.substring(i);
				}
		}
		return StringUtils.EMPTY;
	}

	private Map<String, Integer> abstractUnknowDegreeCountMap(String originStr) {
		Set<String> rltSet = Sets.newHashSet();
		for (int i = 0; i < originStr.length(); i++) {
			String singleStr = String.valueOf(originStr.charAt(i));
			if (!NumberUtils.isNumber(singleStr) && !Utils.MINUS.equals(singleStr)) {
				rltSet.add(singleStr);
			}
		}
		Map<String, Integer> rltMap = Maps.newHashMap();
		for (String str : rltSet) {
			int degree = StringUtils.countMatches(originStr, str);
			rltMap.put(str, Integer.valueOf(degree));
		}
		return rltMap;
	}

	private String abstracUnknowsStrDistinct(Map<String, Integer> unknownDegreeCountMap){
		List<String> unknowList = unknownDegreeCountMap
				.keySet()
				.stream()
				.sorted()
				.collect(Collectors.toList());
		return StringUtils.join(unknowList, StringUtils.EMPTY);
	}
	
	private String abstractKey(Map<String, Integer> unknownDegreeCountMap) {
		List<String> unknowList = unknownDegreeCountMap.keySet()
				.stream()
				.sorted()
				.map(str -> {
					int degree = unknownDegreeCountMap.get(str).intValue();
					if(degree > 1){
						str = str + degree;
					}
					return str;
				})
				.collect(Collectors.toList());
		
		return StringUtils.join(unknowList, StringUtils.EMPTY);
	}

	private int abstractDegreeAmount(Map<String, Integer> unknownDegreeCountMap) {
		int amount = 0;
		for (Map.Entry<String, Integer> entry : unknownDegreeCountMap.entrySet()) {
			int degree = entry.getValue().intValue();
			amount += degree;
		}
		return amount;
	}

	private String abstractOutputStr(long number, String key) {
		if (number == 0) {
			return "";
		}
		
		if(key.isEmpty()){
			return String.valueOf(number);
		}
		
		String rlt = key;
		if (number == -1) {
			rlt = Utils.MINUS + rlt;
		} else if (number != 1) {
			rlt = number + rlt;
		}
		
		if (number > 0) {
			rlt = "+" + rlt;
		}
		return rlt;
	}
	
	public int getDegreeAmount() {
		return degreeAmount;
	}
	
	public String getUnknownStrDistinct() {
		return unknownStrDistinct;
	}

	public String getKey() {
		return key;
	}

	public String getOutputStr() {
		return outputStr;
	}

	public long getNumber() {
		return number;
	}
	
	public String getUnknownStr() {
		return unknownStr;
	}
	
	public void setNumber(long number){
		this.number = number;	
		this.outputStr = abstractOutputStr(this.number, this.key);
	}
	
	public String toString(){
		return outputStr;
	}
}
