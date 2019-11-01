package com.example.juju;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    InputMethodManager imm; //키보드 내리기

    TextView tvData;
    String id;
    String pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); //키보드 내리기


        tvData = (TextView)findViewById(R.id.textView);
        Button joinBtn = (Button)findViewById(R.id.join);
        Button loginBtn = (Button)findViewById(R.id.login);

        final EditText idEt = findViewById(R.id.teditTex) ;
        final EditText pwEt = findViewById(R.id.editText2) ;
        imm.hideSoftInputFromWindow(idEt.getWindowToken(), 0);  //키보드 내리기
        imm.hideSoftInputFromWindow(pwEt.getWindowToken(), 0); //키보드 내리기


        // int a = 6;
        toast();
        //버튼이 클릭되면 여기 리스너로 옴
        joinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                id = idEt.getText().toString();
                pw = pwEt.getText().toString();
                new JSONTask(MainActivity.this).execute("http://10.0.2.2:4000/auth/join");//AsyncTask 시작시킴
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                id = idEt.getText().toString();
                pw = pwEt.getText().toString();
                new JSONTask(MainActivity.this).execute("http://10.0.2.2:4000/auth/login");//AsyncTask 시작시킴
            }
        });


    }

    public void toast() {
        Toast.makeText(getApplicationContext(), "환영합니다.", Toast.LENGTH_SHORT).show();
    }

    // 자바 객체생성, 생성자, 메서드 호출, 상속, 예외
    public class JSONTask extends AsyncTask<String, String, String>{

        MainActivity context;

        public JSONTask(MainActivity context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.e("withpd", "Process");
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", id);
                jsonObject.accumulate("password", pw);
                Log.e("myid2",id);
                Log.e("mypw",pw);


//                jsonObject.accumulate("email", "hhhhhhh");
//                jsonObject.accumulate("password", "qqqq");

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
                    con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");//POST방식으로 보냄
                    con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
                    con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
                    con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
                    con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
                    con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
                    con.connect();

                    //서버로 보내기위해서 스트림 만듬
                    OutputStream outStream = con.getOutputStream();
                    //버퍼를 생성하고 넣음
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();//데이터가 꽉차지 않아도 강제적으로 보냄
                    writer.close();//버퍼를 받아줌

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    Log.e("withpd", buffer.toString());

                    //여기서 부터 테스트
                    if(buffer.toString().contains("로그인성공")) {
                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                        startActivity(intent);
                    }


                    if(buffer.toString().contains("반갑습니다")) {      // 존재하면
                        Log.e("withpd", "TOAST");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "환영합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();

//                        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
//                        startActivity(intent);
                    } else {
                        Log.e("withpd", "일치하지 않습니다!");
                    }
                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    Log.e("withpd", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();

                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            tvData.setText(result);//서버로 부터 받은 값을 출력해주는 부
        }
    }


    public class JSONTask2 extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }


}