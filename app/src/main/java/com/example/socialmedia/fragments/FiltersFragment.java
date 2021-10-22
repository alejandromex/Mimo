package com.example.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.FiltersActivity;
import com.example.socialmedia.adapters.ChatAdapter;
import com.example.socialmedia.adapters.EventAdapter;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.Evento;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.EventsProvider;
import com.example.socialmedia.providers.NotificationProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class FiltersFragment extends Fragment {

    View mView;
    RecyclerView rvNotifications;
    EventsProvider eventsProvider;
    AuthProvider authProvider;
    ArrayList<String> eventos;
    EventAdapter eventAdapter;

    FloatingActionButton btnAddEventoAleatorio;

    public FiltersFragment() {
    }



//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        rvNotifications = mView.findViewById(R.id.rvNotifications);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(linearLayoutManager);
        eventsProvider = new EventsProvider();
        authProvider = new AuthProvider();
        eventos = new ArrayList<>();
        FillEventos();
        btnAddEventoAleatorio = mView.findViewById(R.id.btnAddEventoAleatorio);
        btnAddEventoAleatorio.setOnClickListener(v ->{
            AddEvento();
        });

        return mView;
    }

    private void FillEventos()
    {
        eventos.add("Invertir Negocio | Oportunidad");
        eventos.add("Comprar Antiguedad | Oportunidad");
        eventos.add("Cena con Jefe | Oportunidad");
        eventos.add("Mejorar computadora | Oportunidad");
        eventos.add("Invertir en curso | Oportunidad");

        eventos.add("Enfermarse | Negativo");
        eventos.add("Clonacion Tarjeta | Negativo");
        eventos.add("Cartera Perdida | Negativo");
        eventos.add("Perder Trabajo | Negativo");
        eventos.add("Llaves dentro del carro | Negativo");
        eventos.add("Neumatico ponchado | Negativo");
        eventos.add("Celular Perdido | Negativo");
        eventos.add("Audifonos Perdidos | Negativo");
        eventos.add("Monito en la rosca de reyes | Negativo");
        eventos.add("Quedarse dormido, No ir al trabajo | Negativo");
    }

    private void AddEvento()
    {

        int randomNum = ThreadLocalRandom.current().nextInt(0, eventos.size());
        String[] eventoString = eventos.get(randomNum).split("\\|");
        Evento evento = new Evento();
        evento.setDescripcion(eventoString[0]);
        evento.setEstatus(eventoString[1]);
        evento.setIdUser(authProvider.GetUid());

        eventsProvider.save(evento).addOnCompleteListener(t ->{
            if(t.isSuccessful())
            {
            }
            else{
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = eventsProvider.getAll(authProvider.GetUid());
        FirestoreRecyclerOptions<Evento> options = new FirestoreRecyclerOptions.Builder<Evento>().setQuery(query, Evento.class).build();
        eventAdapter = new EventAdapter(options, getContext());
        rvNotifications.setAdapter(eventAdapter);
        eventAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        eventAdapter.stopListening();
    }

}