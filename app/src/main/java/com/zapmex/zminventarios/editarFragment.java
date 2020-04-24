package com.zapmex.zminventarios;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zapmex.zminventarios.Entidades.Lecturas_Entidad;
import com.zapmex.zminventarios.Utilidad.Utilidades;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class editarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public editarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment editarFragment.

    // TODO: Rename and change types and number of parameters
    public static editarFragment newInstance(String param1, String param2) {
        editarFragment fragment = new editarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
*/

    private ListView lstViewBarcodes;  //ELEMENTO VISUAL DE LA LISTA
    ArrayList<String> listaInformacion; //ARRAY QUE VA A LLEVAR LA INFORMACION
    ArrayList<Lecturas_Entidad> listaGetSet; //ARRAY QUE VA A LLEVAR LOS DATOS DE LA BASE

    ConexionSQLite conn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        conn = new ConexionSQLite(getActivity(), "bd_inventario", null, 1);

        lstViewBarcodes = (ListView)getView().findViewById(R.id.lstViewBarcodes);

        consultarListaBarcodes();

        ArrayAdapter adaptador = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);

        lstViewBarcodes.setAdapter(adaptador);

    }

    private void consultarListaBarcodes () {
        SQLiteDatabase db = conn.getReadableDatabase();

        Lecturas_Entidad lecturas_entidad = null;
        listaGetSet = new ArrayList<Lecturas_Entidad>();

        Cursor cursor = db.rawQuery("SELECT * FROM invtBarcode", null);

        while (cursor.moveToNext()) {
            lecturas_entidad.setIdentificador(cursor.getString(0));
            lecturas_entidad.setCodigoBarras(cursor.getString(1));

            listaGetSet.add(lecturas_entidad);
        }
        obtenerLista();
    }

    private void obtenerLista() {
        listaInformacion = new ArrayList<String>();

        for (int i=0; i<listaGetSet.size(); i++){
            listaInformacion.add(listaGetSet.get(i).getIdentificador()+ " - " + listaGetSet.get(i).getCodigoBarras());
        }
    }
}
