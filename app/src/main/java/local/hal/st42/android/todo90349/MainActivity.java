package local.hal.st42.android.todo90349;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{


    /**
     * プレファレンスファイル名を表す定数フィールド。
     */
    private static final String PREFS_NAME = "PSPrefsFile";
    /**
     * 新規登録モードを表す定数フィールド。
     */
    static final int MODE_INSERT = 1;
    /**
     * 更新モードを表す定数フィールド。
     */
    static final int MODE_EDIT = 2;
    /**
     * メモリスト用ListView
     */
    private RecyclerView _rvTodoList;
    /**
     * データベースヘルパーオブジェクト。
     */
    private DatabaseHelper _helper;
    /**
     * 絞り込み機能の種類を表すフィールド。
     */
    private int _menuCategory;

    /**
     * すべてのタスク
     */
    private static final int ALL = 1;

    /**
     *未実施のタスク
     */
    private static final int NOT_DONE = 2;

    /**
     *実施済みのタスク
     */
    private static final int DONE = 3;

    /**
     * FAB
     */
    private FloatingActionButton addFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _helper = new DatabaseHelper(MainActivity.this);

//      ToolBarの設定
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbarLayout);
        toolbarLayout.setTitle(getString(R.string.tv_task_list));
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.LTGRAY);

//      プレファレンスオブジェクトを取得
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        _menuCategory = settings.getInt("searchType",ALL);

        // RecyclerViewを取得
        _rvTodoList = findViewById(R.id.rvTodoList);
        LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
        _rvTodoList.setLayoutManager(layout);
        DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this,layout.getOrientation());
        _rvTodoList.addItemDecoration(decoration);
        setNewCursor();


        /**
         * FABボタン動作
         */
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ToDoEditActivity.class);
                        intent.putExtra("mode", MODE_INSERT);
                        startActivity(intent);
                    }
                }
        );
    }

    /**
     * リサイクラービューで利用するビューホルダークラス。
     */
    private class TodoViewHolder extends RecyclerView.ViewHolder{
        /**
         * タイトル表示用TextViewフィールド。
         */
        public TextView _tvTitleRow;
        /**
         * 期限表示用TextViewフィールド。
         */
        public TextView _tvDeadLineRow;
        /**
         * チェックボックス用フィールド
         */
        public CheckBox _cbTaskCheckRow;

        /**
         * コンストラクタ。
         */
        public TodoViewHolder(View itemVIew){
            super(itemVIew);
            _tvTitleRow = itemVIew.findViewById(R.id.tvTitleRow);
            _tvDeadLineRow = itemVIew.findViewById(R.id.tvDeadLineRow);
            _cbTaskCheckRow = itemVIew.findViewById(R.id.cbTaskCheckRow);
        }
    }

    /**
     * リサイクラービューで使用するアダプタクラス。
     */
    private class TodoListAdapter extends RecyclerView.Adapter<TodoViewHolder>{
        /**
         * リストデータを表すフィールド
         */
        private Cursor _todoList;

        /**
         * コンストラクタ
         */
        public TodoListAdapter(Cursor todoList){
            _todoList = todoList;
        }

        @Override
        public TodoViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View row = inflater.inflate(R.layout.row, parent, false);
            row.setOnClickListener(new ListItemClickListener());
            TodoViewHolder holder = new TodoViewHolder(row);
            return holder;
        }

        @Override
        public void onBindViewHolder(TodoViewHolder holder, int position){
            _todoList.moveToPosition(position);
//          タイトルの取得
            int titleIdx = _todoList.getColumnIndex("name");
            String title = _todoList.getString(titleIdx);
            Log.d("とってきた値", title);
            holder._tvTitleRow.setText(title);
            int deadlineIdx = _todoList.getColumnIndex("deadline");
            Long deadline = _todoList.getLong(deadlineIdx);
            Date today = new Date();
            Date date = new Date(deadline);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String strDate = sdf.format(date);
            String strToday = sdf.format(today);
            if (strToday.equals(strDate)){
                holder._tvDeadLineRow.setText(R.string.deadline_today);
                holder._tvDeadLineRow.setTextColor(Color.parseColor("#fd7e00"));
            }else if(today.compareTo(date) > 0){
                holder._tvDeadLineRow.setText(strDate);
                holder._tvDeadLineRow.setTextColor(Color.RED);
            }else if (today.compareTo(date) < 0){
                holder._tvDeadLineRow.setText(getString(R.string.deadline)+strDate);
                holder._tvDeadLineRow.setTextColor(Color.BLUE);
            }
            int idIdx = _todoList.getColumnIndex("_id");
            long id = _todoList.getLong(idIdx);
            int doneNum = _todoList.getColumnIndex("done");
            int toDoCheck = _todoList.getInt(doneNum);
            boolean checked = false;
            if(toDoCheck == 1){
                checked = true;
            }
            holder._cbTaskCheckRow.setChecked(checked);
            holder._cbTaskCheckRow.setTag(id);
            holder._cbTaskCheckRow.setOnClickListener(new OnCheckBoxClickListener());
        }

        @Override
        public int getItemCount(){
            return _todoList.getCount();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNewCursor();
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
        inflater.inflate(R.menu.menu_options_activity_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuListOptionTitle = menu.findItem(R.id.menuListOptionTitle);
        switch(_menuCategory) {
            case ALL:
                menuListOptionTitle.setTitle(R.string.all);
                break;
            case NOT_DONE:
                menuListOptionTitle.setTitle(R.string.not_done);
                break;
            case DONE:
                menuListOptionTitle.setTitle(R.string.done);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        boolean returnVal = true;
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.fabAdd:
                Intent intent = new Intent(MainActivity.this, ToDoEditActivity.class);
                intent.putExtra("mode", MODE_INSERT);
                startActivity(intent);
                break;
            case R.id.menuListOptionAll:
                _menuCategory = ALL;
                editor.putInt("searchType",ALL);
                setNewCursor();
                break;
            case R.id.menuListOptionNotDone:
                _menuCategory = NOT_DONE;
                editor.putInt("searchType",NOT_DONE);
                setNewCursor();
                break;
            case R.id.menuListOptionDone:
                _menuCategory = DONE;
                editor.putInt("searchType",DONE);
                setNewCursor();
                break;
            default:
                returnVal = super.onOptionsItemSelected(item);
                break;
        }
        editor.apply();
        if (returnVal){
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * リストビューを表示させるメソッド。
     */
    private void setNewCursor(){
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db);
        if (_menuCategory == ALL){
            cursor = DataAccess.findAll(db);
        }else if(_menuCategory == NOT_DONE){
            cursor = DataAccess.findNotDone(db);
        }else if(_menuCategory == DONE){
            cursor = DataAccess.findDone(db);
        }
        TodoListAdapter adapter = new TodoListAdapter(cursor);
        _rvTodoList.setAdapter(adapter);
    }




    /**
     * リストがクリックされた時のリスナクラス。
     */
    private class ListItemClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CheckBox cbTaskCheckRow = view.findViewById(R.id.cbTaskCheckRow);
            long id = (long) cbTaskCheckRow.getTag();
            Intent intent = new Intent(MainActivity.this, ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", id);
            startActivity(intent);
        }
    }

    /**
     * checkboxを押したときの動作
     */
    private class OnCheckBoxClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CheckBox cbTaskCheck = (CheckBox) view;
            boolean isChecked = cbTaskCheck.isChecked();
            long id = (Long) cbTaskCheck.getTag();
            SQLiteDatabase db = _helper.getWritableDatabase();
            DataAccess.changeTaskChecked(db, id, isChecked);
            setNewCursor();
        }
    }
}