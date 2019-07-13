package wyj.speak_weake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ContactInfoDao {
    private final MyDBOpenHelper helper;

    public ContactInfoDao(Context context) {
        helper = new MyDBOpenHelper(context);
    }

    /**
     * 添加一条记录
     *
     * @param username  用户名
     * @param password 密码
     * @return 返回的是添加在数据库的行号 -1代表失败
     */
    public long add(String username, String password) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long rowid = db.insert("login", null, values);
        // 记得关闭数据库释放资源
        db.close();
        return rowid;
    }

    public long addevent(String functionname, String event) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("functionname", functionname);
        values.put("event", event);
        long rowid = db.insert("function", null, values);
        // 记得关闭数据库释放资源
        db.close();
        return rowid;
    }

    /**
     * 根据姓名删除一条记录
     *
     * @param username 用户名
     * @return 返回0代表的是没有删除任何的记录，返回整数int值达标删除了几条数据
     */
    public int delete(String username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        //        db.execSQL("delete from contactinfo where name = ?;", new Object[]{name});
        int rowcount = db.delete("login", "username=?", new String[]{username});
        // 记得关闭数据库释放资源
        db.close();
        return rowcount;
    }

    /**
     * 修改联系人密码
     *
     * @param newpassword 新的电话号码
     * @param username     要更改的联系人姓名
     * @return 0代表一行也没有更新成功，大于0代表的是更新了多少行记录
     */
    public int update(String newpassword, String username) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newpassword);
        int rowcount = db.update("login", values, "username=?", new String[]{username});
        // 记得关闭数据库释放资源
        db.close();
        return rowcount;
    }

    public boolean query(String username,String password) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql="select * from login where username=? and password=?";
        Cursor cursor=db.rawQuery(sql, new String[]{username,password});
        if(cursor.moveToFirst()==true){
            cursor.close();
            return true;
        }
        return false;
    }
}
