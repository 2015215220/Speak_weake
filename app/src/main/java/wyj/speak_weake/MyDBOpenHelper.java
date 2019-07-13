package wyj.speak_weake;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBOpenHelper extends SQLiteOpenHelper {
    // 第一个参数是上下文
    // 第二个参数是数据库名称
    // 第三个参数null表示使用默认的游标工厂
    // 第四个参数是数据库的版本号 数据库只能升级 不能降级，版本号只能变大不能变小
    public MyDBOpenHelper(Context context) {
        super(context, "hy", null, 1);
    }
    /**
     * Called when the database is created for the first time.
     * 当数据库第一次被创建的时候调用的方法，适合在这个方法里面把数据库的表结构定义出来
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("onCreate 数据库被创建了");
        db.execSQL("create table login(id integer primary key autoincrement, username varchar(20), password varchar(20));");
        db.execSQL("create table function(id integer primary key autoincrement, functionname varchar(20), event varchar(3000));");
    }
    /**
     * Called when the database needs to be upgrade.
     * 当数据库更新的时候调用的方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("onUpgrade 数据库被更新了");
    }
}
