package ivan.kovalenko.note;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.api.APIUsers.DeleteNoteResponse;
import ivan.kovalenko.database.Provider;
import ivan.kovalenko.database.NoteDatabaseColumns.TableNote;
import ivan.kovalenko.login.MainActivity;
import ivan.kovalenko.notenote.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

public class NoteDeleteDialogFragment extends DialogFragment implements OnClickListener, LoaderCallbacks<DeleteNoteResponse>{

	APIUsers API = new APIUsers();
	Bundle bundle;
	long ID;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		bundle = this.getArguments();
		ID = bundle.getLong(NoteActivity.NOTE_ID);
		super.onCreate(savedInstanceState);
		
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
 
		AlertDialog.Builder abd = new AlertDialog.Builder(getActivity())
		.setTitle("DELETE NOTE")
		.setPositiveButton("YES", this)
		.setNegativeButton("NO", this)		
		.setMessage("NOTE DELETE");
		
		return abd.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case Dialog.BUTTON_NEGATIVE:
			break;
		case Dialog.BUTTON_POSITIVE:
			bundle = new Bundle();
			bundle.putLong("ID", ID);
			bundle.putString("ST", ((MyApplication)getActivity().getApplication()).getLocalData().getSessionId());
	
			getActivity().getLoaderManager().initLoader(1, bundle, this).forceLoad();
			break;
		}
	}
	
	public static class DeleteNote extends AsyncTaskLoader<DeleteNoteResponse>{
		String sessionID;
		long noteID;
		APIException apiexception;
		
		public DeleteNote(Context context, String sessionID, long noteID) {
			super(context);
			this.sessionID = sessionID;
			this.noteID    = noteID;
		}

		@Override
		public DeleteNoteResponse loadInBackground() {
			try {
				DeleteNoteResponse data = new APIUsers().getDeleteNote(sessionID, noteID);
				return data;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
		
	}

	APIException apiexception;
	DialogFragment Loader;
	
	@Override
	public Loader<DeleteNoteResponse> onCreateLoader(int id, Bundle args) {
		Loader = new DialogFragment();
		Loader.show(getFragmentManager(), "LOADER");
		Loader.setCancelable(false);
		return new DeleteNote(getActivity(),args.getString("ST"), args.getLong("ID"));
	}
	
	@Override
	public void onLoadFinished(Loader<DeleteNoteResponse> loader, DeleteNoteResponse data) {
		if(data == null){
			APIUtils.ToastException(getActivity(), apiexception);
		}else{
			switch(data.GetNoteResponse()) {
				case 0:		
					
					Log.d("DEBUG", "note id = " + ID);
					int count = Loader.getActivity().getContentResolver().delete(Provider.URI_NOTE, TableNote._ID + "=" + ID, null);											
					Log.d("DEBUG", "deleted rows = " + count);
					break;
				case 2:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.edit_note_problem));
				break;
				default:
					MainActivity.showToast(getActivity(), getResources().getString(R.string.exception));
				break;
			}
			Loader.dismissAllowingStateLoss();
		}
	}
	@Override
	public void onLoaderReset(Loader<DeleteNoteResponse> loader) {
	}
}