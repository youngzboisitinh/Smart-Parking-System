    package com.example.smartparking;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;

    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.lifecycle.ViewModelProvider;
    import androidx.navigation.Navigation;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.smartparking.Models.TicketCount;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * A simple {@link Fragment} subclass.
     * Use the  factory method to
     * create an instance of this fragment.
     */
    public class TicketFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_ticket, container, false);

            Button addticket = view.findViewById(R.id.btnNewticket);
            addticket.setOnClickListener(v ->{
                Navigation.findNavController(v).navigate(R.id.action_ticketFragment1_to_ticketFragment2);
            });

            return view;
        }
    }