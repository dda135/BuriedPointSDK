package fanjh.mine.buriedpointsdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

import fanjh.mine.buriedpoint.BuriedPointClient;
import fanjh.mine.buriedpointannotation.PageBackground;
import fanjh.mine.buriedpointannotation.PageShow;


/**
 * Created by faker on 2018/2/8.
 */
@RequiresApi
public class AActivity extends Activity{
    @SerializedName("sss")
    private Button loginButton;
    private Button loginoutButton;
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        loginButton = findViewById(R.id.btn_login);
        loginoutButton = findViewById(R.id.btn_loginout);
        registerButton = findViewById(R.id.btn_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuriedPointClient.getInstance().login(232);
                finish();
            }
        });
        loginoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuriedPointClient.getInstance().loginOut();
                finish();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuriedPointClient.getInstance().register("111");
                finish();
                int i = 1/0;
            }
        });
    }

    @PageShow
    public Map<String,String> click(){
        HashMap<String,String> params = new HashMap<>();
        params.put("type","click");
        params.put("haha","enen");
        params.put("c","a1");
        params.put("b2","gdf");
        return params;
    }

    @PageBackground
    public Map<String,String> background(){
        HashMap<String,String> params = new HashMap<>();
        params.put("type","report");
        params.put("background",getClass().getSimpleName());
        return params;
    }

    @PageBackground
    public Map<String,String> demo(){
        return null;
    }

}
