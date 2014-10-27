package ivan.kovalenko.login;

import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUsers.RegistrResponse;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.notenote.R;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationFragment extends Fragment implements View.OnClickListener {
	
	protected class ViewHolder {
		public EditText login           = null;
		public EditText password        = null;
		public EditText reenretPassword = null;

		public Button enterButton;

		public ViewHolder(View view) {

			login           = (EditText) view.findViewById(R.id.registration_LoginEditText);
			password 		= (EditText) view.findViewById(R.id.registration_PasswordEditText);
			reenretPassword = (EditText) view.findViewById(R.id.registration_ReenterPasswordEditText);

			enterButton = (Button) view.findViewById(R.id.registration_ButtonEnter);
		}
	}
	
	protected ViewHolder h;
	protected APIUsers API;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance) {
		return inflater.inflate(R.layout.registration_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveInstanceState) {
		super.onViewCreated(view, saveInstanceState);
		h = new ViewHolder(view);
		h.enterButton.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		API = new APIUsers();
	}

	@Override
	public void onClick(View v) {
		final String LOGIN           = h.login.getText().toString();
		final String PASSWORD        = h.password.getText().toString();
		final String REENTERPASSWORD = h.reenretPassword.getText().toString();
		
		if (!TextUtils.isEmpty(LOGIN) && !TextUtils.isEmpty(PASSWORD) && !TextUtils.isEmpty(REENTERPASSWORD) && PASSWORD.equals(REENTERPASSWORD)) {
			new MyAsyncTask().execute(new ÑheckUser(LOGIN, PASSWORD));
		}
		else {
			MainActivity.showToast(getActivity(), getResources().getString(R.string.registration_fragment_do_not_match));
		}
	}
	
	public class ÑheckUser{
		private String login;
		private String password;
		
		ÑheckUser(String _login, String _password){
			login = _login;
			password = _password;
		}
		 
		public String getLogin(){
			return login;
		}
		
		public String getPassword(){
			return password;
		}
	}
	
	public class MyAsyncTask extends AsyncTask<ÑheckUser, Void, RegistrResponse> {
		APIException apiexception;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			h.login.setEnabled(false);
			h.password.setEnabled(false);
			h.reenretPassword.setEnabled(false);
			h.enterButton.setEnabled(false);
		}

		@Override
		protected RegistrResponse doInBackground(ÑheckUser... params) {
			
			try {
				return API.regist(params[0].getLogin(), params[0].getPassword());
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(RegistrResponse result) {
			super.onPostExecute(result);
			
			h.login.setEnabled(true);
			h.password.setEnabled(true);
			h.reenretPassword.setEnabled(true);
			h.enterButton.setEnabled(true);
			
			
			
			if (result == null) {
				APIUtils.ToastException(getActivity(), apiexception);
			} else {
				switch (result.getUserCreate()) {
				case 0:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.registration_fragment_new_account));
					getActivity().getActionBar().setSelectedNavigationItem(0);
					break;
				case 1:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.registration_fragment_same_user));
					break;
				default:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.exception));
					break;
				}
			}
		}
	}
}