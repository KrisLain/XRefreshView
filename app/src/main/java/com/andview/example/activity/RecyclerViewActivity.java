package com.andview.example.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.Menu;
import android.view.MenuItem;

import com.andview.example.R;
import com.andview.example.recylerview.Person;
import com.andview.example.recylerview.SimpleAdapter;
import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshView.SimpleXRefreshListener;
import com.andview.refreshview.XRefreshViewFooter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends Activity {
	RecyclerView recyclerView;
	SimpleAdapter adapter;
	List<Person> personList = new ArrayList<Person>();
	XRefreshView xRefreshView;
	int lastVisibleItem = 0;
	LinearLayoutManager layoutManager;
	private boolean isBottom = false;
	private int mLoadCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recylerview);
		xRefreshView = (XRefreshView) findViewById(R.id.xrefreshview);
		xRefreshView.setPullLoadEnable(true);
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view_test_rv);
		recyclerView.setHasFixedSize(true);

		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		initData();
		adapter = new SimpleAdapter(personList);
		// 设置静默加载模式
//		xRefreshView.setSlienceLoadMore();
		// 静默加载模式不能设置footerview
		 adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
		recyclerView.setAdapter(adapter);
		xRefreshView.setAutoLoadMore(true);
		xRefreshView.setPinnedTime(1000);
		xRefreshView.setMoveForHorizontal(true);
		//设置静默加载时提前加载的item个数
//		xRefreshView.setPreLoadCount(2);

		xRefreshView.setXRefreshViewListener(new SimpleXRefreshListener() {

			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						xRefreshView.stopRefresh();
					}
				}, 2000);
			}

			@Override
			public void onLoadMore(boolean isSlience) {
				new Handler().postDelayed(new Runnable() {
					public void run() {
						adapter.insert(new Person("More ", "21"),
								adapter.getAdapterItemCount());
						adapter.insert(new Person("More ", "21"),
								adapter.getAdapterItemCount());
						adapter.insert(new Person("More ", "21"),
								adapter.getAdapterItemCount());
						mLoadCount++;
						if (mLoadCount >= 3) {
							xRefreshView.setLoadComplete(true);
						} else {
							// 刷新完成必须调用此方法停止加载
							xRefreshView.stopLoadMore();
						}
					}
				}, 1000);
			}
		});
		// 实现Recyclerview的滚动监听，在这里可以自己处理到达底部加载更多的操作，可以不实现onLoadMore方法，更加自由
		xRefreshView.setOnRecyclerViewScrollListener(new OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = layoutManager.findLastVisibleItemPosition();
			}

			public void onScrollStateChanged(RecyclerView recyclerView,
											 int newState) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					isBottom = adapter.getItemCount() - 1 == lastVisibleItem;
				}
			};
		});
	}

	private void initData() {
		for (int i = 0; i < 20; i++) {
			Person person = new Person("name" + i, "" + i);
			personList.add(person);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 加载菜单
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int menuId = item.getItemId();
		switch (menuId) {
			case R.id.menu_clear:
				mLoadCount = 0;
				xRefreshView.setLoadComplete(false);
				break;
			case R.id.menu_refresh:
				xRefreshView.startRefresh();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}