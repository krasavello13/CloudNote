package ivan.kovalenko.note;

import ivan.kovalenko.notenote.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
 
 
public class NoteAdapter extends CursorAdapter {
	
	public NoteAdapter(Context context, Cursor c , boolean autoRequery) {
        super(context, c, autoRequery);
    }
 
	public interface OnItemDeleteListener {
		void onItemDelete(long id);
	}

	private OnItemDeleteListener mOnItemDeleteListener;

	public void setOnItemDeleteListener(OnItemDeleteListener listener) {
		mOnItemDeleteListener = listener;
	}

	private void invokeOnItemDeleteListener(long id) {
		if (mOnItemDeleteListener != null) {
			mOnItemDeleteListener.onItemDelete(id);
		}
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.list, parent, false);
		return retView;
    }
	
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
   
		TextView textViewNoteTitle = (TextView) view.findViewById(R.id.noteName);
		
		textViewNoteTitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

		TextView textViewNoteSubtitle = (TextView) view.findViewById(R.id.noteSubtitle);
		
		textViewNoteSubtitle.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));

		final long NOTE_ID = cursor.getLong(0);
		((Button) view.findViewById(R.id.buttonDelete)).setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				invokeOnItemDeleteListener(NOTE_ID);
			}
		});
    }	
} 
