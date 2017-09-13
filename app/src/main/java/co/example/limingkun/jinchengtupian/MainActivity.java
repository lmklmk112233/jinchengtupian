package co.example.limingkun.jinchengtupian;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    private EditText et_url;
    private Button btg_go;
    private ImageView img_urlpic;

    public static final int SUCCESS= 0;
    public static final int FAIL =1;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch(msg.what){
                case FAIL:
                    Toast.makeText(MainActivity.this, "读取网络图片失败！",1).show();
                    break;

                case SUCCESS:
                    img_urlpic.setImageBitmap((Bitmap) msg.obj);    //UI主线程里面更新UI信息
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_url = (EditText) findViewById(R.id.et_url);
        btg_go = (Button) findViewById(R.id.btg_go);
        img_urlpic = (ImageView) findViewById(R.id.img_urlpic);

        btg_go.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        final String picUrl = et_url.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap netBitmap = getBitmapFromNet(picUrl);
                Message msg = new Message();
                if(netBitmap == null) {
                    msg.what = FAIL;
                } else {
                    msg.what = SUCCESS;
                    msg.obj = netBitmap;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }


    private Bitmap getBitmapFromNet(String urlAddr){
        Bitmap netBitmap = null;
        HttpURLConnection httpConn = null;

        try {
            URL url = new URL(urlAddr);

            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(30000);
            httpConn.setReadTimeout(10000);

            httpConn.connect();

            /* 读取成功 */
            if(200 == httpConn.getResponseCode()) {
                InputStream is =  httpConn.getInputStream();
                netBitmap = BitmapFactory.decodeStream(is);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(httpConn != null) {
                httpConn.disconnect();
                httpConn = null;
            }
        }

        return netBitmap;
    }
}