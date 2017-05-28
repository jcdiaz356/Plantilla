package com.dataservicios.plantilla.adapter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dataservicios.plantilla.R;
import com.dataservicios.plantilla.model.Store;
import com.dataservicios.plantilla.view.StoreAuditActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcdia on 12/05/2017.
 */

public class StoreAdapterReciclerView extends RecyclerView.Adapter<StoreAdapterReciclerView.StoreViewHolder>  {

    private ArrayList<Store>    stores;
    private int                 resource;
    private Activity            activity;
    private Filter              fRecords;

    public StoreAdapterReciclerView(ArrayList<Store> stores, int resource, Activity activity) {
        this.stores     = stores;
        this.resource   = resource;
        this.activity   = activity;
    }

    @Override
    public StoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        view.setOnClickListener(new  View.OnClickListener(){
            public void onClick(View v)
            {
                //action
                //Toast.makeText(activity,"dfgdfg",Toast.LENGTH_SHORT).show();
            }
        });
        return new StoreAdapterReciclerView.StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoreViewHolder holder, int position) {
        final Store store = stores.get(position);

        holder.tvId.setText("ID : " + String.valueOf(store.getId()));
        holder.tvFullName.setText(store.getFullname());
        holder.tvAddress.setText(String.valueOf(store.getAddress()));
        holder.tvDistrict.setText(String.valueOf(store.getDistrict()));

        if(store.getStatus() >= 1)  {
            holder.btAudit.setVisibility(View.INVISIBLE);
            holder.imgStatus.setVisibility(View.VISIBLE) ;
        } else {
            holder.btAudit.setVisibility(View.VISIBLE) ;
            holder.imgStatus.setVisibility(View.INVISIBLE);
        }


        holder.btShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "ID Store: " + store.getId() + " \nTienda: " + store.getFullname()  ;
                String shareSub = "Ruta";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_TITLE, shareBody);
                activity.startActivity(Intent.createChooser(sharingIntent, "Share using"));

            }
        });

        holder.btAudit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int store_id = store.getId();
//              Toast.makeText(activity, String.valueOf(store.id), Toast.LENGTH_SHORT).show();
                Bundle bolsa = new Bundle();
                bolsa.putInt("store_id", Integer.valueOf(store_id));
                Intent intent = new Intent(activity,StoreAuditActivity.class);
                intent.putExtras(bolsa);
                activity.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return stores.size();
    }

//    @Override
//    public Filter getFilter() {
//        if(fRecords == null) {
//            fRecords=new RecordFilter();
//        }
//        return fRecords;
//    }
//
    public class StoreViewHolder extends RecyclerView.ViewHolder {
        private TextView    tvId;
        private TextView    tvFullName;
        private TextView    tvAddress;
        private TextView    tvDistrict;
        private Button      btShared;
        private Button      btAudit;
        private ImageView   imgStatus;

        public StoreViewHolder(View itemView) {
            super(itemView);
            tvId            = (TextView)    itemView.findViewById(R.id.tvId);
            tvFullName      = (TextView)    itemView.findViewById(R.id.tvFullName);
            tvAddress       = (TextView)    itemView.findViewById(R.id.tvAddress);
            tvDistrict        = (TextView)    itemView.findViewById(R.id.tvDistrict);
            btShared        = (Button)      itemView.findViewById(R.id.btShared);
            btAudit    = (Button)      itemView.findViewById(R.id.btAudit);
            imgStatus       = (ImageView)   itemView.findViewById(R.id.imgStatus);
        }
    }

//    private class RecordFilter extends Filter {
//
//
//        @Override
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//
//            //Implement filter logic
//            // if edittext is null return the actual list
//            if (constraint == null || constraint.length() == 0) {
//                //No need for filter
//                results.values = stores;
//                results.count = stores.size();
//
//            } else {
//                //Need Filter
//                // it matches the text  entered in the edittext and set the data in adapter list
//                ArrayList<Store> fRecords = new ArrayList<Store>();
//
//                for (Store s : stores) {
//                    if (s.getFullname().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
//                        //fRecords.add(s);
//                        fRecords.add(s);
//                    }
//                }
//                results.values = fRecords;
//                results.count = fRecords.size();
//            }
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint, FilterResults results) {
//            stores = (ArrayList<Store>) results.values;
//            notifyDataSetChanged();
//     }
//    }

    public void setFilter(ArrayList<Store> stores){
        this.stores = new ArrayList<>();
        this.stores.addAll(stores);
        notifyDataSetChanged();
    }
}