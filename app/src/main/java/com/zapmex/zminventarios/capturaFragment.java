package com.zapmex.zminventarios;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zapmex.zminventarios.Entidades.Lecturas_Entidad;
import com.zapmex.zminventarios.Utilidad.Utilidades;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link capturaFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class capturaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   /*
   *  private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public capturaFragment() {
        // Required empty public constructor
    }

     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment capturaFragment.

    // TODO: Rename and change types and number of parameters
    public static capturaFragment newInstance(String param1, String param2) {
        capturaFragment fragment = new capturaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private EditText txtCB;
    private TextView lblMensaje; // conteo

    private ListView lstLista;
    private ArrayList<Lecturas_Entidad> items_arreglo;  //cambia de String a Lecturas_Entidad
    private ArrayAdapter adaptador_items;

    private Button botAgregar;
    int bandera_enter, bandera_limpiar = 0; // BANDERAS DE PASO PARA EL "ENTER"
    public int vcontador = 0;
    public int valor = 0;

    List<Lecturas_Entidad> listaUsuarios = new ArrayList<>();

    Button btnExportar;

    ConexionSQLite conn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_captura, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtCB = (EditText)getView().findViewById(R.id.txtCodigodeBarras);
        lblMensaje = (TextView)getView().findViewById(R.id.lblMensaje);

        lstLista = (ListView)getView().findViewById(R.id.lstLista);
        botAgregar = (Button)getView().findViewById(R.id.botAgregar);  //BOTON DE AGREGAR - DECLARACION

        final ArrayList<Lecturas_Entidad> items_arreglo = new ArrayList<Lecturas_Entidad>(); //array de barcodes //cambia de String a Lecturas_Entidad


        //PARA DESHABILITAR LA APERTURA AUTOMATICA DEL TECLADO AL INICIAR LA APLICACION - WORKS -
        //txtCB.setInputType(InputType.TYPE_NULL);

        adaptador_items = new ArrayAdapter(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, items_arreglo);
        lstLista.setAdapter(adaptador_items);

        pedirPermisos();

         /* DETALLES DE MOSTRAR AL ABRIR LA APP
                 Metodo para consultar la base / mostrar el contendio ya escaneado
                 NO FUNCIONA PARECE SER UN DETALLE DEL LLENADO DEL LISTVIEW YA QUE, AL PARECER ES UN LLENADO
                 DE STRINGS Y VIENE DE UN INT Y DE UN LONG CORRESPONDIENTES AL CONSECUITVOS Y AL BARCODE.
              // mostrar();
        */


            txtCB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bandera_enter=charSequence.toString().indexOf("nn");
                if (bandera_enter>-1 && bandera_limpiar==0){
                    bandera_limpiar=1;

                    txtCB.setText(txtCB.getText().toString().replace("nn", ""));

                    //Consulta_BD si el barcode esta en la base
                    if (consultar())
                    {
                        int valor1 = 0;
                        long valor2 = 0;

                        valor1=registrarBarcode();
                        valor2=Long.parseLong(txtCB.getText().toString().replace("nn", ""));
                        //añade el barcode al tope del list view
                        // items_arreglo.add(0,new Lecturas_Entidad(valor1, valor2));
                        // lstLista.setAdapter(adaptador_items);
                        // adaptador_items.notifyDataSetChanged();

                        vcontador++;
                        lblMensaje.setText(Integer.toString(vcontador)); //se sube el contador de la lista

                        //Toast.makeText(MainActivity.this, "Barcode Agregado", Toast.LENGTH_LONG).show();
                        limpiar();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "NO ENCONTRADO", Toast.LENGTH_LONG).show();

                        //Reproduce un sonido de alerta cuando no encuentra el BC en la BD
                        MediaPlayer mediaplayer = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.alerta);
                        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.reset();
                                mediaPlayer.release();
                            }
                        });
                        mediaplayer.start();

                        // Alerta de BARCODE NO ENCONTRADO
                        AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                        alerta.setMessage("No se encontro el codigo de barras, para continuar presione OK")
                                .setPositiveButton("   OK   ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //
                                    }
                                });
                        AlertDialog titulo = alerta.create();
                        titulo.setTitle("¡ALERTA!");
                        titulo.show();
                        limpiar();
                    }

                    bandera_enter=0;
                    bandera_limpiar=0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // metodo para borrar un item de la list view
        // Ademas de que borra el BC de la base de acuerdo al ListView Seleccionado
        lstLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                valor = i;
                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                alerta.setMessage("¿Desea borrar este codigo?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Lecturas_Entidad p = items_arreglo.get(valor);

                                //INVOCA Eliminar Usuario.
                                eliminarBarcode(p.getIdentificador());

                                items_arreglo.remove(valor);
                                adaptador_items.notifyDataSetChanged();
                                vcontador--;
                                lblMensaje.setText(Integer.toString(vcontador));

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Eliminar codigo de barras");
                titulo.show();
                return false;
            }
        });

    }

    // EMPIEZAN LOS METODOS PARA ESTA PARTE DEL FRAGMENT

    //METODO PARA PERMISOS DE ALMACENAMIENTO DENTRO DEL TELEFONO
    public void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(
                getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    //METODO para Registrar BC escaneados
    private int registrarBarcode() {

        ConexionSQLite conn=new ConexionSQLite(getActivity(),"bd_inventario",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Utilidades.CAMPO_BARCODE,txtCB.getText().toString());
        //values.put(Utilidades.CAMPO_ID,vcontador);

        Long idResultante=db.insert(Utilidades.TABLA_INVTBARCODE,Utilidades.CAMPO_BARCODE,values);

        //consulta de ultimo añadido
        int ultimo_registro= consultarSql();

        db.close();
        return ultimo_registro;
    }

    //METODO para Verificar si el BC esta en la base de la Tienda
    private boolean  consultar() { //PARA CONSULTAR SI EL CB ES PARTE DE LOS CODIGOS DE BARRA DE LA TIENDA

        ConexionSQLite conn=new ConexionSQLite(getActivity(),"bd_inventario",null,1);

        SQLiteDatabase db=conn.getReadableDatabase();
        String[] parametros={txtCB.getText().toString()};
        String[] campos={Utilidades.CAMPO_BARCODEFIJO};

        try {
            Cursor cursor =db.query(Utilidades.TABLA_INVTFIJO,campos,Utilidades.CAMPO_BARCODEFIJO+"=?",
                    parametros,null,null,null);
            cursor.moveToFirst();
            if (cursor.getCount()>0){
                return true;
            }
            cursor.close();
            return false;
            //txtCodigodeBarras.setText(cursor.getString(0));

        }catch (Exception e){
            //Mensaje de error para el barcode
            Toast.makeText(getActivity().getApplicationContext(),"ERROR EN BARCODE" + " "+ e.getMessage().toString() ,Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //METODO para Eliminar BC de la base de acuerdo al listView
    //TODO: CAMBIAR EL NOMBRE DEL METODO
    private void eliminarBarcode(String identificador) {

        ConexionSQLite conn=new ConexionSQLite(getActivity(),"bd_inventario",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();
        String[] parametros={String.valueOf(identificador)};

        db.delete(Utilidades.TABLA_INVTBARCODE,Utilidades.CAMPO_ID+"=?",parametros);
        Toast.makeText(getActivity().getApplicationContext(),"ELIMINADO DE LA BD",Toast.LENGTH_LONG).show();

        limpiar();
        db.close();
    }

    //METODO para Mostrar BC Registrados en la BD y llevarlos al listView
    private int consultarSql() {  //AÑADIDO es para devolver el id del registro que se añadio

        ConexionSQLite conn=new ConexionSQLite(getActivity(),"bd_inventario",null,1);

        SQLiteDatabase db=conn.getReadableDatabase();
        String[] parametros={};
        int valor =0;
        try {
            Cursor cursor =db.rawQuery("SELECT max ("+Utilidades.CAMPO_ID+") FROM "+Utilidades.TABLA_INVTBARCODE,parametros);
            cursor.moveToFirst();

            valor= Integer.parseInt(cursor.getString(0));
            cursor.close();
            return valor;
        }
        catch (Exception e){
            //Aqui añadir el mensaje de error para el barcode
            Toast.makeText(getActivity().getApplicationContext(),"ERROR CONSULTAR SQL" + " "+ e.getMessage().toString() ,Toast.LENGTH_LONG).show();
        }
        return valor;
    }

    //METODO basico para limpiar el editText
    private void limpiar () {

        // detalle por limpiado del edit text?
        // txtCB.setText("");
        txtCB.getText().clear();
    }

}
