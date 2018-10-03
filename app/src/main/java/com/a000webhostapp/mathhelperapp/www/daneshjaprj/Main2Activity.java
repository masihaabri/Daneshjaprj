package com.a000webhostapp.mathhelperapp.www.daneshjaprj;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    List<HashMap<String, Object>> hash_store_list;
    String[] items_store_list;
    CustomAdapter customAdapter;
    ListView lv;
    private ShimmerFrameLayout mShimmerViewContainer;
    RelativeLayout rt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        intit();
        customAdapter = new CustomAdapter();
        get();
        lv.setAdapter(customAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final HashMap<String, Object> hash_get = (HashMap<String, Object>) hash_store_list.get(position);
                Intent intent = new Intent(Main2Activity.this, Detail.class);
                intent.putExtra("ID", hash_get.get("ID").toString());
                intent.putExtra("Name", hash_get.get("Name").toString());
                intent.putExtra("Price", hash_get.get("Price").toString());
                intent.putExtra("Description", hash_get.get("Description").toString());
                intent.putExtra("Available", hash_get.get("Available").toString());
                intent.putExtra("Like", hash_get.get("Like").toString());
                intent.putExtra("Discount_percent", hash_get.get("Discount_percent").toString());
                intent.putExtra("image", hash_get.get("image").toString());
                startActivity(intent);
            }
        });

    }

    private void get() {
        AsyncHttpGet get = new AsyncHttpGet("https://mathhelperapp.000webhostapp.com/daneshkala/product.php?Product=lk");

        get.setTimeout(6000);
        AsyncHttpClient.getDefaultInstance().executeString(get, new AsyncHttpClient.StringCallback() {
            @Override
            public void onCompleted(Exception ex, AsyncHttpResponse source, final String result) {
                if (ex != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main2Activity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                        }
                    });
                    ex.printStackTrace();
                    return;
                }
                if (!result.equals("") && !result.equals("[]")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mShimmerViewContainer.stopShimmerAnimation();
                            mShimmerViewContainer.setVisibility(View.GONE);
                            try {
                                Log.e("result", result);
                                JSONArray jsonArray = new JSONArray(result);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    HashMap<String, Object> hash_add = new HashMap<>();
                                    hash_add.put("ID", object.getString("id"));
                                    hash_add.put("Name", object.getString("name"));
                                    hash_add.put("Price", object.getString("price"));
                                    hash_add.put("Description", object.getString("description"));
                                    hash_add.put("Available", object.getString("available"));
                                    hash_add.put("Like", object.getString("like"));
                                    hash_add.put("Discount_percent", object.getString("dicount_percent"));
                                    JSONArray ja = object.getJSONArray("image");
                                    JSONObject jso = ja.getJSONObject(0);
                                    hash_add.put("image", jso.getString("url"));
                                    Log.e("json", object.getString("name"));
                                    hash_store_list.add(hash_add);
                                    items_store_list = new String[hash_store_list.size()];


                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mShimmerViewContainer.stopShimmerAnimation();
                                    mShimmerViewContainer.setVisibility(View.GONE);
                                    customAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void intit() {
        hash_store_list = new ArrayList<>();
        items_store_list = new String[hash_store_list.size()];
        lv = findViewById(R.id.lv);
        rt = findViewById(R.id.loading_layout);

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items_store_list.length;
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
            final HashMap<String, Object> hash_get = (HashMap<String, Object>) hash_store_list.get(position);
            convertView = getLayoutInflater().inflate(R.layout.custom_list, null);
            TextView name = convertView.findViewById(R.id.list_name);
            TextView description = convertView.findViewById(R.id.description);
            ImageView img = convertView.findViewById(R.id.list_img);
            TextView price = convertView.findViewById(R.id.list_price);
            TextView available = convertView.findViewById(R.id.list_available);
            TextView discount = convertView.findViewById(R.id.list_discount_percent);
            View line = convertView.findViewById(R.id.line1);
            name.setText(hash_get.get("Name").toString());
            description.setText(hash_get.get("Description").toString());
            price.setText("تومان" + hash_get.get("Price").toString());
            if (hash_get.get("Available").toString().equals("1")) {
                available.setText(R.string.available);
                available.setTextColor(Main2Activity.this.getResources().getColor(R.color.green));
            } else {
                available.setText(R.string.notavailable);
                available.setTextColor(Main2Activity.this.getResources().getColor(R.color.red));
            }
            discount.setText("تخفیف " + "%" + hash_get.get("Discount_percent"));
            if (position < 1) {
                line.setVisibility(View.INVISIBLE);
            } else {
                line.setVisibility(View.VISIBLE);
            }
            try {
                Picasso.with(Main2Activity.this)
                        .load(hash_get.get("image").toString())
                        .into(img);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
