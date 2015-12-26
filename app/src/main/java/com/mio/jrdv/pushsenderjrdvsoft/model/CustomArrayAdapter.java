package com.mio.jrdv.pushsenderjrdvsoft.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import com.mio.jrdv.pushsenderjrdvsoft.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseramondelgado on 25/12/15.
 */
public class CustomArrayAdapter extends ArrayAdapter<Vecino>  implements View.OnClickListener  {

    private LayoutInflater layoutInflater;

    //para le search:

    private ArrayList<Vecino> mDisplayedValues;    // Values to be displayed
    private  ArrayList<Vecino>mOriginalValues; // Original Values
    private Vecinosfilter filter;



    public CustomArrayAdapter(Context context, List<Vecino> objects)
    {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);


        //para el search
        this.mOriginalValues= new ArrayList<Vecino>(objects);


        this.mDisplayedValues=new ArrayList<Vecino>(objects);




    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // holder pattern
        Holder holder = null;
        if (convertView == null)
        {
            holder = new Holder();

            convertView = layoutInflater.inflate(R.layout.listview_row, null);
            holder.setTextViewTitle((TextView) convertView.findViewById(R.id.textViewTitle));
            holder.setTextViewSubtitle((TextView) convertView.findViewById(R.id.textViewSubtitle));
            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        Vecino row = getItem(position);
        holder.getTextViewTitle().setText(row.getComunidad());
        holder.getTextViewSubtitle().setText(row.getNombre());

        holder.getCheckBox().setTag(position);
        holder.getCheckBox().setChecked(row.isChecked());
        holder.getCheckBox().setOnClickListener(this);

        //para cambiar el color si esta seleccionada:

        changeBackground(getContext(), holder.getCheckBox());


        return convertView;
    }



    @Override
    public void onClick(View v) {

        CheckBox checkBox = (CheckBox) v;
        int position = (Integer) v.getTag();
        getItem(position).setChecked(checkBox.isChecked());


        //String msg = this.getContext().getString(R.string.check_toast, position, checkBox.isChecked());
        //Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();

        changeBackground(CustomArrayAdapter.this.getContext(), checkBox);

    }


    @SuppressWarnings("deprecation")
    private void changeBackground(Context context, CheckBox checkBox) {
        View row = (View) checkBox.getParent();
        Drawable drawable;
        context.getResources().getDrawable(
                R.drawable.listview_selector_checked);
        if (checkBox.isChecked()) {
            drawable = context.getResources().getDrawable(
                    R.drawable.listview_selector_checked);
        } else {
            drawable = context.getResources().getDrawable(
                    R.drawable.listview_selector);
        }
        row.setBackgroundDrawable(drawable);
    }


    static class Holder
    {
        TextView textViewTitle;
        TextView textViewSubtitle;
        CheckBox checkBox;

        public TextView getTextViewTitle()
        {
            return textViewTitle;
        }

        public void setTextViewTitle(TextView textViewTitle)
        {
            this.textViewTitle = textViewTitle;
        }

        public TextView getTextViewSubtitle()
        {
            return textViewSubtitle;
        }

        public void setTextViewSubtitle(TextView textViewSubtitle)
        {
            this.textViewSubtitle = textViewSubtitle;
        }
        public CheckBox getCheckBox()
        {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox)
        {
            this.checkBox = checkBox;
        }

    }


    //Para el filtrado

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter  = new Vecinosfilter();
        }
        return filter;
    }





    private class Vecinosfilter extends Filter
    {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint != null && constraint.toString().length() > 0)
            {
                ArrayList<Vecino> filteredItems = new ArrayList<Vecino>();

                for(int i = 0, l = mOriginalValues.size(); i < l; i++)
                {
                    Vecino country = mOriginalValues.get(i);
                    if(country.getNombre().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = mOriginalValues;
                    result.count = mOriginalValues.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {


            if(results.count==0){

                notifyDataSetInvalidated();
            }
            else{



            mDisplayedValues = (ArrayList<Vecino>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = mDisplayedValues.size(); i < l; i++)
                add(mDisplayedValues.get(i));
            notifyDataSetInvalidated();

            }
        }
    }







    /////////////////////////////////////////



}
