package kr.hs.emirim.s2117.mirim_project_0803_sqlite;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DBHelper dbHelper;
    EditText editName, editCount, editResultName, editResultCount;
    Button btnSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("아이돌 정보 관리");
        setContentView(R.layout.activity_main);
        editName = findViewById(R.id.edit_name);
        editCount = findViewById(R.id.edit_count);
        editResultName = findViewById(R.id.edit_result_name);
        editResultCount = findViewById(R.id.edit_result_count);
        Button btnInit = findViewById(R.id.btn_init);
        Button btnInsert = findViewById(R.id.btn_insert);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);
        btnSelect = findViewById(R.id.btn_select);
        btnInit.setOnClickListener(btnListener);
        btnInsert.setOnClickListener(btnListener);
        btnUpdate.setOnClickListener(btnListener);
        btnDelete.setOnClickListener(btnListener);
        btnSelect.setOnClickListener(btnListener);

        dbHelper = new DBHelper(this);

    }
    //integer=" 문자열='
    View.OnClickListener btnListener = new View.OnClickListener() {
        SQLiteDatabase db;
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_init:
                    db = dbHelper.getWritableDatabase();//sql을 실행하기 위한 참조값.?
                    dbHelper.onUpgrade(db, 1, 2);//초기화 할려면 버전을 바꿔야함
                    db.close();
                    break;
                case R.id.btn_insert:
                    db = dbHelper.getWritableDatabase();
                    db.execSQL("insert into idolTbl values('"+editName.getText().toString()+"',"+editCount.getText().toString()+");");
                    db.close();
                    Toast.makeText(getApplicationContext(), "새로운 idol 정보가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    editName.setText("");
                    editCount.setText("");
                    btnSelect.callOnClick();
                    break;
                case R.id.btn_update:
                    db = dbHelper.getWritableDatabase();
                    db.execSQL("update idolTbl set cnt ="+editCount.getText().toString()+" where name ='"+editName.getText().toString()+"';");
                    btnSelect.callOnClick();
                    //db.close();
                    editName.setText("");
                    editCount.setText("");
                    break;
                case R.id.btn_delete:
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setTitle("삭제");
                    dlg.setMessage("정말로 삭제하시겠습니까?");
                    dlg.setNegativeButton("취소", null);
                    dlg.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db = dbHelper.getWritableDatabase();
                            db.execSQL("delete from idolTbl where name ='"+editName.getText().toString()+"';");
                            btnSelect.callOnClick();
                            //db.close();
                            editName.setText("");
                            editCount.setText("");
                        }
                    });
                    dlg.show();

                    break;
                case R.id.btn_select:
                    db = dbHelper.getReadableDatabase();//select문이라서 getReadableDatabase()
                    Cursor c = db.rawQuery("select * from idolTbl;", null);//중간에 들어가는 인수가 없어서 null

                    String strName = "아이돌명\r\n__________\r\n";//\r 현재 커서를 맨앞으로가서 첫번째줄로 옮김
                    String strCnt = "인원수\r\n__________\r\n";

                    while(c.moveToNext()){
                        strName += c.getString(0) + "\r\n";
                        strCnt += c.getInt(1) + "\r\n";
                    }

                    editResultName.setText(strName);
                    editResultCount.setText((strCnt));//append(누적시키는 함수.?)

                    c.close();
                    db.close();
                    break;
            }
        }
    };
    //callback 메소드라서 자동으로 생성
    public class DBHelper extends SQLiteOpenHelper{//SQLiteOpenHelper가 상속을 받는다.?
        //추상클래스
        //DB 생성
        public DBHelper(Context context){
            super(context, "idolDB", null, 1);//부모클래스가 가지고 있는 생성자만 호출 가능.?
            //실행하기전에 버전을 올리면 기존에 입력했던 데이터가 사라짐(새로 만들고 싶을때 다시해야할 경우 버전을 올리자)
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table idolTbl(name char(30) primary key ," + "cnt integer);");//sql만 대소문자 상관없음
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {//매개변수명이 길어서 db로 이름 변경
            db.execSQL("drop table if exists idolTbl");
            onCreate(db);

        }
    }
}