package local.hal.st42.android.todo90349;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteDialog extends DialogFragment {
    private DatabaseHelper _helper;
    private long idNo = 0;

    //コンストラクタ
    public DeleteDialog(DatabaseHelper helper) {
        _helper = helper;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity parent = getActivity();
        Bundle extras = getArguments();
        idNo = extras.getLong("idNo");
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(R.string.delete_check_title);
        builder.setMessage(R.string.delete_check_msg);
        builder.setPositiveButton(R.string.dlg_btn_ok, new DialogButtonClickListener());
        builder.setNegativeButton(R.string.dlg_btn_ng, new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * ダイアログのボタンが押された時の処理が記述されたメンバクラス。
     */
    private class DialogButtonClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //削除処理
                SQLiteDatabase db = _helper.getWritableDatabase();
                DataAccess.delete(db, idNo);
                getActivity().finish();
            }
        }
    }
}
