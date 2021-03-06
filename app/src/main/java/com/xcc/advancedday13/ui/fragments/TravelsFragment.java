package com.xcc.advancedday13.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lhh.ptrrv.library.PullToRefreshRecyclerView;
import com.xcc.advancedday13.R;
import com.xcc.advancedday13.adapters.TravelsAdapter;
import com.xcc.advancedday13.base.BaseFragment;
import com.xcc.advancedday13.constants.HttpConstant;
import com.xcc.advancedday13.model.TravelRoot;
import com.xcc.advancedday13.ui.UserActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class TravelsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,Handler.Callback, TravelsAdapter.OnReadAllClicked, TravelsAdapter.OnUserIconClicked, TravelsAdapter.OnUserMoreClicked {

    private static final int UP_DATE = 100;
    private static final int DOWN = 200;
    private static final int UP = 300;
    private static final String TAG = TravelsFragment.class.getSimpleName();
    private PullToRefreshRecyclerView mPtrrv;
    private TravelsAdapter adapter;
    private int pageIndex=1;
    private Handler mHandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout=inflater.inflate(R.layout.fragment_travels,container,false);
        initView();
        return layout;
    }

    private void initView() {
        mHandler=new Handler(this);
        mPtrrv = ((PullToRefreshRecyclerView) layout.findViewById(R.id.travels_ptrrv));
        mPtrrv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPtrrv.setRefreshing(true);
        mPtrrv.setOnRefreshListener(this);
        adapter = new TravelsAdapter(getActivity(), null);
        adapter.setReadAllClicked(this);
        adapter.setUserIconClicked(this);
        adapter.setUserMoreClicked(this);
        mPtrrv.setAdapter(adapter);
        setupView(Selected.DOWN);
    }

    @Override
    public void onUserIconClicked(TravelRoot.DataBean item) {
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra("id",item.getActivity().getUser().getId());
        startActivity(intent);
    }

    @Override
    public void onUserMoreClicked(TravelRoot.DataBean item, View view) {
        
    }


    enum Selected{
        UP,DOWN
    }
    private void setupView(final Selected state) {
        RequestParams params = new RequestParams(HttpConstant.TRAVELS_URL+pageIndex);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        Gson gson = new Gson();
                        TravelRoot travelRoot = gson.fromJson(result, TravelRoot.class);
                        Message msg = Message.obtain();
                        msg.obj=travelRoot;
                        msg.what=UP_DATE;
                        if (state.equals(Selected.DOWN)) {
                            msg.arg1=DOWN;
                        }else if (state.equals(Selected.UP)) {
                            msg.arg1=UP;
                        }
                        mHandler.sendMessage(msg);
                    }
                };
                thread.start();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.e(TAG, "onFinished: ");
            }
        });
    }
    @Override
    public void onRefresh() {
        pageIndex=1;
        setupView(Selected.DOWN);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case UP_DATE:
                TravelRoot travelRoot = (TravelRoot) msg.obj;
                if (msg.arg1==DOWN) {
                    adapter.updateRes(travelRoot.getData());
                }else if (msg.arg1==UP){
                    adapter.addRes(travelRoot.getData());
                }
                break;
        }
        return false;
    }

    @Override
    public void onReadAllClicked(TravelRoot.DataBean item) {
        item.getActivity().setReadAllClicked(true);
        if (item.getActivity().getParent_district_count()<=1) {
            item.getActivity().setShowDetail(false);
        }else {
            item.getActivity().setShowDetail(true);
        }
        if (mPtrrv.getRecyclerView().getScrollState()== RecyclerView.SCROLL_STATE_IDLE&&!mPtrrv.getRecyclerView().isComputingLayout()) {
            adapter.notifyDataSetChanged();
        }
    }
}
