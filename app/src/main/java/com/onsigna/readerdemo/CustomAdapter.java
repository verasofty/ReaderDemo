package com.onsigna.readerdemo;

import android.content.Context;
import android.util.Log;
import android.widget.ListAdapter;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class CustomAdapter implements ListAdapter, View.OnClickListener {

    private static String TAG = CustomAdapter.class.getSimpleName();
    private View.OnClickListener listener;
    ArrayList<SubjectData> arrayList;
    Context context;

    public CustomAdapter(Context context, ArrayList<SubjectData> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        Log.d(TAG, "== getItem() ==");

        Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
        return position;
    }
    @Override
    public long getItemId(int position) {
        Log.d(TAG, "== getItemId() ==");

        Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubjectData subjectData=arrayList.get(position);
        if(convertView==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView=layoutInflater.inflate(R.layout.list_row, null);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "== convertView.setOnClickListener() ==");
                    Log.d(TAG, "Nombre -> " + arrayList.get(position).SubjectName);
                }
            });
            TextView tvAuth = convertView.findViewById(R.id.tvAuth);
            TextView tvCardNumber = convertView.findViewById(R.id.tvCardNumber);
            TextView tvAmount = convertView.findViewById(R.id.tvAmount);
            TextView tvStatus = convertView.findViewById(R.id.tvStatus);

            tvAuth.setText(subjectData.SubjectName);
            tvCardNumber.setText(subjectData.Image);
            tvStatus.setText(subjectData.Link);
            tvAmount.setText(subjectData.SubjectName);

        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "== onClick() ==");
        if (listener != null){
            listener.onClick(view);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        Log.d(TAG, "== CustomAdapter.setOnClickListener() ==");
        this.listener = listener;
    }
}
