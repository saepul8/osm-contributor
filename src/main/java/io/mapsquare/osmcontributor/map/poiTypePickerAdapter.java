/**
 * Copyright (C) 2015 eBusiness Information
 *
 * This file is part of OSM Contributor.
 *
 * OSM Contributor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSM Contributor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSM Contributor.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.mapsquare.osmcontributor.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import io.mapsquare.osmcontributor.R;
import io.mapsquare.osmcontributor.core.model.PoiType;
import io.mapsquare.osmcontributor.map.events.NewPoiTypeSelected;

class poiTypePickerAdapter extends BaseAdapter implements Filterable {

    private List<PoiType> originalValues = null;
    private List<PoiType> filteredValues = null;
    private LayoutInflater inflater;
    private ItemFilter filter = new ItemFilter();
    private EditText editText;
    private Context context;
    private BitmapHandler bitmapHandler;


    EventBus eventBus;

    public poiTypePickerAdapter(Context context, List<PoiType> values, EditText editText, EventBus eventBus, BitmapHandler bitmapHandler) {
        this.filteredValues = values;
        Collections.sort(filteredValues, new SortIgnoreCase()); // sorting values ignoring case
        this.originalValues = filteredValues;
        this.editText = editText;
        this.context = context;
        this.eventBus = eventBus;
        this.bitmapHandler = bitmapHandler;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredValues.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.single_autocomplete_layout, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        final PoiType value = filteredValues.get(position);
        //Capitalize the first letter
        String cap = value.getName().substring(0, 1).toUpperCase() + value.getName().substring(1);
        holder.getTextView().setText(cap);
        holder.getIcon().setImageDrawable(bitmapHandler.getDrawable(value.getIcon()));
        holder.getInfo().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoiTypeInfo(value);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventBus.post(new NewPoiTypeSelected(value));
                closeKeyboard();
            }
        });
        return view;
    }

    public void addAll(Collection<PoiType> poiTypes) {
        originalValues.clear();
        originalValues.addAll(poiTypes);
        notifyDataSetChanged();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    static class ViewHolder {
        @InjectView(R.id.text_view)
        TextView textView;

        @InjectView(R.id.icon)
        ImageView icon;

        @InjectView(R.id.info)
        ImageView info;

        public TextView getTextView() {
            return textView;
        }

        public ImageView getIcon() {
            return icon;
        }


        public ImageView getInfo() {
            return info;
        }

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();


            FilterResults results = new FilterResults();

            final List<PoiType> list = originalValues;

            int count = list.size();
            final ArrayList<PoiType> newValuesList = new ArrayList<>(count);

            if (filterString.isEmpty()) {
                results.values = originalValues;
                results.count = originalValues.size();
                return results;
            }

            for (int i = 0; i < count; i++) {
                String filterableString = list.get(i).getKeywordsStr();
                if (filterableString.toLowerCase().contains(filterString) || filterableString.toLowerCase().equals(filterString)) {
                    newValuesList.add(list.get(i));
                }
            }

            results.values = newValuesList;
            results.count = newValuesList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredValues = (ArrayList<PoiType>) results.values;
            notifyDataSetChanged();
        }

    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }

    private void showPoiTypeInfo(PoiType poiType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(poiType.getDescription())
                .setCancelable(true)
                .setTitle(poiType.getName())
                .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

