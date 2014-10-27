package ivan.kovalenko.login;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.api.APIUsers.LoginResponse;
import ivan.kovalenko.note.NoteActivity;
import ivan.kovalenko.notenote.R;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment implements OnClickListener {

	protected class ViewHolder {

		private EditText login;
		private EditText password;
		// Временная кнопка
		private Button demo;
		private Button enterButton;

		public ViewHolder(View view) {

			login       = (EditText) view.findViewById(R.id.login_LoginEditText);
			password    = (EditText) view.findViewById(R.id.login_PasswordEditText);

			enterButton = (Button) view.findViewById(R.id.login_ButtonEnter);
			demo        = (Button) view.findViewById(R.id.button1);
		}
	}
	private static final int LOGIN_ASYNCTASKLOADER  = 0;
	
	private static final String PREF_SETTINGS_LOGIN = "Login";
	private static final String LOGIN_KEY           = "LOGIN";
	private static final String PASSWORD_KEY        = "PASSWORD";
	private static final String DEFOLT_VALUE        = ""; 
	
	protected ViewHolder h;
	protected APIUsers API;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance) {
		ViewGroup roorView = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);
		return roorView;
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState) {
		super.onViewCreated(view, saveInstanceState);
		
		h = new ViewHolder(view);
		h.enterButton.setOnClickListener(this);
		
		h.demo.setOnClickListener(this);
		
		if (saveInstanceState == null) {
			getLastLogin();
		}
		
		if (!TextUtils.isEmpty(h.login.getText())) {
			// смена фокуса на поле "Password"
			h.password.requestFocus();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("DEBUG", "onStart");
		if(getLoaderManager().getLoader(LOGIN_ASYNCTASKLOADER) != null){
			h.login.setEnabled(false);
			h.password.setEnabled(false);
			h.enterButton.setEnabled(false);
			Log.d("DEBUG", "initLoader");
			
			Log.d("DEBUG", "loader.isReset=" + getLoaderManager().getLoader(LOGIN_ASYNCTASKLOADER).isReset());			
			Log.d("DEBUG", "loader.isStarted=" + getLoaderManager().getLoader(LOGIN_ASYNCTASKLOADER).isStarted());
			
			getLoaderManager().initLoader(LOGIN_ASYNCTASKLOADER, null, LoginAsyncTaskCallbacks);
			
//			Log.d("DEBUG", "loader.isReset=" + getLoaderManager().getLoader(LOGIN_ASYNCTASKLOADER).isReset());			
//			Log.d("DEBUG", "loader.isStarted=" + getLoaderManager().getLoader(LOGIN_ASYNCTASKLOADER).isStarted());
			
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		API = new APIUsers();
	}
	
	@Override
	public void onClick(View v) {
		final String LOGIN    = h.login.getText().toString();
		final String PASSWORD = h.password.getText().toString();
		final Bundle BUNDLE = new Bundle();
		BUNDLE.putString(LOGIN_KEY, LOGIN);
		BUNDLE.putString(PASSWORD_KEY, PASSWORD);
		
		switch (v.getId()) {
			case R.id.button1:		
				BUNDLE.putString(LOGIN_KEY, "demo");
				BUNDLE.putString(PASSWORD_KEY, "demo");
				Log.d("DEBUG", "initLoader.forceLoad");
				getLoaderManager().initLoader(LOGIN_ASYNCTASKLOADER, BUNDLE, LoginAsyncTaskCallbacks).forceLoad();
				break;
			case R.id.login_ButtonEnter:
				if (TextUtils.isEmpty(LOGIN) || TextUtils.isEmpty(PASSWORD)) {
					if(TextUtils.isEmpty(LOGIN)){
						MainActivity.showToast(getActivity(), getResources().getString(R.string.login_fragment_login_toast));
					}
					else{
						MainActivity.showToast(getActivity(), getResources().getString(R.string.login_fragment_password_toast));
					}
				} else {
					saveLastLogin();
					getLoaderManager().initLoader(1, BUNDLE, LoginAsyncTaskCallbacks).forceLoad();
				}
				break;
		}
	}

	private void saveLastLogin() {
		final String LOGIN = h.login.getText().toString();
		
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_SETTINGS_LOGIN, Context.MODE_PRIVATE).edit();
		editor.putString(LOGIN_KEY, LOGIN);
		editor.commit();
	}

	private void getLastLogin() {
		SharedPreferences preferense = getActivity().getSharedPreferences(PREF_SETTINGS_LOGIN, Context.MODE_PRIVATE);
		String lastLogin 			 = preferense.getString(LOGIN_KEY, DEFOLT_VALUE);
		h.login.setText(lastLogin);
	}
	
	public static class LoginAsyncTask extends AsyncTaskLoader<LoginResponse>{
		String login;
		String password; 
		APIException apiexception;
		LoginResponse response;
		
		public LoginAsyncTask(Context context, String password, String login) {
			super(context);
			this.login    = login;
			this.password = password;
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();
			if(response != null || apiexception != null) {
				deliverResult(response);
			}
		}
		
		@Override
		public LoginResponse loadInBackground() {
			try {
				LoginResponse loginResponse = new APIUsers().login(login, password);
				return response = loginResponse;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
		
		public APIException getAPIException(){
			return apiexception;
		}
	} 
	
	LoaderCallbacks<LoginResponse> LoginAsyncTaskCallbacks = new LoaderCallbacks<APIUsers.LoginResponse>() {
		
		@Override
		public Loader<LoginResponse> onCreateLoader(int id, Bundle args) {
			Log.d("DEBUG", "onCreateLoader");
			
			h.login.setEnabled(false);
			h.password.setEnabled(false);
			h.enterButton.setEnabled(false);
			 
			return new LoginAsyncTask(getActivity().getApplicationContext(), args.getString(PASSWORD_KEY), args.getString(LOGIN_KEY));
		}

		
		@Override
		public void onLoadFinished(Loader<LoginResponse> loader, LoginResponse data) {
			Log.d("DEBUG", "onLoadFinished");
			h.login.setEnabled(true);
			h.password.setEnabled(true);
			h.enterButton.setEnabled(true);
			
			if (data == null) {
				//Если класс преналежит родителю. 
				if(loader instanceof LoginAsyncTask){
					APIUtils.ToastException(getActivity(), ((LoginAsyncTask)loader).getAPIException());
				}
				
			} else {
				switch (data.getUserCreate()) {
				case 0:
					((MyApplication) getActivity().getApplication()).getLocalData().setSessionId(data.getSessionID());
					Intent intent = new Intent(getActivity(),NoteActivity.class);
					startActivity(intent);
					break;
				case 1:
					MainActivity.showToast(getActivity(),getResources().getString(R.string.login_fragment_please_register));
					break;
				default:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.exception));
					break;
				}
			}
			getLoaderManager().destroyLoader(LOGIN_ASYNCTASKLOADER);
		}

		@Override
		public void onLoaderReset(Loader<LoginResponse> loader) {
			Log.d("DEBUG", "onLoadReset");
			getLoaderManager().destroyLoader(LOGIN_ASYNCTASKLOADER);
		}
	};	
}