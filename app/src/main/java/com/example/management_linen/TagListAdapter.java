package com.example.management_linen;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.VectorEnabledTintResources;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {
    private List<HashMap<String, String>> tagList;
    Context context;
    public TagListAdapter(Context context, List<HashMap<String, String>> tagList) {
        this.tagList = tagList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> tagData = tagList.get(position);

        holder.tagUiiTextView.setText(tagData.get("tagUii"));
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagUiiTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagUiiTextView = itemView.findViewById(R.id.textView2);
        }
    }


//    private List<HashMap<String, String>> tagList;
//    private LayoutInflater inflater;
//
//    public TagListAdapter(Context context, List<HashMap<String, String>> tagList) {
//        this.tagList = tagList;
//        this.inflater = LayoutInflater.from(context);
//
//        System.out.println("from taglistadapter : " + this.tagList);
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.activity_testpage, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        HashMap<String, String> tagData = tagList.get(position);
//        holder.tagUiiTextView.setText(tagData.get("tagUii"));
//        holder.tagLenTextView.setText(tagData.get("tagLen"));
//        holder.tagCountTextView.setText(tagData.get("tagCount"));
//        holder.tagRssiTextView.setText(tagData.get("tagRssi"));
//
//        System.out.println("from onBindViewHolder : " + tagList);
//    }
//
//    @Override
//    public int getItemCount() {
//        System.out.println("from getItemCount : " + tagList);
//        return tagList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tagUiiTextView;
//        TextView tagLenTextView;
//        TextView tagCountTextView;
//        TextView tagRssiTextView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tagUiiTextView = itemView.findViewById(R.id.tagUii);
////            tagLenTextView = itemView.findViewById(R.id.tagLen);
////            tagCountTextView = itemView.findViewById(R.id.tagCount);
////            tagRssiTextView = itemView.findViewById(R.id.tagRssi);
//        }
//    }
}
