package com.city.list.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.city.list.main.DBManager;
import com.city.list.main.areamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 地区页面
 * @author xiaojia
 */
public class area extends AppCompatActivity {

    private BaseAdapter adapter;
    private ListView mCityLit;
    private TextView overlay;
    private MyLetterListView letterListView;
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread;
    private SQLiteDatabase database;
    private ArrayList<areamodel> mCityNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        mCityLit = (ListView) findViewById(R.id.area_list);
        letterListView = (MyLetterListView) findViewById(R.id.cityLetterListView);
        Button goback= (Button) findViewById(R.id.area_goback);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });


        DBManager dbManager = new DBManager(this);
        dbManager.openDateBase();
        dbManager.closeDatabase();
        database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/" + DBManager.DB_NAME, null);
        mCityNames = getCityNames();
        database.close();
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        initOverlay();
        setAdapter(mCityNames);
        mCityLit.setOnItemClickListener(new CityListOnItemClick());
    }

    /**
     * 从数据库获取城市数据
     *
     * @return
     */
    private ArrayList<areamodel> getCityNames()
    {
        ArrayList<areamodel> names = new ArrayList<areamodel>();
        Cursor cursor = database.rawQuery("SELECT * FROM T_City ORDER BY NameSort", null);
        for (int i = 0; i < cursor.getCount(); i++)
        {
            cursor.moveToPosition(i);
            areamodel cityModel = new areamodel();
            cityModel.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
            cityModel.setNameSort(cursor.getString(cursor.getColumnIndex("NameSort")));
            cityModel.setAreaCode(cursor.getString(cursor.getColumnIndex("AreaCode")));
            names.add(cityModel);
        }
        return names;
    }

    private class LetterListViewListener implements MyLetterListView.OnTouchingLetterChangedListener
    {

        @Override
        public void onTouchingLetterChanged(final String s)
        {
            if (alphaIndexer.get(s) != null)
            {
                int position = alphaIndexer.get(s);
                mCityLit.setSelection(position+1);
                overlay.setText(sections[position]);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见

                handler.postDelayed(overlayThread, 700);
            }
        }

    }


    /**
     * 为ListView设置适配器
     *
     * @param list
     */
    private void setAdapter(List<areamodel> list)
    {
        if (list != null)
        {
            adapter = new ListAdapter(this, list);
            mCityLit.setAdapter(adapter);
        }

    }

    /**
     * ListViewAdapter
     *
     *
     */
    private class ListAdapter extends BaseAdapter
    {
        private LayoutInflater inflater;
        private List<areamodel> list;

        public ListAdapter(Context context, List<areamodel> list)
        {

            this.inflater = LayoutInflater.from(context);
            this.list = list;
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[list.size()];

            for (int i = 0; i < list.size(); i++)
            {
                // 当前汉语拼音首字母
                // getAlpha(list.get(i));
                String currentStr = list.get(i).getNameSort();
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameSort() : " ";
                if (!previewStr.equals(currentStr))
                {
                    String name = list.get(i).getNameSort();
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }

        }

        @Override
        public int getCount()
        {
            return list.size();
        }

        @Override
        public Object getItem(int position)
        {
            return list.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.view_item_top_line =convertView.findViewById(R.id.view_item_top_line);
                convertView.setTag(holder);
            } else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(list.get(position).getCityName());
            holder.code.setText(list.get(position).getAreaCode());
            String currentStr = list.get(position).getNameSort();
            String previewStr = (position - 1) >= 0 ? list.get(position - 1).getNameSort() : " ";
            if (!previewStr.equals(currentStr))
            {
                holder.alpha.setVisibility(View.VISIBLE);
                holder.alpha.setText(currentStr);
                holder.view_item_top_line.setVisibility(View.GONE);
            } else
            {
                holder.alpha.setVisibility(View.GONE);
                holder.view_item_top_line.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        private class ViewHolder
        {
            TextView alpha;
            TextView name;
            TextView code;
            View view_item_top_line;
        }

    }

    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    /**
     * 城市列表点击事件
     *
     *
     */
    class CityListOnItemClick implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
        {
            if(pos!=0){
                areamodel cityModel = (areamodel) mCityLit.getAdapter().getItem(pos);
                String all_data = cityModel.getCityName()+"("+cityModel.getAreaCode()+")";
//                Toast.makeText(area.this, all_data, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("all_data",all_data);
                setResult(RESULT_OK,intent);
                finish();
            }

        }

    }

    // 设置overlay不可见
    private class OverlayThread implements Runnable
    {

        @Override
        public void run()
        {
            overlay.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}
