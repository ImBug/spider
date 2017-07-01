package oro.util.number;

public class NumberUtil {
	
	/**
	 * 
	 * @param value 原始值
	 * @param format 以运算符开始
	 * @return
	 */
	public static double calVal(String value,String format) throws Exception{
		double val = getNumberOfStr(value);
		if(format != null && format.trim().length() > 0){
			StringBuffer n = new StringBuffer();
			char oper = '0';
			String f = format + '+';
			for(int i=0; i<f.length(); i++){
				char c = f.charAt(i);
				if((c >='0' && c <= '9') || c == '.'){
					n.append(c);
				}else{
					switch (oper) {
					case '+': {
						val += Double.valueOf(n.toString());
						break;
					}
					case '-': {
						val -= Double.valueOf(n.toString());
						break;
					}
					case '*': {
						val *= Double.valueOf(n.toString());
						break;
					}
					case '/': {
						val /= Double.valueOf(n.toString());
						break;
					}
					default:
						break;
					}
					oper = c;
					n = new StringBuffer();
				}
				//遇到完整的数字做一次运算
			}
		}
		return val;
	}
	
	/**
	 * 提取字符串中的数字组在一起
	 * 最多一个小数点
	 * 不要处理过大的数
	 * @param str
	 * @return
	 */
	public static double getNumberOfStr(String str){
		if(str != null){
			String num = "";
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if((c >='0' && c <= '9') || c == '.'){
					num += c;
				}
			}
			if(num.length() > 0)return Double.valueOf(num);
		}
		return 0.0;
	}
	
	/**
	 * 遇到第一个end 符号结束
	 * @param str
	 * @param end
	 * @return
	 */
	public static String fetchNumberFrom(String str,char end){
		if(str != null){
			StringBuffer num = new StringBuffer();
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(c == end && num.length() > 0){//如果没提取到数字也不受end约束
					break;
				}
				if(c >='0' && c <= '9'){
					num.append(c);
				}
			}
			return num.toString();
		}
		return null;
	}

	public static String fetchNumberFrom(String str){
		if(str != null){
			StringBuffer num = new StringBuffer();
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(c >='0' && c <= '9'){
					num.append(c);
				}
			}
			return num.toString();
		}
		return null;
	}
	
	public static String fetchIpFrom(String str){
		if(str != null){
			StringBuffer num = new StringBuffer();
			for(int i=0; i<str.length(); i++){
				char c = str.charAt(i);
				if(num.length() > 0){//如果没提取到数字也不受end约束
					if(c == '.'){
						num.append(c);
						continue;
					}else if(c == ':' || c < '0' || c > '9'){
						break;
					}
				}
				if(c >='0' && c <= '9'){
					num.append(c);
				}
			}
			return num.toString();
		}
		return null;
	}
}
