package local.hal.st42.android.todo90349;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ToDoEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    /**
     * 新規登録モード化更新モードかを表すフィールド。
     */
    private int _mode = MainActivity.MODE_INSERT;

    /**
     * 更新モードの際、現在表示しているメモ情報のデータベース上の主キー値。
     */
    private long _idNo = 0;
    /**
     * データベースヘルパーオブジェクト。
     */
    private DatabaseHelper _helper;

    private Button btDeadLine;
    private Integer doneFlg = 0;
    private long ms = 0;

    private MenuItem menuItem;
    private boolean menuVisible = true;

    private Calendar _cal = Calendar.getInstance();
    private int _year = _cal.get(Calendar.YEAR);
    private int _month = _cal.get(Calendar.MONTH);
    private int _day = _cal.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);

        _helper = new DatabaseHelper(ToDoEditActivity.this);

//      ToolBarの設定
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

//      今日の日付を初期値として設定
        final DateFormat today = new SimpleDateFormat("yyyy年MM月dd日");
        final Date date = new Date(System.currentTimeMillis());
        btDeadLine = findViewById(R.id.bt_deadline);
        btDeadLine.setText(today.format(date));

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode", MainActivity.MODE_INSERT);

        if (_mode == MainActivity.MODE_INSERT) {
            TextView tvTitleEdit = findViewById(R.id.tvTaskEdit);
            tvTitleEdit.setText(R.string.tv_task_insert);
            changeVisible();
        } else {
//          編集画面の処理
            _idNo = intent.getLongExtra("idNo", 0);
            SQLiteDatabase db = _helper.getWritableDatabase();
            ToDo ToDoData = DataAccess.findByPK(db, _idNo);


//          タスク名
            EditText etInputName = findViewById(R.id.etInputName);
            etInputName.setText(ToDoData.getName());

//          期限
            btDeadLine = findViewById(R.id.bt_deadline);
            ms = ToDoData.getDeadLine();
//            Log.d("持ってきたms", deadlineMs);
            String longMs = msToString(ms);
            btDeadLine.setText(longMs);
            String[] result = longMs.split("[亜-煕]");
            _year = Integer.parseInt(result[0]);
            _month = Integer.parseInt(result[1]) - 1;
            _day = Integer.parseInt(result[2]);
//          タスク
            Switch swDone = findViewById(R.id.swDone);
            Integer resDone = ToDoData.getDone();
            Log.d("resDone", String.valueOf(resDone));
            if (resDone == 1) {
                swDone.setChecked(true);
            }

            EditText etInputNote = findViewById(R.id.etInputNote);
            etInputNote.setText(ToDoData.getNote());
        }
//         アクションバーに戻るを表示する
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //削除ボタン非表示
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItem = menu.findItem(R.id.option_menu_delete);
        menuItem.setVisible(menuVisible);
        return true;
    }

    //カレンダー表示
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _year = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        // CalendarにDatePickerDialogの内容を入れる
        Calendar ms_cal = Calendar.getInstance();
        ms_cal.set(_year, _month, _day);

        // Calendarからミリ秒を取得
        ms = ms_cal.getTimeInMillis();
        Log.d("ミリ秒", String.valueOf(ms));
        String str = msToString(ms);
        btDeadLine.setText(str);
    }

    //  カレンダー文字列変換
    private String msToString(long ms) {
        String str;
        Date date = new Date(ms);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        str = formatter.format(date);
        return str;
    }


    public void showDatePickerDialog(View v) {
        Bundle extras = new Bundle();
        extras.putLong("year", _year);
        extras.putLong("month", _month);
        extras.putLong("day", _day);
        DialogFragment newFragment = new DatePick();
        newFragment.setArguments(extras);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void changeVisible() {
        menuVisible = false;
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }


    //オプションメニューを表示
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_activity_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.option_menu_save:
                EditText etInputName = findViewById(R.id.etInputName);
//              タスク名
                String inputName = etInputName.getText().toString();
//              期限
                Long inputMs = ms;

                if (inputName.equals("")) {
                    Toast.makeText(ToDoEditActivity.this, R.string.msg_input_name, Toast.LENGTH_SHORT).show();
                } else if(inputMs.equals("")){
                    Toast.makeText(ToDoEditActivity.this, R.string.msg_input_ms, Toast.LENGTH_SHORT).show();
                }
                else {
                    EditText etInputNote = findViewById(R.id.etInputNote);
                    String inputNote = etInputNote.getText().toString();
                    Switch swDone = (Switch) findViewById(R.id.swDone);
//              スイッチがオンなら1にする
                    if (swDone.isChecked()) {
                        doneFlg = 1;
                    }
                    SQLiteDatabase db = _helper.getWritableDatabase();

                    if (_mode == MainActivity.MODE_INSERT) {
                        DataAccess.insert(db, inputName, inputMs, doneFlg, inputNote);
                    } else {
                        DataAccess.update(db, _idNo, inputName, inputMs, doneFlg, inputNote);
                    }
                    finish();
                    Log.d("タスク名", inputName);
                    Log.d("登録するミリ秒", String.valueOf(inputMs));
                    Log.d("フラグ", doneFlg.toString());
                    Log.d("タスク詳細", inputNote);
                }
                break;
            case R.id.option_menu_delete:
                Bundle extras = new Bundle();
                extras.putLong("idNo", _idNo);
                DeleteDialog deleteDialog = new DeleteDialog(_helper);
                deleteDialog.setArguments(extras);
                FragmentManager manager = getSupportFragmentManager();
                deleteDialog.show(manager, "Dialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}