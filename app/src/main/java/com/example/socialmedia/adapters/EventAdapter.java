package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.EventLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.ChatActivity;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.Evento;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.EventsProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class EventAdapter extends FirestoreRecyclerAdapter<Evento, EventAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;
    AuthProvider authProvider;
    EventsProvider eventsProvider;

    public EventAdapter(FirestoreRecyclerOptions<Evento> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        this.userProvider = new UserProvider();
        this.authProvider = new AuthProvider();
        this.eventsProvider = new EventsProvider();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Evento evento) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String eventoId = document.getId();

        eventsProvider.getEvento(eventoId).addOnSuccessListener(dc ->{
           if(dc.exists())
           {
               String estatus = dc.getString("estatus");
               holder.txtDescripcion.setText(dc.getString("descripcion"));
               holder.txtEstatus.setText(dc.getString("estatus"));
           }
        });

        holder.viewHolder.setOnClickListener(v ->{
        });
    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtDescripcion, txtEstatus;
        View viewHolder;


        public ViewHolder(View view)
        {
            super(view);
            txtDescripcion = view.findViewById(R.id.txtDescEvento);
            txtEstatus = view.findViewById(R.id.txtEventoEstatus);

            viewHolder = view;
        }


    }
}
