package com.android.management_linen.helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class ContainsFilterAdapter extends ArrayAdapter<String> {
    private final List<String> originalItems;
    private final Filter filter;

    public ContainsFilterAdapter(Context context, int resource, List<String> items) {
        super(context, resource, new ArrayList<>(items));
        this.originalItems = new ArrayList<>(items);
        this.filter = new ContainsFilter();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private class ContainsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(originalItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : originalItems) {
                    if (item.toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null) {
                addAll((List<String>) results.values);
            }
            notifyDataSetChanged();
        }
    }
}
