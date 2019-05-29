package com.rightway.concursosino;

public class DatosTransferDTO {

    private String funcion;
    private String pregunta;
    private String siNo;
    private String idTableta;
    private String accion;


    public String getFuncion() {
        return funcion;
    }

    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getSiNo() {
        return siNo;
    }

    public void setSiNo(String siNo) {
        this.siNo = siNo;
    }

    public String getIdTableta() {
        return idTableta;
    }

    public void setIdTableta(String idTableta) {
        this.idTableta = idTableta;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }
}
