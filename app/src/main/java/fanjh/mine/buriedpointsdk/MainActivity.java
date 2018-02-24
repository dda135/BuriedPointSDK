package fanjh.mine.buriedpointsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fanjh.mine.buriedpoint.BuriedPointClient;
import fanjh.mine.buriedpoint.core.DataModelFactory;
import fanjh.mine.buriedpoint.pb.ReportEntry;
import fanjh.mine.buriedpointannotation.PageBackground;
import fanjh.mine.buriedpointannotation.PageShow;

public class MainActivity extends AppCompatActivity {
    private Button jumpButton;
    private Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jumpButton = findViewById(R.id.btn_jump);
        clickButton = findViewById(R.id.btn_click);
        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AActivity.class);
                startActivity(intent);
            }
        });
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuriedPointClient.getInstance().report("click");
            }
        });
        BuriedPointClient.getInstance().startApp();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @PageShow
    public Map<String,String> report(){
        Map<String,String> map = new HashMap<String,String>();
        map.put("a","ss");
        return map;
    }

    @PageBackground
    public Map<String,String> background(){
        HashMap<String,String> params = new HashMap<>();
        params.put("type","report");
        params.put("background",getClass().getSimpleName());
        return params;
    }

}
