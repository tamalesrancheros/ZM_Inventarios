package com.zapmex.zminventarios.Entidades;

public class Lecturas_Entidad {

    private String identificador;
    private String codigoBarras;

    public Lecturas_Entidad(String s2, String s1, String s, String string){

    }

    public Lecturas_Entidad(String string) {
        this.identificador = identificador;
        this.codigoBarras = codigoBarras;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    @Override
    public String toString(){
        return  "ID= " + identificador + " CB= "+ codigoBarras;
    }

}
