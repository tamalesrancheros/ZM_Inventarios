package com.zapmex.zminventarios;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.zapmex.zminventarios.Entidades.Lecturas_Entidad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */

public class imexFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    /*
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public imexFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment imexFragment.

    // TODO: Rename and change types and number of parameters
    public static imexFragment newInstance(String param1, String param2) {
        imexFragment fragment = new imexFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Button btnExportar;
    Button btnImportar;

    List<Lecturas_Entidad> listaUsuarios = new ArrayList<>();

    ConexionSQLite conn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imex, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnImportar = (Button) getView().findViewById(R.id.btnImportar);
        btnExportar = (Button) getView().findViewById(R.id.btnExportar);

        pedirPermisos();
        obtenerUsuarios();

        btnImportar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                importarCSV();
            }
        });

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exportarCSV();
            }
        });
    }

    // EMPIEZAN LOS METODOS PARA ESTA PARTE DEL FRAGMENT

    //METODO PARA CONSULTAR Y EXTRAER DE LA BASE LOS BARCODES
    // TODO: CAMBIAR EL NOMBRE DEL METODO
    private void obtenerUsuarios() {
        listaUsuarios.clear();

        ConexionSQLite admin = new ConexionSQLite(getActivity(), "bd_inventario", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select barcode from invtBarcode", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do{
                listaUsuarios.add(
                        new Lecturas_Entidad(
                                fila.getString(0)));
            } while (fila.moveToNext());
        } else {
            Toast.makeText(getContext(), "NO HAY REGISTROS", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }


    //METODO PARA EXPORTAR LOS BARCODES A UN ARCHIVO CSV
    private void exportarCSV() {
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/ZAPMEX/CSV");
        String archivo = carpeta.toString() + "/" + "Colectora.csv";

        boolean isCreate = false;
        if(!carpeta.exists()){
            isCreate = carpeta.mkdir();
        }
        try{
            FileWriter fileWriter = new FileWriter(archivo);

            ConexionSQLite admin = new ConexionSQLite(getActivity(), "bd_inventario", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("select barcode from invtBarcode", null);
            if(fila !=null && fila.getCount() !=0){
                fila.moveToFirst();
                do{
                    fileWriter.append(fila.getString(0));
                    fileWriter.append("\n");
                } while (fila.moveToNext());
            } else {
                Toast.makeText(getContext(), "NO HAY REGISTROS", Toast.LENGTH_SHORT).show();
            }

            db.close();
            fileWriter.close();
            Toast.makeText(getContext(), "EXPORTACION EXITOSA DEL CSV", Toast.LENGTH_SHORT).show();

        } catch(Exception e) {

        }
    }

    private void importarCSV(){
        limpiarTablas ("invtFijo");

        File carpeta = new File(Environment.getExternalStorageDirectory() + "/ZAPMEX/SKU");
        String archivo = carpeta.toString() + "/" + "Sku.csv";

        boolean isCreate = false;
        if(!carpeta.exists()) {
            Toast.makeText(getContext(), "NO EXISTE LA CARPETA", Toast.LENGTH_SHORT).show();
        }else {
            String cadena;
            String[] arreglo;

            try {
                FileReader fileReader = new FileReader(archivo);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                while((cadena  = bufferedReader.readLine()) !=null) {
                    arreglo = cadena.split(",");

                    ConexionSQLite admin = new ConexionSQLite(getActivity(), "bd_inventario", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();

                    ContentValues registro = new ContentValues();

                    registro.put("barcodeFijo", arreglo[0]);
                    registro.put("marcaFijo", arreglo[1]);
                    registro.put("estiloFijo", arreglo[2]);
                    registro.put("tallaFijo", arreglo[3]);

                    listaUsuarios.add(
                            new Lecturas_Entidad(
                                    arreglo[0],
                                    arreglo[1],
                                    arreglo[2],
                                    arreglo[3]
                            )
                    );

                    db.insert("invtFijo", null, registro);
                    db.close();

                    Toast.makeText(getContext(), "IMPORTACION EXITOSA DEL CSV", Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) { }
        }
    }

    //METODO PARA PERMISOS DE ALMACENAMIENTO DENTRO DEL TELEFONO
    public void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(
                getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    public void limpiarTablas(String tabla) {
        ConexionSQLite admin = new ConexionSQLite(getActivity(), "bd_inventario", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        admin.borrarRegistros(tabla, db);

        Toast.makeText(getContext(), "SE LIMPIO LOS REGISTROS DE LA TABLA: "+ tabla, Toast.LENGTH_SHORT).show();
    }
}
