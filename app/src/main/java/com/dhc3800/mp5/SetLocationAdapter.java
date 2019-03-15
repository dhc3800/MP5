package com.dhc3800.mp5;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SetLocationAdapter extends RecyclerView.Adapter<SetLocationAdapter.ViewHolder> {
    private ArrayList<SetLocation> locations;
    private DecimalFormat df = new DecimalFormat("###.###");

    public SetLocationAdapter(ArrayList<SetLocation> locations) {
        this.locations = locations;
    }

    public void setChange(ArrayList<SetLocation> locations) {
        this.locations = new ArrayList<>();
        this.locations.addAll(locations);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView add;
        TextView name;
        TextView lat;
        TextView lon;
        View view;
        public ViewHolder(View v) {
            super(v);
            add = v.findViewById(R.id.Address);
            name = v.findViewById(R.id.Name);
            lat = v.findViewById(R.id.Lat);
            lon = v.findViewById(R.id.Long);
            view = v;
        }

    }


    @Override
    public SetLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final SetLocation location = locations.get(position);
        holder.add.setText(location.address);
        holder.name.setText(location.name);
        holder.lat.setText(String.valueOf(df.format(location.Latitude)));
        holder.lon.setText(String.valueOf(df.format(location.Longitude)));

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                new AlertDialog.Builder(v.getContext()).setTitle("Delete Location")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SetLocation s = locations.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, locations.size());
                                Helper helper = new Helper(v.getContext());
                                helper.delete(helper.getLocation(s.id));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (locations == null) {
            return 0;
        }
        return locations.size();
    }





}
