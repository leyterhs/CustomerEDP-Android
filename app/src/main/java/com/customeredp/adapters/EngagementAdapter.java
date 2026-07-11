package com.customeredp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.customeredp.R;
import com.customeredp.models.Engagement;
import java.util.List;

public class EngagementAdapter extends RecyclerView.Adapter<EngagementAdapter.ViewHolder> {

    private List<Engagement> engagements;
    private OnEngagementActionListener listener;

    public interface OnEngagementActionListener {
        void onEdit(Engagement engagement);
        void onDelete(Engagement engagement);
    }

    public EngagementAdapter(List<Engagement> engagements, OnEngagementActionListener listener) {
        this.engagements = engagements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_engagement, parent, false);
        return new ViewHolder(view);
    }

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Engagement engagement = engagements.get(position);
		holder.titleText.setText(engagement.getTitle());
		holder.statusText.setText(engagement.getStatus());
		holder.priorityText.setText(engagement.getPriority());
		holder.dueDateText.setText(engagement.getDueDate());
		
		
		holder.editButton.setVisibility(View.GONE);
		holder.deleteButton.setVisibility(View.GONE);
	}

    @Override
    public int getItemCount() {
        return engagements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, statusText, priorityText, dueDateText;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.engagementTitle);
            statusText = itemView.findViewById(R.id.engagementStatus);
            priorityText = itemView.findViewById(R.id.engagementPriority);
            dueDateText = itemView.findViewById(R.id.engagementDueDate);
            editButton = itemView.findViewById(R.id.editEngagementButton);
            deleteButton = itemView.findViewById(R.id.deleteEngagementButton);
        }
    }
}