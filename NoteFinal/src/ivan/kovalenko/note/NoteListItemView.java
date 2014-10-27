package ivan.kovalenko.note;

import ivan.kovalenko.notenote.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class NoteListItemView extends TextView {
	
	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;
	
	public NoteListItemView(Context context, AttributeSet ats, int ds) {
		super(context, ats, ds);
		init();
	}

	public NoteListItemView(Context context) {
		super(context);
		init();
	}

	public NoteListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		// �������� ������ �� ������� ��������
		Resources myResources = getResources();
		// ������� ����� ��� ���������
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.notepad_margin));
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.notepad_lines));
		// �������� ���� ���� ��� ����� � ������ ������
		paperColor = myResources.getColor(R.color.notepad_paper);
		margin = myResources.getDimension(R.dimen.notepad_margin);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// ������� ���� �����
		canvas.drawColor(paperColor);
		// ������ ������
		canvas.drawLine(margin, 0, margin, getMeasuredHeight(), marginPaint);
		// ���������� ����� � ������� �� ������
		canvas.save();
		canvas.translate(margin, 0);
		// ���������� TextView ��� ������ ������
		super.onDraw(canvas);
		canvas.restore();
	}
}