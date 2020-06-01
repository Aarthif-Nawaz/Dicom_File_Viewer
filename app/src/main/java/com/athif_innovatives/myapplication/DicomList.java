package com.athif_innovatives.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class DicomList extends ArrayAdapter<Dicom_Extractor> {
    private static final String TAG = "DicomList";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView id;
        TextView name;
        TextView description;
    }

    /**
     * Default constructor for the DicomList
     * @param context
     * @param resource
     * @param objects
     */
    public DicomList(Context context, int resource, ArrayList<Dicom_Extractor> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String id = getItem(position).getTagId();
        String name = getItem(position).getTagName();
        String description = getItem(position).getDescription();


        Dicom_Extractor dicom = new Dicom_Extractor(id,name,description);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.id = (TextView) convertView.findViewById(R.id.tagID);
            holder.name = (TextView) convertView.findViewById(R.id.tagName);
            holder.description = (TextView) convertView.findViewById(R.id.Description);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.id.setText(dicom.getTagId());
        holder.name.setText(dicom.getTagName());
        holder.description.setText(dicom.getDescription());


        return convertView;
    }
}
