package ivan.kovalenko.api;

import android.content.Context;
import android.widget.Toast;

public final class APIUtils {
	private APIUtils(){
		
	}

	public static void ToastException(Context context, APIException e) {
		e.printStackTrace();
		switch (e.getTypesOfExceptions()) {
		case ERROR_INTERNET:
			Toast.makeText(context, "Problems with internet conection", Toast.LENGTH_SHORT).show();
			break;

		case ERROR_SERVER:
			Toast.makeText(context, "Problems with server", Toast.LENGTH_SHORT).show();
			break;

		case ERROR_JSON:
			Toast.makeText(context, "Problems with JSON response", Toast.LENGTH_SHORT).show();
			break;
			
		default:
			Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}
