package oro.util.xml;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 提供一些XML工具类
 */
public class XMLUtil {
	
	/**
	 * 加载一个指定的XML文件
	 * @param filename
	 * @return
	 * @throws DocumentException 
	 */
	public static Document parseXmlFile(String filename) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		return saxReader.read(new File(filename));
	}
	
	public static Document readFromClassPath(String filename) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		return saxReader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
	}

	/**
	 * 检查并返回一个XML节点下是否有一个指定的元素，如果没有则弹出异常
	 * @param node
	 * @param name
	 * @return
	 */
	public static Element checkElement(Element element, String name) {
		Element result = element.element(name);
		if (result == null)
			throw new IllegalArgumentException(String.format("XML解析错误，节点[%s]必须包含子元素[%s]", element.getName(), name));
		return result;
	}

	/**
	 * 检查并返回一个XML元素上的属性文本值，如果属性不存在则弹出异常
	 * @param element
	 * @param name
	 * @return
	 */
	public static String checkAttrString(Element element, String name) {
		String temp = element.attributeValue(name);
		if (temp == null)
			throw new IllegalArgumentException(String.format("XML解析错误，元素[%s]必须包含属性[%s]", element.getName(), name));
		return temp;
	}

	/**
	 * 获取一个XML元素布尔值，如果属性不存在或解析失败则返回defaultValue
	 */
	public static boolean getAttrBoolean(Element element, String name, boolean defaultValue) {
		String temp = element.attributeValue(name);
		if (temp == null || temp.length() == 0)
			return defaultValue;
		try {			
			if (temp.equalsIgnoreCase("true"))
				return true;
			else if (temp.equalsIgnoreCase("false"))
				return false;
			else
				throw new IllegalArgumentException("值不是true或false");
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 获取一个XML元素的子元素列表
	 * @param task
	 * @param string
	 * @return
	 */
	public static Element[] getElements(Element task, String name) {
		List<?> result = task.elements(name);
		return result.toArray(new Element[0]);
	}

	/**
	 * 为元素设置一个属性，如果value为null，则设置为0长度字符串
	 * @param task
	 * @param name
	 * @param value
	 */
	public static void setAttribute(Element task, String name, String value) {		
		task.add(DocumentFactory.getInstance().createAttribute(task, name, value == null ? "" : value));
	}
	
	/**
	 * 为元素设置一个布尔属性
	 * @param task
	 * @param name
	 * @param value
	 */
	public static void setAttribute(Element task, String name, boolean value) {
		task.add(DocumentFactory.getInstance().createAttribute(task, name, Boolean.toString(value)));
	}
	
	/**
	 * 为元素设置一个整形属性
	 * @param task
	 * @param name
	 * @param value
	 */
	public static void setAttribute(Element task, String name, int value) {
		task.add(DocumentFactory.getInstance().createAttribute(task, name, Integer.toString(value)));
	}

	/**
	 * 为元素设置一个字符串数组
	 * @param task
	 * @param name
	 * @param values
	 */
	public static void setAttribute(Element task, String name, String[] values) {
		StringBuffer sb = new StringBuffer();
		for (String value : values) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(value);
		}
		setAttribute(task, name, sb.toString());
	}
	
	public static Map<String,String> parseAttributeMap(Element e){
		List<Attribute> list = e.attributes();
		Map<String,String> map = new HashMap(e.attributeCount());
		for(Attribute attr:list){
			map.put(attr.getName(), attr.getValue());
		}
		return map;
	}
}
