package oro.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * 自定义对象与JSON解析配置
 */
public class CustomObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 1L;

	public CustomObjectMapper() {
		SimpleModule module = new SimpleModule("RestModule");
		this.registerModule(module);
		this.configure(SerializationFeature.INDENT_OUTPUT, true);
		this.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		this.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
		this.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false);
		this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
	
	public static String encodeJson(Object obj)throws RuntimeException {
		if (obj == null)return null;
		try {
			return new CustomObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException("无法序列化对象为json格式：" + obj, e);
		}		
	}
	
	public static  <T> T decodeJson(String json, Class<T> cls) {
		if (json == null)return null;
		try {
			return new CustomObjectMapper().readValue(json, cls);
		} catch (Throwable e) {
			throw new RuntimeException("无法反序列化为对象：" + json, e);
		}
	}
}