package chenhong.com.swipedelete;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<String> list=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        for(int i=0;i<30;i++){
            list.add("第"+i+"条数据");
        }
        listView.setAdapter(new Myadapter());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){// 在滚动状态
                     SwipeLayoutManager.getInstance().closeCurrentLayout();
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    static class ViewHolder{
        TextView tv_name,tv_delete;
        SwipeLayout swipeLayout;
        public ViewHolder(View convertview){
            tv_name= (TextView) convertview.findViewById(R.id.tv_name);
            tv_delete= (TextView) convertview.findViewById(R.id.tv_delete);
            swipeLayout= (SwipeLayout) convertview.findViewById(R.id.swipelayout);
        }
        public  static ViewHolder getHolder(View convertview){
            ViewHolder viewHolder= (ViewHolder) convertview.getTag();
            if(viewHolder==null){
                viewHolder=new ViewHolder(convertview);
                convertview.setTag(viewHolder);
            }
            return  viewHolder;
        }
    }


    class Myadapter extends BaseAdapter implements SwipeLayout.OnSwipeStateChangedListener{
        @Override
        public int getCount() {
            return list.size();
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
            if(convertView==null){
                convertView=View.inflate(MainActivity.this,R.layout.adapter_list,null);
            }
            ViewHolder viewHolder=ViewHolder.getHolder(convertView);
            viewHolder.tv_name.setText(list.get(position));
            viewHolder.swipeLayout.setTag(position);
            viewHolder.swipeLayout.setOnSwipeStateChangedListener(this);
            return convertView;
        }
        @Override
        public void onOpen(Object tag) {
            Toast.makeText(MainActivity.this,"第"+(Integer)tag+"个打开",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onClose(Object tag) {
        }
    }
}
