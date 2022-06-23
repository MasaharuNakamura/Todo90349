package local.hal.st42.android.todo90349;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class DataAccess {
    /**
     * 全データ検索メソッド。
     *
     * @param db SQLiteDatabaseオブジェクト。
     * @return 検索結果のCursorオブジェクト。
     */
    public static Cursor findAll(SQLiteDatabase db){
        String sql = "SELECT * FROM tasks ORDER BY deadline DESC";
        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }

    /**
     * 実行済みタスクを絞り込み
     * @param db
     * @return
     */
    public static Cursor findDone(SQLiteDatabase db){
        String sql = "SELECT * FROM tasks WHERE done = 1 ORDER BY deadline DESC";
        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }

    /**
     * 実行済みタスクを絞り込み
     * @param db
     * @return
     */
    public static Cursor findNotDone(SQLiteDatabase db){
        String sql = "SELECT * FROM tasks WHERE done = 0 ORDER BY deadline ASC";
        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }



    /**
     * 主キーによる検索。
     *
     * @param db SQLiteDatabaseオブジェクト。
     * @param id 主キー値。
     * @return 主キーに対応するデータを格納したMemoオブジェクト。
     * 　　　　　対応するデータが存在しない場合はnull。
     */
    public static ToDo findByPK(SQLiteDatabase db, long id){
        String sql = "SELECT * FROM tasks WHERE _id = " + id;
        Cursor cursor = db.rawQuery(sql,null);
        ToDo result = null;
        if (cursor.moveToFirst()){
            int idxName = cursor.getColumnIndex("name");
            int idxDeadLine = cursor.getColumnIndex("deadline");
            int idxDone = cursor.getColumnIndex("done");
            int idxNote = cursor.getColumnIndex("note");

            String name = cursor.getString(idxName);
            Long deadline = cursor.getLong(idxDeadLine);
            Integer done = cursor.getInt(idxDone);
            String note = cursor.getString(idxNote);

            result = new ToDo();
            result.setId(id);
            result.setName(name);
            result.setDeadLine(deadline);
            result.setDone(done);
            result.setNote(note);

        }
        return result;
    }

    /**
     * メモ情報を更新するメソッド。
     *
     * @param db SQLiteDatabaseオブジェクト。
     * @param id 主キー値。
     * @param name タスク名。
     * @param deadline 期限を表すエボックからのミリセカンド。
     * @param done タスクが完了(1)か未完了(0)のフラグ。
     * @param note タスクの詳細。
     * @return 更新件数。
     */
    public static int update(SQLiteDatabase db, long id, String name, Long deadline, Integer done, String note){
        String sql = "UPDATE tasks SET name = ?, deadline = ? , done = ? ,note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1,name);
        stmt.bindLong(2,deadline);
        stmt.bindLong(3,done);
        stmt.bindString(4,note);
        stmt.bindLong(5,id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    /**
     * メモ情報を新規登録するメソッド。
     *
     * @param db SQLiteDatabaseオブジェクト。
     * @param name タスク名。
     * @param deadline 期限を表すエボックからのミリセカンド。
     * @param done タスクが完了(1)か未完了(0)のフラグ。
     * @param note メモ内容
     * @return  登録されたレコードの主キー値。
     */
    public static long insert(SQLiteDatabase db,String name, Long deadline, Integer done, String note){
        String sql = "INSERT INTO tasks (name,deadline,done,note) VALUES(?,?,?,?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1,name);
        stmt.bindLong(2,deadline);
        stmt.bindLong(3,done);
        stmt.bindString(4,note);
        long id = stmt.executeInsert();
        return id;
    }

    /**
     * todoリスト情報を削除するメソッド。
     *
     * @param db SQLiteDatabaseオブジェクト。
     * @param id 主キー値。
     * @return 削除件数。
     */
    public static int delete(SQLiteDatabase db, long id) {
        String sql = "DELETE FROM tasks WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    public static void changeTaskChecked(SQLiteDatabase db, long id, boolean isChecked)
    {
        String sql = "UPDATE tasks SET done = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        if(isChecked) {
            stmt.bindLong(1, 1);
        }
        else {
            stmt.bindLong(1, 0);
        }
        stmt.bindLong(2, id);
        stmt.executeUpdateDelete();
    }
}

