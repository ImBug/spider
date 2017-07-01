package oro.util.bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具
 * @author honghm
 *
 */
public class BeanUtil {
	
	public static HashMap<String,Class> getFieldMap(String classPath) throws ClassNotFoundException{
		Class cls = Class.forName(classPath);
		Field[] fieldlist = cls.getDeclaredFields();
		HashMap<String,Class> fieldHashMap=new HashMap<String,Class>(fieldlist.length);
		for (int i = 0; i < fieldlist.length; i++) {
			Field fld = fieldlist[i];
			fieldHashMap.put(fld.getName(), fld.getType());
		}
		return fieldHashMap;
  }
	
	/**
	 * 不考虑父类属性
	 * @param bean
	 * @param map
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setValue(Object bean,Map<String, Object> map) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException{
		for(Field field:bean.getClass().getDeclaredFields()){
			Object val = map.get(field.getName());
			if(val != null){
				field.setAccessible(true);
				if(field.getType().isAssignableFrom(val.getClass())){
					field.set(bean, val);
				}else{
					if(field.getType() == int.class){
						field.set(bean, Integer.valueOf(val.toString()));
					}else if(field.getType() == boolean.class){
						field.set(bean, Boolean.valueOf(val.toString()));
					}else if(field.getType() == long.class){
						field.set(bean, Long.valueOf(val.toString()));
					}
				}
			}
		}
	}
	
}
