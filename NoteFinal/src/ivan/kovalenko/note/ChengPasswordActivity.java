package ivan.kovalenko.note;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUsers.ChengPasswordResponse;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.login.MainActivity;
import ivan.kovalenko.notenote.R;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChengPasswordActivity extends Activity implements OnClickListener, LoaderCallbacks<ChengPasswordResponse> {
	private EditText  	   oldPassword;
	private EditText  	   newPassword;
	private EditText 	   reenterNewPassword;
	private Intent    	   intent;
	private Button    	   enterButton;
	private Bundle    	   bundle;
	private String [] 	   value;
	
	private final String STRING_KEY = "String";
	private final int    LOADER_ID  = 0;
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		reenterNewPassword = (EditText) findViewById(R.id.reenterPassword);

		enterButton = (Button) findViewById(R.id.buttonEnterChenge);
		enterButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final String SESSION_ID = ((MyApplication) getApplication()).getLocalData().getSessionId();
		final String OLD_PASSWORD = oldPassword.getText().toString();
		final String NEW_PASSWORD = newPassword.getText().toString();
		final String REENTER_NEW_PASSWORD = reenterNewPassword.getText().toString();

		if (!NEW_PASSWORD.equals(REENTER_NEW_PASSWORD)) {
			MainActivity.showToast(this,getResources().getString(R.string.registration_fragment_do_not_match));
		} else {
			bundle = new Bundle();
			value = new String[3];
			value[0] = SESSION_ID;
			value[1] = OLD_PASSWORD;
			value[2] = NEW_PASSWORD;
			
			bundle.putStringArray(STRING_KEY, value);
			
			getLoaderManager().initLoader(LOADER_ID, bundle, this).forceLoad();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public static class ChangePassword extends AsyncTaskLoader<ChengPasswordResponse>{
		protected APIException apiexception;
		String [] value;		
		public ChangePassword(Context context, String [] value) {
			super(context);
			this.value = value;
		}

		@Override
		public ChengPasswordResponse loadInBackground() {

			try {
				return  new APIUsers().chengPassword(value[0], value[2], value[1]);
						
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	
	
	@Override
	public Loader<ChengPasswordResponse> onCreateLoader(int id, Bundle args) {
		oldPassword.setEnabled(false);
		newPassword.setEnabled(false);
		reenterNewPassword.setEnabled(false);
		enterButton.setEnabled(false);		
		return new ChangePassword(ChengPasswordActivity.this, args.getStringArray(STRING_KEY));
	}

	@Override
	public void onLoadFinished(Loader<ChengPasswordResponse> Loader, ChengPasswordResponse data) {
		APIException apiexception = null;
		oldPassword.setEnabled(true);
		newPassword.setEnabled(true);
		reenterNewPassword.setEnabled(true);
		enterButton.setEnabled(true);

		if (data == null) {
			APIUtils.ToastException(ChengPasswordActivity.this, apiexception);
		} else {
			switch (data.getChengPasswordResponse()) {
			case 0:
				MainActivity.showToast(ChengPasswordActivity.this,getResources().getString(R.string.cheng_password_activity_cheng_complite));
				// clear all activity 
				intent = new Intent(ChengPasswordActivity.this,MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case 1:
				MainActivity.showToast(ChengPasswordActivity.this,getResources().getString(R.string.cheng_password_activity_session_id_problem));
				break;
			case 2:
				MainActivity.showToast(ChengPasswordActivity.this,getResources().getString(R.string.cheng_password_activity_old_password));
				break;
			default:
				MainActivity.showToast(ChengPasswordActivity.this, getResources().getString(R.string.exception));
				break;
			}
		}	
	}

	@Override
	public void onLoaderReset(Loader<ChengPasswordResponse> loader) {
	}
}