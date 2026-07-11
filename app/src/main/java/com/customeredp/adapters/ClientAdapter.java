package com.customeredp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.customeredp.R;
import com.customeredp.models.Client;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Client> clients;
    private OnClientActionListener listener;

    public interface OnClientActionListener {
        void onEdit(Client client);
        void onDelete(Client client);
    }

    public ClientAdapter(List<Client> clients, OnClientActionListener listener) {
        this.clients = clients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = clients.get(position);
        holder.nameText.setText(client.getName());
        holder.emailText.setText(client.getEmail());
        holder.companyText.setText(client.getCompany());

        holder.editButton.setOnClickListener(v -> listener.onEdit(client));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(client));
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public void updateClients(List<Client> newClients) {
        this.clients = newClients;
        notifyDataSetChanged();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText, companyText;
        Button editButton, deleteButton;

        ClientViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.clientNameText);
            emailText = itemView.findViewById(R.id.clientEmailText);
            companyText = itemView.findViewById(R.id.clientCompanyText);
            editButton = itemView.findViewById(R.id.editClientButton);
            deleteButton = itemView.findViewById(R.id.deleteClientButton);
        }
    }
}