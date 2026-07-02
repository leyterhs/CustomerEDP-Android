package com.customeredp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> users;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int userId);
    }

    public UserAdapter(List<User> users, OnDeleteClickListener listener) {
        this.users = users;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.usernameText.setText(user.getUsername());
        holder.emailText.setText(user.getEmail());
        holder.roleText.setText("Role: " + user.getRole());

        if (user.getUsername().equals("admin")) {
            holder.deleteButton.setEnabled(false);
            holder.deleteButton.setText("Admin");
        } else {
            holder.deleteButton.setEnabled(true);
            holder.deleteButton.setText("Delete");
            holder.deleteButton.setOnClickListener(v ->
                    deleteListener.onDeleteClick(user.getId()));
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<User> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, emailText, roleText;
        Button deleteButton;

        UserViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            emailText = itemView.findViewById(R.id.emailText);
            roleText = itemView.findViewById(R.id.roleText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}