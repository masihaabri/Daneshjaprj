package com.a000webhostapp.mathhelperapp.www.daneshjaprj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Detail extends AppCompatActivity {
    MaterialDialog wait;
    String ID, Name, Price, Description, Available, Like, Discount_percent, image;
    ImageView detail_img;
    TextView detail_name, detail_price, detail_available, detail_percent, detail_likes, detail_description;
    ListView detail_lv;

    List<HashMap<String, Object>> hash_comment;
    String[] items_comment;
    CustomAdapterComment customAdapterComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        wait = new MaterialDialog.Builder(Detail.this)
                .content("لطفا صبر کنید...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        hash_comment = new ArrayList<>();
        items_comment = new String[hash_comment.size()];
        Intent i = getIntent();
        ID = i.getStringExtra("ID");
        Name = i.getStringExtra("Name");
        Price = i.getStringExtra("Price");
        Description = i.getStringExtra("Description");
        Available = i.getStringExtra("Available");
        Like = i.getStringExtra("Like");
        Discount_percent = i.getStringExtra("Discount_percent");
        image = i.getStringExtra("image");
        customAdapterComment = new CustomAdapterComment();
        detail_lv.setAdapter(customAdapterComment);
        try {
            Picasso.with(Detail.this)
                    .load(image)
                    .into(detail_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        detail_name.setText("نام محصول : " + Name);
        detail_price.setText("قیمت : " + Price);
        if (Available.equals("1")) {
            detail_available.setText(R.string.available);
        } else if (Available.equals("0")) {
            detail_available.setText(R.string.notavailable);
            detail_available.setTextColor(this.getResources().getColor(R.color.red));
        }
        detail_percent.setText("درصد تخفیف : " + "%" + Discount_percent);
        detail_likes.setText("تعداد لایک ها : " + Like);
        detail_description.setText(Description);
        get();
    }

    private void get() {
        wait.show();
        AsyncHttpGet get = new AsyncHttpGet("https://mathhelperapp.000webhostapp.com/daneshkala/product.php?Product=lk");

        get.setTimeout(6000);
        AsyncHttpClient.getDefaultInstance().executeString(get, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, final String result) {
                if (ex != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Detail.this, R.string.error, Toast.LENGTH_SHORT).show();
                            wait.dismiss();
                        }
                    });
                    ex.printStackTrace();
                    return;
                }
                if (!result.equals("") && !result.equals("[]")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(result);
                                int id = Integer.parseInt(ID);
                                JSONObject jsonObject = jsonArray.getJSONObject(id-1);
                                JSONArray ja = jsonObject.getJSONArray("comments");
                                for (int x = 0; x < ja.length(); x++) {
                                    JSONObject obj = ja.getJSONObject(x);
                                    HashMap<String, Object> hash_adddd = new HashMap<>();
                                    hash_adddd.put("content", obj.getString("content"));
                                    hash_comment.add(hash_adddd);
                                    items_comment = new String[hash_comment.size()];
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    customAdapterComment.notifyDataSetChanged();
                                }
                            });
                            wait.dismiss();
                        }
                    });
                }
            }
        });
    }

    public void init() {
        detail_img = findViewById(R.id.detail_img);
        detail_name = findViewById(R.id.detail_name);
        detail_price = findViewById(R.id.detail_price);
        detail_available = findViewById(R.id.detail_available);
        detail_percent = findViewById(R.id.detail_percent);
        detail_likes = findViewById(R.id.detail_likes);
        detail_description = findViewById(R.id.detail_description);
        detail_lv = findViewById(R.id.detail_lv);
    }


    class CustomAdapterComment extends BaseAdapter {

        @Override
        public int getCount() {
            return items_comment.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.e("adapter", "worked");
            HashMap<String, Object> hash_get = (HashMap<String, Object>) hash_comment.get(position);
            convertView = getLayoutInflater().inflate(R.layout.comment_list, null);
            TextView username = convertView.findViewById(R.id.username);
            TextView text = convertView.findViewById(R.id.d);
            username.setText("Alireza: ");
            text.setText(hash_get.get("content").toString());

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(Detail.this, Main2Activity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

}
