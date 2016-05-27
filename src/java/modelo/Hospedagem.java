package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

public class Hospedagem implements Serializable {
    
    private Integer id;
    private String destino;
    private String dataEntrada;
    private String dataSaida;
    private Integer numQuartos;
    private Float preco;
    
    public Hospedagem() {
        id = (new Long((new Date().getTime())/1000).intValue())
                 + (new Random().nextInt(10000));
        dataEntrada = null;
        dataSaida = null;
        numQuartos = 0;
    }
    
    public Hospedagem(Integer i) {
        id = i;
        dataEntrada = null;
        dataSaida = null;
        numQuartos = 0;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Integer getNumQuartos() {
        return numQuartos;
    }

    public void setNumQuartos(Integer numQuartos) {
        this.numQuartos = numQuartos;
    }

    public Float getPreco() {
        return preco;
    }

    public void setPreco(Float preco) {
        this.preco = preco;
    }   
}
