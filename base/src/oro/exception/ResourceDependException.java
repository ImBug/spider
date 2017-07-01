package oro.exception;

/**
 * 资源依赖异常
 * @author honghm
 *
 */
public class ResourceDependException extends BaseException {
	
	private static final long serialVersionUID = -4175775976470945408L;
	
	private final static String FORMAT = "store中不存在[%s=%s]";
	
	public ResourceDependException(String message) {
		super(message);
	}

	public ResourceDependException(String code,String id) {
		super(String.format(FORMAT, code,id));
	}
	

}
