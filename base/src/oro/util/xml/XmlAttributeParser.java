package oro.util.xml;


import java.util.ArrayList;
import java.util.List;

public class XmlAttributeParser {
  
  /*
   * 属性值的数据类型
   */
  private String dataType;
  
  /*
   * 属性的值个数，通常为数组的长度
   */
  private String length;
  
  /*
   * 属性值
   */
  private String value;
  
  public XmlAttributeParser() {
  }
  
  public XmlAttributeParser(String strVal) {
    
    String[] val = parseValue(strVal);
    if (val != null && val[1] != null) {
      this.dataType = val[0];
      
      String val2 = val[1];
      if (val2.length() > 2 && val2.startsWith("(") && val2.endsWith(")")) {
        val2 = val2.substring(1, val2.length() - 1);
      }
      
      val = parseValue(val2);
      if (val != null && val[1] != null) {
        this.length = val[0];
        this.value = val[1];
      } else {
        this.value = val[0];
      }
    }
    
  }

  private String[] parseValue(String attrVal) {
    if (attrVal == null || attrVal.length() == 0) {
      return null;
    }
    
    String[] valPair = new String[2];
    int index = attrVal.indexOf(":");
    
    if (index > -1) {
      valPair[0] = attrVal.substring(0, index);
      valPair[1] = attrVal.substring(index + 1, attrVal.length());
    } else {
      valPair[0] = attrVal;
      valPair[1] = null;
    }
    
    return valPair;
  }
  
  public String toString() {
    return "dataType:" + dataType + " length:" + length + " value:" + value;
  }
  
  public String getDataType() {
    return dataType;
  }

  public String getLength() {
    return length;
  }

  public String getValue() {
    return value;
  }
  
  /**
   * 采用特定的分隔符","分割字符串，返回字符数组
   * @param defaultVal 当分分割结果数组长度小于预期(length属性决定)时，默认添加的字符串值
   * @return 字符串数组，永远不为null
   */
  public String[] getValueArray(String defaultVal) {
    return getValueArray(',', defaultVal);
  }
  
  public String[] getValueArray(char delimiter, String defaultVal) {
    if (value == null) {
      return new String[0];
    }
    
    defaultVal = defaultVal == null ? "" : defaultVal;
    String[] values = split(value, delimiter, defaultVal);
    List<String> result = new ArrayList<String>();
    int expectedLen = (length == null || "".equals(length)) ? 0 : Integer.parseInt(length);

    for (int i = 0; i < expectedLen; i++) {
      if (i < values.length) {
        if (values[i] == null || "".equals(values[i])) {
          result.add(defaultVal);
        } else {
          result.add(values[i]);
        }
        
      //补充末尾的数据
      } else {
        result.add(defaultVal);
      }
    }
    
    return result.toArray(new String[expectedLen]);
  }
  
  public static String[] split(String str, char delimiter, String defaultVal) {
    List<String> result = new ArrayList<String>();
    char[] chars = str.toCharArray();
    int index = 0;
    boolean inBlock = false;
    boolean usedInBlock = false;
    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      if (c == delimiter && !inBlock) {
        if (index == i) {
          result.add(defaultVal);
        } else if (index < i) {
          if (usedInBlock) {
            usedInBlock = false;
            result.add(str.substring(index, i).replaceAll("\"", ""));
          } else {
            result.add(str.substring(index, i));
          }
        }
        
        index = i + 1; //跳过分割符
        continue;
      }
      
      //双引号
      if (c == '"') {
        inBlock = !inBlock;
        usedInBlock = true;
      }
    }
    
    result.add(str.substring(index, str.length()));
    
    if (chars[chars.length - 1] == delimiter) {
      result.add(defaultVal);
    }
    
    return result.toArray(new String[result.size()]);
  }
}
