package ivan.kovalenko.api;

public class APIException extends Exception {
	private static final long serialVersionUID = -2365644721531004797L;

	public enum TypeOfException {
		ERROR_INTERNET, ERROR_SERVER, ERROR_JSON, ERROR_UNKNOWN
	}

	final TypeOfException typeOfException;
	
	public TypeOfException getTypesOfExceptions() {
		return typeOfException;
	}
	
	public APIException(TypeOfException _tTypeOfException, Throwable _throwable){
		super(_throwable);
		typeOfException = _tTypeOfException;
	}
}
